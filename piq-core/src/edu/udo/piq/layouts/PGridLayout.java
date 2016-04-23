package edu.udo.piq.layouts;

import java.util.Arrays;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ThrowException;

public class PGridLayout extends AbstractMapPLayout {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final Growth DEFAULT_COLUMN_GROWTH = Growth.PREFERRED;
	public static final Growth DEFAULT_ROW_GROWTH = Growth.PREFERRED;
	public static final int DEFAULT_GAP_BETWEEN_COLUMNS = 4;
	public static final int DEFAULT_GAP_BETWEEN_ROWS = 4;
	
	protected final MutablePSize prefSize = new MutablePSize();
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
	protected boolean valid = false;
	
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
	
	protected void onInvalidated() {
		valid = false;
	}
	
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
		
		valid = false;
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		GridConstraint constr = (GridConstraint) constraint;
		for (int cx = constr.x; cx < constr.x + constr.w; cx++) {
			for (int cy = constr.y; cy < constr.y + constr.h; cy++) {
				componentGrid[cellID(cx, cy)] = null;
			}
		}
		
		valid = false;
	}
	
	public void setInsets(PInsets insets) {
		ThrowException.ifNull(insets, "insets == null");
		this.insets = insets;
		invalidate();
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
	
	public void layOut() {
		PInsets insets = getInsets();
		PBounds bounds = getOwner().getBounds();
		int x = bounds.getX() + insets.getFromLeft();
		int y = bounds.getY() + insets.getFromTop();
		
		
		refreshLayout();
		
//		System.out.println("colCount="+getColumnCount());
//		System.out.println("rowCount="+getRowCount());
		
		int cellX = x;
		int cellY = y;
		
		for (int cx = 0; cx < getColumnCount(); cx++) {
			cellY = y;
			int colW = sizeCol[cx];
			
			for (int cy = 0; cy < getRowCount(); cy++) {
				int rowH = sizeRow[cy];
				
				PCompInfo info = getCellInternal(cx, cy);
				if (info != null) {
					PComponent cell = info.getComponent();
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
						
						PSize cellPrefSize = getPreferredSizeOf(cell);
						int childX = constr.alignH.getX(cellX, cellW, cellPrefSize.getWidth());
						int childY = constr.alignV.getY(cellY, cellH, cellPrefSize.getHeight());
						int childW = constr.alignH.getW(cellW, cellPrefSize.getWidth());
						int childH = constr.alignV.getH(cellH, cellPrefSize.getHeight());
						
//						System.out.println("prefSize="+cellPrefSize);
//						System.out.println("cellX="+cellX+", cellY="+cellY+", cellW="+cellW+", cellH="+cellH);
//						System.out.println("childX="+childX+", childY="+childY+", childW="+childW+", childH="+childH);
//						System.out.println();
						
						setChildBounds(cell, childX, childY, childW, childH);
					}
				}
				cellY += rowH + getGapAfterRow(cy);
			}
			
			cellX += colW + getGapAfterColumn(cx);
		}
	}
	
	public PSize getPreferredSize() {
		if (valid) {
			return prefSize;
		}
		refreshSize();
		return prefSize;
	}
	
	protected void refreshSize() {
		valid = true;
		
		for (int cy = 0; cy < getRowCount(); cy++) {
			sizeRow[cy] = 0;
		}
		for (int cx = 0; cx < getColumnCount(); cx++) {
			sizeCol[cx] = 0;
			for (int cy = 0; cy < getRowCount(); cy++) {
				PCompInfo info = getCellInternal(cx, cy);
				if (info == null) {
					continue;
				}
				GridConstraint constr = (GridConstraint) info.getConstraint();
				if (cx != constr.x || cy != constr.y) {
					continue;
				}
				PSize cellPrefSize = getPreferredSizeOf(info.getComponent());
				int cellPrefW = cellPrefSize.getWidth();
				int cellPrefH = cellPrefSize.getHeight();
				
				if (cellPrefW > sizeCol[cx]) {
					sizeCol[cx] = cellPrefW;
				}
				if (cellPrefH > sizeRow[cy]) {
					sizeRow[cy] = cellPrefH;
				}
			}
		}
		countGrowCol = 0;
		countGrowRow = 0;
		int minW = 0;
		int minH = 0;
		for (int cx = 0; cx < getColumnCount(); cx++) {
			minW += sizeCol[cx];
//			colGrownW[cx] = colGrownW[cx];
			if (growCols[cx] == Growth.MAXIMIZE) {
				countGrowCol++;
			}
		}
		for (int cy = 0; cy < getRowCount(); cy++) {
			minH += sizeRow[cy];
//			rowGrownH[cy] = rowGrownH[cy];
			if (growRows[cy] == Growth.MAXIMIZE) {
				countGrowRow++;
			}
		}
		prefSize.setWidth(minW);
		prefSize.setHeight(minH);
	}
	
	protected void refreshLayout() {
		refreshSize();
		
		PComponent owner = getOwner();
		if (owner == null) {
			return;
		}
		PInsets insets = getInsets();
		PBounds ownerBounds = owner.getBounds();
		if (ownerBounds == null) {
			return;
		}
		int totalW = ownerBounds.getWidth() - insets.getHorizontal();
		int totalH = ownerBounds.getHeight() - insets.getVertical();
		int w = prefSize.getWidth();
		int h = prefSize.getHeight();
		
		if (totalW > w && countGrowCol > 0) {
			int growW = (totalW - w) / countGrowCol;
			for (int cx = 0; cx < getColumnCount(); cx++) {
				if (growCols[cx] == Growth.MAXIMIZE) {
					sizeCol[cx] += growW;
				}
			}
		}
		if (totalH > h && countGrowRow > 0) {
			int growH = (totalH - h) / countGrowRow;
			for (int cy = 0; cy < getRowCount(); cy++) {
				if (growRows[cy] == Growth.MAXIMIZE) {
					sizeRow[cy] += growH;
				}
			}
		}
	}
	
	public static class GridConstraint {
		
		protected int x, y;
		protected int w = 1, h = 1;
		protected AlignH alignH = AlignH.CENTER;
		protected AlignV alignV = AlignV.CENTER;
		
		public GridConstraint() {
		}
		
		public GridConstraint(String encodedAsStr) {
			GridConstraintParser.decodeAndApply(this, encodedAsStr);
		}
		
		public GridConstraint(int x, int y) {
			this(x, y, 1, 1);
		}
		
		public GridConstraint(int x, int y, int width, int height) {
			if (x < 0 || y < 0 || width <= 0 || height <= 0) {
				throw new IllegalArgumentException("x="+x+", y="+y+", width="+width+", height="+height);
			}
			this.x = x;
			this.y = y;
			this.w = width;
			this.h = height;
		}
		
		public GridConstraint x(int value) {
			x = value;	return this;
		}
		
		public GridConstraint y(int value) {
			y = value;	return this;
		}
		
		public GridConstraint w(int value) {
			w = value;	return this;
		}
		
		public GridConstraint h(int value) {
			h = value;	return this;
		}
		
		public GridConstraint alignH(AlignH value) {
			alignH = value;	return this;
		}
		
		public GridConstraint alignV(AlignV value) {
			alignV = value;	return this;
		}
		
	}
	
	public static enum AlignH {
		LEFT,
		RIGHT {
			public int getX(int cellX, int cellW, int prefW) {
				return cellX + cellW - prefW;
			}
		},
		CENTER {
			public int getX(int cellX, int cellW, int prefW) {
				return cellX + cellW / 2 - prefW / 2;
			}
		},
		FILL {
			public int getW(int cellW, int prefW) {
				return cellW;
			}
		},
		;
		public static final AlignH L = LEFT;
		public static final AlignH R = RIGHT;
		public static final AlignH C = CENTER;
		public static final AlignH F = FILL;
		
		private static final AlignH[] ALL = values();
		
		public static AlignH get(String name) {
			if (name.length() == 0) {
				return null;
			}
			if (name.length() == 1) {
				char ch = name.charAt(0);
				for (AlignH elem : ALL) {
					if (elem.name().charAt(0) == ch) {
						return elem;
					}
				}
			} else {
				for (AlignH elem : ALL) {
					if (elem.name().equalsIgnoreCase(name)) {
						return elem;
					}
				}
			}
			return null;
		}
		
		public int getX(int cellX, int cellW, int prefW) {
			return cellX;
		}
		
		public int getW(int cellW, int prefW) {
			return prefW;
		}
	}
	
	public static enum AlignV {
		TOP,
		BOTTOM {
			public int getY(int cellY, int cellH, int prefH) {
				return cellY + cellH - prefH;
			}
		},
		CENTER {
			public int getY(int cellY, int cellH, int prefH) {
				return cellY + cellH / 2 - prefH / 2;
			}
		},
		FILL {
			public int getH(int cellH, int prefH) {
				return cellH;
			}
		},
		;
		public static final AlignV T = TOP;
		public static final AlignV B = BOTTOM;
		public static final AlignV C = CENTER;
		public static final AlignV F = FILL;
		
		private static final AlignV[] ALL = values();
		
		public static AlignV get(String name) {
			if (name.length() == 0) {
				return null;
			}
			if (name.length() == 1) {
				char ch = name.charAt(0);
				for (AlignV elem : ALL) {
					if (elem.name().charAt(0) == ch) {
						return elem;
					}
				}
			} else {
				for (AlignV elem : ALL) {
					if (elem.name().equalsIgnoreCase(name)) {
						return elem;
					}
				}
			}
			return null;
		}
		
		public int getY(int cellY, int cellH, int prefH) {
			return cellY;
		}
		
		public int getH(int cellH, int prefH) {
			return prefH;
		}
	}
	
	public static enum Growth {
		PREFERRED, 
		MAXIMIZE,
		;
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
					AlignH value = AlignH.get(valueAsStr);
					if (value != null) {
						c.alignH(value);
						return;
					}
				} else if (dim == 'V' || dim == 'Y') {
					AlignV value = AlignV.get(valueAsStr);
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