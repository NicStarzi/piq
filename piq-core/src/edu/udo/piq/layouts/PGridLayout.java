package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PGridLayout extends AbstractMapPLayout {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final Growth DEFAULT_COLUMN_GROWTH = Growth.PREFERRED;
	public static final Growth DEFAULT_ROW_GROWTH = Growth.PREFERRED;
	public static final int DEFAULT_GAP_BETWEEN_COLUMNS = 4;
	public static final int DEFAULT_GAP_BETWEEN_ROWS = 4;
	
	protected final PCompInfo[] componentGrid;
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
		componentGrid = new PCompInfo[numberOfColumns * numberOfRows];
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
	protected void onChildAdded(PComponent child, Object constraint) {
		if (constraint instanceof String) {
			constraint = new GridConstraint((String) constraint);
			setChildConstraint(child, constraint);
		}
		GridConstraint constr = (GridConstraint) constraint;
		for (int cx = constr.x; cx < constr.x + constr.w; cx++) {
			for (int cy = constr.y; cy < constr.y + constr.h; cy++) {
				componentGrid[cellID(cx, cy)] = getInfoFor(child);
			}
		}
	}
	
	@Override
	protected void onChildRemoved(PComponent child, Object constraint) {
		GridConstraint constr = (GridConstraint) constraint;
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
		return getStyleAttribute(ATTRIBUTE_KEY_INSETS, insets);
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
		PCompInfo info = getCellInternal(cx, cy);
		if (info == null) {
			return null;
		}
		return info.getComponent();
	}
	
	protected PCompInfo getCellInternal(int cx, int cy) {
		return componentGrid[cellID(cx, cy)];
	}
	
	private int cellID(int cx, int cy) {
		return cx + cy * getColumnCount();
	}
	
	@Override
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds bounds = getOwner().getBoundsWithoutBorder();
		int x = bounds.getX() + insets.getFromLeft();
		int y = bounds.getY() + insets.getFromTop();
		
		refreshLayout();
		
		int cellX = x;
		int cellY;
		
		for (int cx = 0; cx < getColumnCount(); cx++) {
			cellY = y;
			int colW = sizeCol[cx];
			
			for (int cy = 0; cy < getRowCount(); cy++) {
				int rowH = sizeRow[cy];
//				System.out.println("cy="+cy+"; rowH="+rowH);
				
				PCompInfo info = getCellInternal(cx, cy);
				if (info != null) {
					PComponent cellCmp = info.getComponent();
					GridConstraint constr = (GridConstraint) info.getConstraint();
					
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
						
						PSize cellPrefSize = getPreferredSizeOf(cellCmp);
						int cellPrefW = cellPrefSize.getWidth();
						int cellPrefH = cellPrefSize.getHeight();
						int childX = constr.alignH.getLeftX(cellX, cellW, cellPrefW);
						int childY = constr.alignV.getTopY(cellY, cellH, cellPrefH);
						int childW = constr.alignH.getWidth(cellX, cellW, cellPrefW);
						int childH = constr.alignV.getHeight(cellY, cellH, cellPrefH);
						
//						System.out.println("child="+cellCmp);
//						System.out.println("prefSize="+cellPrefSize);
//						System.out.println("cellX="+cellX+", cellY="+cellY+", cellW="+cellW+", cellH="+cellH);
//						System.out.println("childX="+childX+", childY="+childY+", childW="+childW+", childH="+childH);
//						System.out.println();
						
						setChildBounds(cellCmp, childX, childY, childW, childH);
					}
				}
				cellY += rowH + getGapAfterRow(cy);
			}
			
			cellX += colW + getGapAfterColumn(cx);
		}
	}
	
	protected void refreshLayout() {
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
		
		if (totalW > prefW && countGrowCol > 0) {
			int growW = (totalW - prefW) / countGrowCol;
//			System.out.println("growW="+growW+"; totalW="+totalW+"; w="+w+"; countGrow="+countGrowCol);
			for (int cx = 0; cx < getColumnCount(); cx++) {
				if (growCols[cx] == Growth.MAXIMIZE) {
					sizeCol[cx] += growW;
//					System.out.println("sizeCol["+cx+"]="+sizeCol[cx]);
				}
			}
		}
		if (totalH > prefH && countGrowRow > 0) {
			int growH = (totalH - prefH) / countGrowRow;
			for (int cy = 0; cy < getRowCount(); cy++) {
				if (growRows[cy] == Growth.MAXIMIZE) {
					sizeRow[cy] += growH;
				}
			}
		}
	}
	
	@Override
	protected void onInvalidated() {
		// Reset row and column sizes first
		Arrays.fill(sizeCol, 0);
		Arrays.fill(sizeRow, 0);
		boolean[] isColSet = new boolean[getColumnCount()];
//		boolean[] isRowSet = new boolean[getRowCount()];
//		for (int cx = 0; cx < getColumnCount(); cx++) {
		for (int cy = 0; cy < getRowCount(); cy++) {
//			sizeCol[cx] = 0;
			// Refresh row and column sizes (column by column)
			boolean isRowSet = false;
			for (int cx = 0; cx < getColumnCount(); cx++) {
//			for (int cy = 0; cy < getRowCount(); cy++) {
				PCompInfo info = getCellInternal(cx, cy);
				if (info == null) {
					continue;
				}
				GridConstraint constr = (GridConstraint) info.getConstraint();
				/*
				 * A component can be placed in more than one cell if the width
				 * and/or height of its constraint are larger than 1.
				 * In this case we want to only count the component once so we
				 * filter them here.
				 */
				if (cx != constr.x || cy != constr.y) {
					continue;
				}
				PSize cellPrefSize = getPreferredSizeOf(info.getComponent());
//				System.out.println("cx="+cx+"; comp="+info.getComponent()+"; prefSize="+cellPrefSize+"; constr="+constr);
				if (!isColSet[cx] || constr.w == 1) {
					int cellPrefW = cellPrefSize.getWidth();
					if (!isColSet[cx] || cellPrefW > sizeCol[cx]) {
						sizeCol[cx] = cellPrefW;
//						System.out.println("sizeCol["+cx+"]="+sizeCol[cx]);
					}
					if (constr.w == 1) {
						isColSet[cx] = true;
					}
				}
//				System.out.println("cx="+cx+"; cy="+cy+"; cmp="+info.getComponent()+"; isRowSet="+isRowSet+"; constrH="+constr.h);
				if (!isRowSet || constr.h == 1) {
					int cellPrefH = cellPrefSize.getHeight();
					if (!isRowSet || cellPrefH > sizeRow[cy]) {
						sizeRow[cy] = cellPrefH;
//						System.out.println("sizeRow["+cy+"]="+cellPrefH);
					}
					if (constr.h == 1) {
						isRowSet = true;
					}
				}
//				System.out.println();
			}
		}
		countGrowCol = 0;
		countGrowRow = 0;
		int minW = 0;
		int minH = 0;
		// Sum up column sizes for preferred width
		for (int cx = 0; cx < getColumnCount(); cx++) {
//			System.out.println("cx="+cx+"; col="+sizeCol[cx]+"; gap="+getGapAfterColumn(cx));
			minW += sizeCol[cx];
			minW += getGapAfterColumn(cx);
			// We count this here for later even though it is not needed for determining preferred size
			if (growCols[cx] == Growth.MAXIMIZE) {
				countGrowCol++;
			}
		}
		// We take the last gap away since there is no following column
		if (getColumnCount() > 0) {
			minW -= getGapAfterColumn(getColumnCount() - 1);
		}
		// Sum up row sizes for preferred height
		for (int cy = 0; cy < getRowCount(); cy++) {
			minH += sizeRow[cy];
			minH += getGapAfterRow(cy);
			// We count this here for later even though it is not needed for determining preferred size
			if (growRows[cy] == Growth.MAXIMIZE) {
				countGrowRow++;
			}
		}
		// We take the last gap away since there is no following row
		if (getRowCount() > 0) {
			minH -= getGapAfterRow(getRowCount() - 1);
		}
		PInsets insets = getInsets();
//		System.out.println("minW="+minW+"; insets="+insets.getWidth());
		prefSize.setWidth(minW + insets.getWidth());
		prefSize.setHeight(minH + insets.getHeight());
//		System.out.println("##########################");
//		System.out.println();
	}
	
	public static class GridConstraint {
		
		protected int x, y;
		protected int w = 1, h = 1;
		protected AlignmentX alignH = AlignmentX.CENTER;
		protected AlignmentY alignV = AlignmentY.CENTER;
		
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
		
	}
	
	public static enum Growth {
		PREFERRED,
		MAXIMIZE,
		;
		public static final List<Growth> ALL =
				Collections.unmodifiableList(Arrays.asList(values()));
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
						
						apply(c, codePiece, codeID++);
					}
				} else {
					sb.append(ch);
				}
			}
			if (sb.length() > 0) {
				String codePiece = sb.toString();
				apply(c, codePiece, codeID++);
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
					}
				} catch (NumberFormatException e) {}
			}
			throw new IllegalArgumentException("Can not parse input: "+code);
		}
	}
	
}