package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PGridLayout extends AbstractMapPLayout {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final Growth DEFAULT_COLUMN_GROWTH = Growth.PREFERRED;
	public static final Growth DEFAULT_ROW_GROWTH = Growth.PREFERRED;
	public static final int DEFAULT_GAP_BETWEEN_COLUMNS = 4;
	public static final int DEFAULT_GAP_BETWEEN_ROWS = 4;
	
	protected final List<PComponentLayoutData> singleCellComps = new ArrayList<>();
	protected final List<PComponentLayoutData> multiCellComps = new ArrayList<>();
	protected final PComponentLayoutData[] componentGrid;
	protected final Growth[] growCols;
	protected final Growth[] growRows;
	protected final int[] sizeCol;
	protected final int[] sizeRow;
	protected final int[] gapCol;
	protected final int[] gapRow;
	protected PInsets insets = DEFAULT_INSETS;
	protected int countGrowCol;
	protected int countGrowRow;
	
	public PGridLayout(PComponent component,
			int numberOfColumns, int numberOfRows)
	{
		super(component);
		componentGrid = new PComponentLayoutData[numberOfColumns * numberOfRows];
		growCols = new Growth[numberOfColumns];
		growRows = new Growth[numberOfRows];
		Arrays.fill(growCols, DEFAULT_COLUMN_GROWTH);
		Arrays.fill(growRows, DEFAULT_ROW_GROWTH);
		
		sizeCol = new int[numberOfColumns];
		sizeRow = new int[numberOfRows];
		gapCol = new int[numberOfColumns];
		gapRow = new int[numberOfRows];
		Arrays.fill(gapCol, DEFAULT_GAP_BETWEEN_COLUMNS);
		Arrays.fill(gapRow, DEFAULT_GAP_BETWEEN_ROWS);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		Object constraint = data.getConstraint();
		if (constraint instanceof String) {
			constraint = new GridConstraint((String) constraint);
			setChildConstraint(data.getComponent(), constraint);
		}
		GridConstraint constr = (GridConstraint) constraint;
		if (constr.w == 1 && constr.h == 1) {
			singleCellComps.add(data);
		} else {
			multiCellComps.add(data);
		}
		for (int cx = constr.x; cx < constr.x + constr.w; cx++) {
			for (int cy = constr.y; cy < constr.y + constr.h; cy++) {
				componentGrid[cellID(cx, cy)] = data;
			}
		}
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		GridConstraint constr = (GridConstraint) data.getConstraint();
		if (constr.w == 1 && constr.h == 1) {
			singleCellComps.remove(data);
		} else {
			multiCellComps.remove(data);
		}
		for (int cx = constr.x; cx < constr.x + constr.w; cx++) {
			for (int cy = constr.y; cy < constr.y + constr.h; cy++) {
				componentGrid[cellID(cx, cy)] = null;
			}
		}
		invalidate();
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = value;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public int getColumnCount() {
		return growCols.length;
	}
	
	public int getRowCount() {
		return growRows.length;
	}
	
	public void setColumnGrowth(Growth value) {
		for (int col = 0; col < getColumnCount(); col++) {
			setColumnGrowth(col, value);
		}
	}
	
	public void setColumnGrowth(int colIndex, Growth value) {
		ThrowException.ifNull(value, "growth == null");
		if (getColumnGrowth(colIndex) != value) {
			growCols[colIndex] = value;
			invalidate();
		}
	}
	
	public Growth getColumnGrowth(int colIndex) {
		ThrowException.ifNotWithin(growCols, colIndex,
				"colIndex < 0 || colIndex >= getColumnCount()");
		return growCols[colIndex];
	}
	
	public void setRowGrowth(Growth value) {
		for (int row = 0; row < getRowCount(); row++) {
			setRowGrowth(row, value);
		}
	}
	
	public void setRowGrowth(int rowIndex, Growth value) {
		ThrowException.ifNull(value, "growth == null");
		if (getRowGrowth(rowIndex) != value) {
			growRows[rowIndex] = value;
			invalidate();
		}
	}
	
	public Growth getRowGrowth(int rowIndex) {
		ThrowException.ifNotWithin(growRows, rowIndex,
				"rowIndex < 0 || rowIndex >= getRowCount()");
		return growRows[rowIndex];
	}
	
	public void setGapAfterColumn(int value) {
		for (int col = 0; col < getColumnCount(); col++) {
			setGapAfterColumn(col, value);
		}
	}
	
	public void setGapAfterColumn(int colIndex, int value) {
		if (getGapAfterColumn(colIndex) != value) {
			gapCol[colIndex] = value;
			invalidate();
		}
	}
	
	public int getGapAfterColumn(int colIndex) {
		ThrowException.ifNotWithin(gapCol, colIndex,
				"colIndex < 0 || colIndex >= getColumnCount()");
		return gapCol[colIndex];
	}
	
	public void setGapAfterRow(int value) {
		for (int row = 0; row < getRowCount(); row++) {
			setGapAfterRow(row, value);
		}
	}
	
	public void setGapAfterRow(int rowIndex, int value) {
		if (getGapAfterRow(rowIndex) != value) {
			gapRow[rowIndex] = value;
			invalidate();
		}
	}
	
	public int getGapAfterRow(int rowIndex) {
		ThrowException.ifNotWithin(gapRow, rowIndex,
				"rowIndex < 0 || rowIndex >= getRowCount()");
		return gapRow[rowIndex];
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint == null) {
			return false;
		}
		GridConstraint gridCons;
		if (constraint instanceof String) {
			try {
				gridCons = new GridConstraint((String) constraint);
			// In case the String can not be parsed to a GridConstraint
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else if (constraint instanceof GridConstraint) {
			gridCons = (GridConstraint) constraint;
		} else {
			return false;
		}
		return isFree(gridCons);
	}
	
	protected boolean isFree(GridConstraint constr) {
		for (int i = 0; i < constr.w; i++) {
			for (int j = 0; j < constr.h; j++) {
				int cellX = constr.x + i;
				int cellY = constr.y + j;
				if (getCell(cellX, cellY) != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	public PComponent getCell(int cx, int cy) {
		ThrowException.ifNotWithin(0, getColumnCount(), cx,
				"cx < 0 || cx >= getColumnCount()");
		ThrowException.ifNotWithin(0, getRowCount(), cy,
				"cy < 0 || cy >= getRowCount()");
		PComponentLayoutData data = getCellInternal(cx, cy);
		if (data == null) {
			return null;
		}
		return data.getComponent();
	}
	
	protected PComponentLayoutData getCellInternal(int cx, int cy) {
		return componentGrid[cellID(cx, cy)];
	}
	
	private int cellID(int cx, int cy) {
		return cx + cy * getColumnCount();
	}
	
	@Override
	protected void layOutInternal() {
//		System.out.println();
//		System.out.println("============================ "+getOwner()+".layOutInternal()");
		PInsets insets = getInsets();
		PBounds bounds = getOwner().getBoundsWithoutBorder();
		int x = bounds.getX() + insets.getFromLeft();
		int y = bounds.getY() + insets.getFromTop();
		int fx = bounds.getFinalX() - insets.getFromRight();
		int fy = bounds.getFinalY() - insets.getFromBottom();
		
		growColumnAndRowSizes();
		
		int cellX = x;
		int cellY;
		for (int cx = 0; cx < getColumnCount(); cx++) {
			cellY = y;
			int colW = sizeCol[cx];
			
			for (int cy = 0; cy < getRowCount(); cy++) {
				int rowH = sizeRow[cy];
//				System.out.println("cy="+cy+"; rowH="+rowH);
				
				PComponentLayoutData data = getCellInternal(cx, cy);
				if (data != null) {
					PComponent cellCmp = data.getComponent();
					GridConstraint constr = (GridConstraint) data.getConstraint();
					
					if (cx == constr.x && cy == constr.y) {
//						System.out.println("cx="+cx+", cy="+cy+", cell="+cell);
						int cellW = colW;
						for (int ox = 1; ox < constr.w; ox++) {
							cellW += sizeCol[cx + ox] + getGapAfterColumn(cx + ox - 1);
						}
						int cellH = rowH;
						for (int oy = 1; oy < constr.h; oy++) {
							cellH += sizeRow[cy + oy] + getGapAfterRow(cy + oy - 1);
						}
						if (cellX + cellW > fx) {
							cellW = Math.max(0, fx - cellX);
						}
						if (cellY + cellH > fy) {
							cellH = Math.max(0, fy - cellY);
						}
						
//						PSize cellPrefSize = getPreferredSizeOf(cellCmp);
//						int cellPrefW = cellPrefSize.getWidth();
//						int cellPrefH = cellPrefSize.getHeight();
//						int childX = constr.alignH.getLeftX(cellX, cellW, cellPrefW);
//						int childY = constr.alignV.getTopY(cellY, cellH, cellPrefH);
//						int childW = constr.alignH.getWidth(cellX, cellW, cellPrefW);
//						int childH = constr.alignV.getHeight(cellY, cellH, cellPrefH);
//						int childFx = Math.min(childX + childW, fx);
//						int childFy = Math.min(childY + childH, fy);
//						childW = Math.max(childFx - childX, 0);
//						childH = Math.max(childFy - childY, 0);
						
//						System.out.println("child="+cellCmp);
//						System.out.println("prefSize="+cellPrefSize);
//						System.out.println("cellX="+cellX+", cellY="+cellY+", cellW="+cellW+", cellH="+cellH);
//						System.out.println("childX="+childX+", childY="+childY+", childW="+childW+", childH="+childH);
//						System.out.println();
						
//						setChildCellFilled(cellCmp, childX, childY, childW, childH);
						setChildCell(cellCmp, cellX, cellY, cellW, cellH, constr.getAlignX(), constr.getAlignY());
					}
				}
				cellY += rowH + getGapAfterRow(cy);
			}
			
			cellX += colW + getGapAfterColumn(cx);
		}
//		System.out.println("============================ "+getOwner()+".OVER");
//		System.out.println();
	}
	
	@Override
	protected void onInvalidated() {
		calculateCellSizes();
		calculatePreferredSize();
	}
	
	protected void growColumnAndRowSizes() {
		PComponent owner = getOwner();
		if (owner == null) {
			return;
		}
		PInsets insets = getInsets();
		PBounds ownerBounds = owner.getBoundsWithoutBorder();
		if (ownerBounds == null) {
			return;
		}
		int totalW = ownerBounds.getWidth() - insets.getWidth();
		int totalH = ownerBounds.getHeight() - insets.getHeight();
		int prefW = prefSize.getWidth() - insets.getWidth();
		int prefH = prefSize.getHeight() - insets.getHeight();
		
//		System.out.println();
//		System.out.println("============================ "+getOwner()+".growColumnAndRowSizes()");
		if (totalW > prefW && countGrowCol > 0) {
			int growW = (totalW - prefW) / countGrowCol;
//			System.out.println("growW="+growW+"; totalW="+totalW+"; prefW="+prefW+"; countGrow="+countGrowCol);
			for (int cx = 0; cx < getColumnCount(); cx++) {
				if (growCols[cx] == Growth.MAXIMIZE) {
					sizeCol[cx] += growW;
//					System.out.println("sizeCol["+cx+"]="+sizeCol[cx]);
				}
			}
		}
		if (totalH > prefH && countGrowRow > 0) {
			int growH = (totalH - prefH) / countGrowRow;
//			System.out.println("growH="+growH+"; totalH="+totalH+"; prefH="+prefH+"; countGrow="+countGrowRow);
			for (int cy = 0; cy < getRowCount(); cy++) {
				if (growRows[cy] == Growth.MAXIMIZE) {
					sizeRow[cy] += growH;
//					System.out.println("sizeRow["+cy+"]="+sizeRow[cy]);
				}
			}
		}
//		System.out.println("============================ "+getOwner()+".OVER");
//		System.out.println();
	}
	
	protected void calculateCellSizes() {
//		System.out.println();
//		System.out.println("============================ "+getOwner()+".calculateCellSizes()");
		// Reset row and column sizes first
		Arrays.fill(sizeCol, 0);
		Arrays.fill(sizeRow, 0);
		for (PComponentLayoutData data : singleCellComps) {
//			System.out.println("singleCellComp="+data.getComponent()+" at "+data.getConstraint());
			GridConstraint constr = (GridConstraint) data.getConstraint();
			PSize cellPrefSize = getPreferredSizeOf(data.getComponent());
			int cellPrefW = cellPrefSize.getWidth();
			if (cellPrefW > sizeCol[constr.x]) {
				sizeCol[constr.x] = cellPrefW;
//				System.out.println("set:sizeCol["+constr.x+"]="+sizeCol[constr.x]);
			}
			int cellPrefH = cellPrefSize.getHeight();
			if (cellPrefH > sizeRow[constr.y]) {
				sizeRow[constr.y] = cellPrefH;
//				System.out.println("set:sizeRow["+constr.y+"]="+cellPrefH);
			}
		}
		for (PComponentLayoutData data : multiCellComps) {
//			System.out.println("multiCellComp="+data.getComponent()+" at "+data.getConstraint());
			GridConstraint constr = (GridConstraint) data.getConstraint();
			PSize compPrefSize = getPreferredSizeOf(data.getComponent());
			int compW = compPrefSize.getWidth();
			int compH = compPrefSize.getHeight();
			int fCx = constr.x + constr.w;
			int fCy = constr.y + constr.h;
			int countGrowCols = 0;
			int countGrowRows = 0;
			for (int cx = constr.x; cx < fCx; cx++) {
				if (getColumnGrowth(cx) == Growth.MAXIMIZE) {
					countGrowCols++;
				}
				compW -= sizeCol[cx];
			}
			for (int cy = constr.y; cy < fCy; cy++) {
				if (getRowGrowth(cy) == Growth.MAXIMIZE) {
					countGrowRows++;
				}
				compH -= sizeRow[cy];
			}
//			System.out.println("compW="+compW+"; was="+compPrefSize.getWidth()+"; countGrowCols="+countGrowCols);
//			System.out.println("compH="+compH+"; was="+compPrefSize.getHeight()+"; countGrowRows="+countGrowRows);
			if (compW > 0 && countGrowCols == 0) {
				int growthCol = compW / constr.w;
				int overhang = compW % constr.w;
				for (int cx = constr.x; cx < fCx; cx++) {
					sizeCol[cx] += growthCol;
					if (cx < overhang) {
						sizeCol[cx] += 1;
					}
//					System.out.println("growBy="+growthCol+"; sizeCol["+cx+"]="+sizeCol[cx]);
				}
			}
			if (compH > 0 && countGrowRows == 0) {
				int growthRow = compH / constr.h;
				int overhang = compH % constr.h;
				for (int cy = constr.y; cy < fCy; cy++) {
					sizeRow[cy] += growthRow;
					if (cy < overhang) {
						sizeRow[cy] += 1;
					}
//					System.out.println("growBy="+growthRow+"; sizeRow["+cy+"]="+sizeRow[cy]);
				}
			}
		}
//		System.out.println("============================ "+getOwner()+".OVER");
//		System.out.println();
	}
	
	protected void calculatePreferredSize() {
		countGrowCol = 0;
		countGrowRow = 0;
		int minW = 0;
		int minH = 0;
		int colCount = getColumnCount();
		// Sum up column sizes for preferred width
		for (int cx = 0; cx < colCount; cx++) {
//			System.out.println("cx="+cx+"; col="+sizeCol[cx]+"; gap="+getGapAfterColumn(cx));
			minW += sizeCol[cx];
			minW += getGapAfterColumn(cx);
			// We count this here for later even though it is not needed for determining preferred size
			if (growCols[cx] == Growth.MAXIMIZE) {
				countGrowCol++;
			}
		}
		// We take the last gap away since there is no following column
		if (colCount > 0) {
			minW -= getGapAfterColumn(colCount - 1);
		}
		int rowCount = getRowCount();
		// Sum up row sizes for preferred height
		for (int cy = 0; cy < rowCount; cy++) {
			minH += sizeRow[cy];
			minH += getGapAfterRow(cy);
			// We count this here for later even though it is not needed for determining preferred size
			if (growRows[cy] == Growth.MAXIMIZE) {
				countGrowRow++;
			}
		}
		// We take the last gap away since there is no following row
		if (rowCount > 0) {
			minH -= getGapAfterRow(rowCount - 1);
		}
		PInsets insets = getInsets();
//		System.out.println("minW="+minW+"; insets="+insets.getWidth());
		prefSize.setWidth(minW + insets.getWidth());
		prefSize.setHeight(minH + insets.getHeight());
	}
	
	public static class GridConstraint {
		
		protected int x, y;
		protected int w = 1, h = 1;
		protected AlignmentX alignH = AlignmentX.PREFERRED_OR_CENTER;
		protected AlignmentY alignV = AlignmentY.PREFERRED_OR_CENTER;
		
		public GridConstraint() {
		}
		
		public GridConstraint(String encodedAsStr) {
			GridConstraintParser.decodeAndApply(this, encodedAsStr);
		}
		
		public GridConstraint(int x, int y) {
			this(x, y, 1, 1);
		}
		
		public GridConstraint(int x, int y, int width, int height) {
			this(x, y, width, height, AlignmentX.CENTER, AlignmentY.CENTER);
		}
		
		public GridConstraint(int x, int y, AlignmentX alignH, AlignmentY alignV) {
			this(x, y, 1, 1, alignH, alignV);
		}
		
		public GridConstraint(int x, int y, int width, int height, AlignmentX alignH, AlignmentY alignV) {
			if (x < 0 || y < 0 || width <= 0 || height <= 0) {
				throw new IllegalArgumentException("x="+x+", y="+y+", width="+width+", height="+height);
			}
			ThrowException.ifNull(alignH, "alignH == null");
			ThrowException.ifNull(alignV, "alignV == null");
			this.x = x;
			this.y = y;
			w = width;
			h = height;
			this.alignH = alignH;
			this.alignV = alignV;
		}
		
		public GridConstraint x(int value) {
			x = value;	return this;
		}
		
		public int getX() {
			return x;
		}
		
		public GridConstraint y(int value) {
			y = value;	return this;
		}
		
		public int getY() {
			return y;
		}
		
		public GridConstraint w(int value) {
			w = value;	return this;
		}
		
		public int getWidth() {
			return getW();
		}
		
		public int getW() {
			return w;
		}
		
		public GridConstraint h(int value) {
			h = value;	return this;
		}
		
		public int getHeight() {
			return getH();
		}
		
		public int getH() {
			return h;
		}
		
		public GridConstraint alignX(AlignmentX value) {
			alignH = value;	return this;
		}
		
		public AlignmentX getAlignX() {
			return alignH;
		}
		
		public GridConstraint alignY(AlignmentY value) {
			alignV = value;	return this;
		}
		
		public AlignmentY getAlignY() {
			return alignV;
		}
		
		public GridConstraint alignH(AlignmentX value) {
			return alignX(value);
		}
		
		public AlignmentX getAlignH() {
			return getAlignX();
		}
		
		public GridConstraint alignV(AlignmentY value) {
			return alignY(value);
		}
		
		public AlignmentY getAlignV() {
			return getAlignY();
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(getClass().getSimpleName());
			builder.append(" [x=");
			builder.append(x);
			builder.append(", y=");
			builder.append(y);
			builder.append(", width=");
			builder.append(w);
			builder.append(", height=");
			builder.append(h);
			builder.append(", alignX=");
			builder.append(alignH);
			builder.append(", alignY=");
			builder.append(alignV);
			builder.append("]");
			return builder.toString();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			return (((prime + x) * prime + y) * prime + w) * prime + h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof GridConstraint)) {
				return false;
			}
			GridConstraint other = (GridConstraint) obj;
			return x == other.x && y == other.y && w == other.w && h == other.h;
		}
		
	}
	
	public static enum Growth {
		PREFERRED,
		MAXIMIZE,
		;
		public static final List<Growth> ALL =
				Collections.unmodifiableList(Arrays.asList(Growth.values()));
		public static final int COUNT = ALL.size();
	}
	
	public static class GridConstraintParser {
		
		public static void decodeAndApply(GridConstraint c, String code) {
			StringBuilder sb = new StringBuilder();
			int codeID = 0;
			for (int i = 0; i < code.length(); i++) {
				char ch = code.charAt(i);
				if (Character.isWhitespace(ch)) {
					if (sb.length() > 0) {
						String codePiece = sb.toString();
						sb.delete(0, sb.length());
						
						GridConstraintParser.apply(c, codePiece, codeID++);
					}
				} else {
					sb.append(ch);
				}
			}
			if (sb.length() > 0) {
				String codePiece = sb.toString();
				GridConstraintParser.apply(c, codePiece, codeID++);
			}
		}
		
		protected static void apply(GridConstraint c, String code, int codeID) {
			code = code.toUpperCase();
			if (code.length() > "alignX=".length() && code.startsWith("ALIGN")) {
				String valueAsStr = code.substring("alignX=".length());
				char dim = code.charAt("align".length());
				if (dim == 'H' || dim == 'X') {
					AlignmentX value = AlignmentX.getByName(valueAsStr);
					if (value != null) {
						c.alignH(value);
						return;
					}
				} else if (dim == 'V' || dim == 'Y') {
					AlignmentY value = AlignmentY.getByName(valueAsStr);
					if (value != null) {
						c.alignV(value);
						return;
					}
				}
			} else {
				try {
					int value = Integer.parseInt(code);
					switch (codeID) {
						case 0: c.x(value); return;
						case 1: c.y(value); return;
						case 2: c.w(value); return;
						case 3: c.h(value); return;
						default: throw new IllegalArgumentException("codeID="+codeID);
					}
				} catch (NumberFormatException e) {}
			}
			throw new IllegalArgumentException("Can not parse input: "+code);
		}
	}
	
}