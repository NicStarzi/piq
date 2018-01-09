package edu.udo.piq.layouts;

import java.util.Arrays;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PSizedGridLayout extends AbstractMapPLayout {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final int DEFAULT_GAP_BETWEEN_COLUMNS = 4;
	public static final int DEFAULT_GAP_BETWEEN_ROWS = 4;
	
	protected final PComponentLayoutData[] componentGrid;
	protected final SizeUnit[] unitCol;
	protected final SizeUnit[] unitRow;
	protected final int[] sizeValCol;
	protected final int[] sizeValRow;
	protected final int[] sizeCol;
	protected final int[] sizeRow;
	protected final int[] gapCol;
	protected final int[] gapRow;
	protected PInsets insets = DEFAULT_INSETS;
	
	public PSizedGridLayout(PComponent component,
			int numberOfColumns, int numberOfRows)
	{
		super(component);
		componentGrid = new PComponentLayoutData[numberOfColumns * numberOfRows];
		
		unitCol = new SizeUnit[numberOfColumns];
		unitRow = new SizeUnit[numberOfRows];
		sizeValCol = new int[numberOfColumns];
		sizeValRow = new int[numberOfRows];
		sizeCol = new int[numberOfColumns];
		sizeRow = new int[numberOfRows];
		gapCol = new int[numberOfColumns];
		gapRow = new int[numberOfRows];
		Arrays.fill(unitCol, SizeUnit.PIXELS);
		Arrays.fill(unitRow, SizeUnit.PIXELS);
		Arrays.fill(gapCol, DEFAULT_GAP_BETWEEN_COLUMNS);
		Arrays.fill(gapRow, DEFAULT_GAP_BETWEEN_ROWS);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		Object constraint = data.getConstraint();
		PComponent child = data.getComponent();
		if (constraint instanceof String) {
			constraint = new GridConstraint((String) constraint);
			setChildConstraint(child, constraint);
		}
		GridConstraint constr = (GridConstraint) constraint;
		for (int cx = constr.x; cx < constr.x + constr.w; cx++) {
			for (int cy = constr.y; cy < constr.y + constr.h; cy++) {
				componentGrid[cellID(cx, cy)] = getDataFor(child);
			}
		}
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		GridConstraint constr = (GridConstraint) data.getConstraint();
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
		return sizeCol.length;
	}
	
	public int getRowCount() {
		return sizeRow.length;
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
	
	public void setColumnSize(int colIndex, SizeUnit unit, int value) {
		ThrowException.ifNotWithin(unitCol, colIndex,
				"colIndex < 0 || colIndex >= getColumnCount()");
		if (getColumnSizeUnit(colIndex) != unit
				|| getColumnSizeValue(colIndex) != value)
		{
			unitCol[colIndex] = unit;
			sizeValCol[colIndex] = value;
			fireInvalidateEvent();
		}
	}
	
	public SizeUnit getColumnSizeUnit(int colIndex) {
		ThrowException.ifNotWithin(unitCol, colIndex,
				"colIndex < 0 || colIndex >= getColumnCount()");
		return unitCol[colIndex];
	}
	
	public int getColumnSizeValue(int colIndex) {
		ThrowException.ifNotWithin(sizeValCol, colIndex,
				"colIndex < 0 || colIndex >= getColumnCount()");
		return sizeValCol[colIndex];
	}
	
	public void setRowSize(int rowIndex, SizeUnit unit, int value) {
		ThrowException.ifNotWithin(unitRow, rowIndex,
				"rowIndex < 0 || rowIndex >= getRowCount()");
		if (getRowSizeUnit(rowIndex) != unit
				|| getRowSizeValue(rowIndex) != value)
		{
			unitRow[rowIndex] = unit;
			sizeValRow[rowIndex] = value;
			fireInvalidateEvent();
		}
	}
	
	public SizeUnit getRowSizeUnit(int rowIndex) {
		ThrowException.ifNotWithin(unitRow, rowIndex,
				"rowIndex < 0 || rowIndex >= getRowCount()");
		return unitRow[rowIndex];
	}
	
	public int getRowSizeValue(int rowIndex) {
		ThrowException.ifNotWithin(sizeValRow, rowIndex,
				"rowIndex < 0 || rowIndex >= getRowCount()");
		return sizeValRow[rowIndex];
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
		PInsets insets = getInsets();
		PBounds bounds = getOwner().getBoundsWithoutBorder();
		int fx = bounds.getFinalX() - insets.getFromRight();
		int fy = bounds.getFinalY() - insets.getFromBottom();
		int x = bounds.getX() + insets.getFromLeft();
		int y = bounds.getY() + insets.getFromTop();
		
		refreshLayout();
		
		int cellX = x;
		int cellY = y;
		
		for (int cx = 0; cx < getColumnCount(); cx++) {
			cellY = y;
			int colW = sizeCol[cx];
			
			for (int cy = 0; cy < getRowCount(); cy++) {
				int rowH = sizeRow[cy];
				
				PComponentLayoutData data = getCellInternal(cx, cy);
				if (data != null) {
					PComponent child = data.getComponent();
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
						
//						PSize cellPrefSize = getPreferredSizeOf(cell);
//						int cellPrefW = cellPrefSize.getWidth();
//						int cellPrefH = cellPrefSize.getHeight();
//						int childX = constr.alignH.getLeftX(cellX, cellW, cellPrefW);
//						int childY = constr.alignV.getTopY(cellY, cellH, cellPrefH);
//						int childW = constr.alignH.getWidth(cellX, cellW, cellPrefW);
//						int childH = constr.alignV.getHeight(cellY, cellH, cellPrefH);
						
//						System.out.println("prefSize="+cellPrefSize);
//						System.out.println("cellX="+cellX+", cellY="+cellY+", cellW="+cellW+", cellH="+cellH);
//						System.out.println("childX="+childX+", childY="+childY+", childW="+childW+", childH="+childH);
//						System.out.println();
						
//						setChildCellFilled(cell, childX, childY, childW, childH);
						setChildCell(child, cellX, cellY, cellW, cellH, constr.getAlignX(), constr.getAlignY());
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
		for (int colIdx = 0; colIdx < getColumnCount() - 1; colIdx++) {
			totalW -= getGapAfterColumn(colIdx);
		}
		int totalH = ownerBounds.getHeight() - insets.getHeight();
		for (int rowIdx = 0; rowIdx < getRowCount() - 1; rowIdx++) {
			totalH -= getGapAfterRow(rowIdx);
		}
		
		for (int cx = 0; cx < getColumnCount(); cx++) {
			SizeUnit unit = unitCol[cx];
			int value = sizeValCol[cx];
			sizeCol[cx] = unit.getColWidth(totalW, value);
//			System.out.println("sizeCol["+cx+"]="+sizeCol[cx]);
		}
		for (int cy = 0; cy < getRowCount(); cy++) {
			SizeUnit unit = unitRow[cy];
			int value = sizeValRow[cy];
			sizeRow[cy] = unit.getRowHeight(totalH, value);
//			System.out.println("sizeRow["+cy+"]="+sizeRow[cy]);
		}
	}
	
	@Override
	protected void onInvalidated() {
		// Reset row and column sizes first
		Arrays.fill(sizeRow, 0);
		for (int cx = 0; cx < getColumnCount(); cx++) {
			sizeCol[cx] = 0;
			// Refresh row and column sizes (column by column)
			for (int cy = 0; cy < getRowCount(); cy++) {
				PComponentLayoutData data = getCellInternal(cx, cy);
				if (data == null) {
					continue;
				}
				GridConstraint constr = (GridConstraint) data.getConstraint();
				/*
				 * A component can be placed in more than one cell if the width
				 * and/or height of its constraint are larger than 1.
				 * In this case we want to only count the component once so we
				 * filter them here.
				 */
				if (cx != constr.x || cy != constr.y) {
					continue;
				}
				PSize cellPrefSize = getPreferredSizeOf(data.getComponent());
//				System.out.println("cx="+cx+"; comp="+data.getComponent()+"; prefSize="+cellPrefSize+"; constr="+constr);
				int colW = (int) Math.ceil(cellPrefSize.getWidth() / (double) constr.w);
				int rowH = (int) Math.ceil(cellPrefSize.getHeight() / (double) constr.h);
				for (int cellX = 0; cellX < constr.w; cellX++) {
					int colIdx = cx + cellX;
					if (sizeCol[colIdx] < colW) {
						sizeCol[colIdx] = colW;
					}
					
					for (int cellY = 0; cellY < constr.h; cellY++) {
						int rowIdx = cy + cellY;
						if (sizeRow[rowIdx] < rowH) {
							sizeRow[rowIdx] = rowH;
						}
					}
				}
			}
		}
		int minW = 0;
		int minH = 0;
		// Sum up column sizes for preferred width
		for (int cx = 0; cx < getColumnCount(); cx++) {
//			System.out.println("cx="+cx+"; col="+sizeCol[cx]+"; gap="+getGapAfterColumn(cx));
			minW += sizeCol[cx];
			minW += getGapAfterColumn(cx);
		}
		// We take the last gap away since there is no following column
		if (getColumnCount() > 0) {
			minW -= getGapAfterColumn(getColumnCount() - 1);
		}
		// Sum up row sizes for preferred height
		for (int cy = 0; cy < getRowCount(); cy++) {
			minH += sizeRow[cy];
			minH += getGapAfterRow(cy);
		}
		// We take the last gap away since there is no following row
		if (getRowCount() > 0) {
			minH -= getGapAfterRow(getRowCount() - 1);
		}
		PInsets insets = getInsets();
//		System.out.println("minW="+minW+"; insets="+insets.getWidth());
		prefSize.setWidth(minW + insets.getWidth());
		prefSize.setHeight(minH + insets.getHeight());
////		System.out.println("##########################");
////		System.out.println();
	}
	
	public static enum SizeUnit {
		
		PIXELS {
			@Override
			public int getColWidth(int totalSize, int value) {
				return value;
			}
			@Override
			public int getRowHeight(int totalSize, int value) {
				return value;
			}
		},
		PERCENT {
			@Override
			public int getColWidth(int totalSize, int value) {
				return (totalSize * value) / 100;
			}
			@Override
			public int getRowHeight(int totalSize, int value) {
				return (totalSize * value) / 100;
			}
		},
		;
		
		public abstract int getColWidth(int totalSize, int value);
		
		public abstract int getRowHeight(int totalSize, int value);
		
	}
	
}