package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.table.PTableCellIndex;
import edu.udo.piq.components.collections.table.PTableIndex;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public class PGridListLayout extends AbstractMapPLayout {
	
	public static final ListAlignment DEFAULT_LIST_ALIGNMENT = ListAlignment.TOP_TO_BOTTOM;
	public static final AlignmentX DEFAULT_COLUMN_ALIGNMENT_X = AlignmentX.CENTER;
	public static final AlignmentY DEFAULT_COLUMN_ALIGNMENT_Y = AlignmentY.CENTER;
	public static final Growth DEFAULT_COLUMN_GROWTH = Growth.PREFERRED;
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final int DEFAULT_GAP_BETWEEN_COLUMNS = 4;
	public static final int DEFAULT_GAP_BETWEEN_ROWS = 4;
	
	protected final List<PComponent[]> rowList = new ArrayList<>();
	protected final Growth[] colGrowths;
	protected final AlignmentX[] colAlignX;
	protected final AlignmentY[] colAlignY;
	protected final int[] cachedColSizes;
	protected int cachedRowSize;
	protected ListAlignment alignList = DEFAULT_LIST_ALIGNMENT;
	protected PInsets insets = DEFAULT_INSETS;
	protected int gapCols = DEFAULT_GAP_BETWEEN_COLUMNS;
	protected int gapRows = DEFAULT_GAP_BETWEEN_ROWS;
	protected int nextAddIdx = 0;
	
	public PGridListLayout(PComponent owner, int numberOfColumns) {
		this(owner, DEFAULT_LIST_ALIGNMENT, numberOfColumns);
	}
	
	public PGridListLayout(PComponent owner, ListAlignment alignment, int numberOfColumns) {
		super(owner);
		
		colGrowths = new Growth[numberOfColumns];
		Arrays.fill(colGrowths, DEFAULT_COLUMN_GROWTH);
		colAlignX = new AlignmentX[numberOfColumns];
		Arrays.fill(colAlignX, DEFAULT_COLUMN_ALIGNMENT_X);
		colAlignY = new AlignmentY[numberOfColumns];
		Arrays.fill(colAlignY, DEFAULT_COLUMN_ALIGNMENT_Y);
		cachedColSizes = new int[numberOfColumns];
		setListAlignment(alignment);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		int numOfCols = getColumnCount();
		
		PTableCellIndex idx = (PTableCellIndex) data.getConstraint();
		int colIdx, rowIdx;
		if (idx == null) {
			colIdx = nextAddIdx % numOfCols;
			rowIdx = nextAddIdx / numOfCols;
		} else {
			colIdx = idx.getColumn();
			rowIdx = idx.getRow();
		}
		if (rowIdx < getRowCount()) {
			PComponent[] row = rowList.get(rowIdx);
			Throw.ifNotNull(row[colIdx], () -> "row["+colIdx+"] == " + row[colIdx]);
			row[colIdx] = data.getComponent();
		} else {
			for (int i = getRowCount(); i < rowIdx; i++) {
				PComponent[] row = new PComponent[numOfCols];
				rowList.add(row);
			}
			PComponent[] row = new PComponent[numOfCols];
			row[colIdx] = data.getComponent();
			rowList.add(row);
		}
		int addIdx = colIdx + rowIdx * numOfCols;
		if (nextAddIdx <= addIdx) {
			nextAddIdx = addIdx + 1;
		}
		invalidate();
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		onChildRemoved(data.getComponent(), data.getConstraint());
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		PTableCellIndex idx = (PTableCellIndex) constraint;
		int colIdx = idx.getColumn();
		int rowIdx = idx.getRow();
		PComponent[] row = rowList.get(rowIdx);
		row[colIdx] = null;
		if (rowIdx == getRowCount()) {
			int countComps = 0;
			for (PComponent comp : row) {
				if (comp != null) {
					countComps++;
				}
			}
			if (countComps == 0) {
				rowList.remove(rowIdx);
			}
		}
		invalidate();
	}
	
	@Override
	protected void clearAllDataInternal() {
		super.clearAllDataInternal();
		rowList.clear();
		Arrays.fill(cachedColSizes, 0);
		cachedRowSize = 0;
		nextAddIdx = 0;
	}
	
	public int getColumnCount() {
		return colGrowths.length;
	}
	
	public int getRowCount() {
		return rowList.size();
	}
	
	public void setGapBetweenRows(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGapBetweenRows() != value) {
			gapRows = value;
			invalidate();
		}
	}
	
	public int getGapBetweenRows() {
		return gapRows;
	}
	
	public void setGapBetweenColumns(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGapBetweenRows() != value) {
			gapCols = value;
			invalidate();
		}
	}
	
	public int getGapBetweenColumns() {
		return gapCols;
	}
	
	public void setColumnGrowth(Growth value) {
		ThrowException.ifNull(value, "value == null");
		Arrays.fill(colGrowths, value);
		invalidate();
	}
	
	public void setColumnGrowth(int columnIdx, Growth value) {
		ThrowException.ifNull(value, "value == null");
		if (getColumnGrowth(columnIdx) != value) {
			colGrowths[columnIdx] = value;
			invalidate();
		}
	}
	
	public Growth getColumnGrowth(int columnIdx) {
		return colGrowths[columnIdx];
	}
	
	public void setListAlignment(ListAlignment value) {
		ThrowException.ifNull(value, "value == null");
		if (getListAlignment() != value) {
			alignList = value;
			invalidate();
		}
	}
	
	public ListAlignment getListAlignment() {
		return alignList;
	}
	
	public void setColumnAlignmentX(AlignmentX value) {
		ThrowException.ifNull(value, "value == null");
		Arrays.fill(colAlignX, value);
		invalidate();
	}
	
	public void setColumnAlignmentX(int columnIdx, AlignmentX value) {
		ThrowException.ifNull(value, "value == null");
		if (getColumnAlignmentX(columnIdx) != value) {
			colAlignX[columnIdx] = value;
			invalidate();
		}
	}
	
	public AlignmentX getColumnAlignmentX(int columnIdx) {
		return colAlignX[columnIdx];
	}
	
	public void setColumnAlignmentY(AlignmentY value) {
		ThrowException.ifNull(value, "value == null");
		Arrays.fill(colAlignY, value);
		invalidate();
	}
	
	public void setColumnAlignmentY(int columnIdx, AlignmentY value) {
		ThrowException.ifNull(value, "value == null");
		if (getColumnAlignmentY(columnIdx) != value) {
			colAlignY[columnIdx] = value;
			invalidate();
		}
	}
	
	public AlignmentY getColumnAlignmentY(int columnIdx) {
		return colAlignY[columnIdx];
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = new ImmutablePInsets(value);
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public List<PComponent> getChildren(PTableIndex index) {
		return getChildren(index, null);
	}
	
	public List<PComponent> getChildren(PTableIndex index, List<PComponent> result) {
		int colIdx = index.getColumn();
		int rowIdx = index.getRow();
		if (colIdx == -1) {
			int resultSize = getColumnCount();
			if (result == null) {
				result = new ArrayList<>(resultSize);
			}
			for (colIdx = 0; colIdx < resultSize; colIdx++) {
				result.add(getChild(colIdx, rowIdx));
			}
		} else if (rowIdx == -1) {
			int resultSize = getRowCount();
			if (result == null) {
				result = new ArrayList<>(resultSize);
			}
			for (rowIdx = 0; rowIdx < resultSize; rowIdx++) {
				result.add(getChild(colIdx, rowIdx));
			}
		} else {
			PComponent comp = getChild(colIdx, rowIdx);
			if (result == null) {
				return Collections.singletonList(comp);
			}
			result.add(comp);
		}
		return result;
	}
	
	public PComponent getChild(PTableCellIndex index) {
		return getChild(index.getColumn(), index.getRow());
	}
	
	public PComponent getChild(int colIdx, int rowIdx) {
		if (colIdx < 0 || colIdx >= getColumnCount()
				|| rowIdx < 0 || rowIdx >= getRowCount())
		{
			return null;
		}
		return rowList.get(rowIdx)[colIdx];
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		return getChild((PTableCellIndex) constraint);
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint == null || ((constraint instanceof PTableCellIndex) && getChildForConstraint(constraint) == null);
	}
	
	@Override
	protected void onInvalidated() {
		Arrays.fill(cachedColSizes, 0);
		cachedRowSize = 0;
		boolean isHorizontal = getListAlignment().isHorizontal();
		
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		if (colCount == 0 || rowCount == 0) {
			prefSize.set(getInsets());
			return;
		}
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			PComponent[] row = rowList.get(rowIdx);
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				PComponent child = row[colIdx];
				PSize childSize = getPreferredSizeOf(child);
				int childPrefW = childSize.getWidth();
				int childPrefH = childSize.getHeight();
				if (isHorizontal) {
					if (cachedRowSize < childPrefW) {
						cachedRowSize = childPrefW;
					}
					if (cachedColSizes[colIdx] < childPrefH) {
						cachedColSizes[colIdx] = childPrefH;
					}
				} else {
					if (cachedRowSize < childPrefH) {
						cachedRowSize = childPrefH;
					}
					if (cachedColSizes[colIdx] < childPrefW) {
						cachedColSizes[colIdx] = childPrefW;
					}
				}
			}
		}
		int gapCol = getGapBetweenColumns();
		int gapRow = getGapBetweenRows();
		int prefRow = cachedRowSize * rowCount + gapRow * (rowCount - 1);
		int prefCol = gapCol * (colCount - 1);
		for (int colIdx = 0; colIdx < colCount; colIdx++) {
			prefCol += cachedColSizes[colIdx];
		}
		int prefW, prefH;
		if (isHorizontal) {
			prefW = prefRow;
			prefH = prefCol;
		} else {
			prefW = prefCol;
			prefH = prefRow;
		}
		prefW += getInsets().getHorizontal();
		prefH += getInsets().getVertical();
		prefSize.set(prefW, prefH);
	}
	
	protected void growColumns() {
		PBounds ownerBnds = getOwner().getBounds();
		int growTotal;
		if (getListAlignment().isHorizontal()) {
			int bndsSize = ownerBnds.getHeight();
			int prefSize = this.prefSize.getHeight();
			growTotal = bndsSize - prefSize;
		} else {
			int bndsSize = ownerBnds.getWidth();
			int prefSize = this.prefSize.getWidth();
			growTotal = bndsSize - prefSize;
		}
		if (growTotal <= 0) {
			return;
		}
		int colCount = getColumnCount();
		// the indices of the columns that grow
		int[] colsThatGrow = new int[colCount];
		int growIdx = 0;
		for (int colIdx = 0; colIdx < colCount; colIdx++) {
			if (colGrowths[colIdx] == Growth.MAXIMIZE) {
				colsThatGrow[growIdx++] = colIdx;
			}
		}
		int growCount = growIdx;
		if (growCount == 0) {
			return;
		}
		
		int perColGrowth = growTotal / growCount;
		for (int i = 0; i < growCount; i++) {
			cachedColSizes[colsThatGrow[i]] += perColGrowth;
		}
		// also add the remainder to as many columns as possible
		for (int i = 0; i < growTotal % growCount; i++) {
			cachedColSizes[colsThatGrow[i]] += 1;
		}
	}
	
	@Override
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBoundsWithoutBorder();
		int fx = ob.getFinalX() - insets.getFromRight();
		int fy = ob.getFinalY() - insets.getFromBottom();
		int minX = ob.getX() + insets.getFromLeft();
		int minY = ob.getY() + insets.getFromTop();
		int alignedX = minX;
		int alignedY = minY;
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		int prefW = prefSize.getWidth();
		int prefH = prefSize.getHeight();
		
		ListAlignment align = getListAlignment();
		boolean isHorizontal = align.isHorizontal();
		switch (align) {
		case CENTERED_LEFT_TO_RIGHT:
			alignedX = minX + w / 2 - (prefW - insets.getHorizontal()) / 2;
			break;
		case CENTERED_TOP_TO_BOTTOM:
			alignedY = minY + h / 2 - (prefH - insets.getVertical()) / 2;
			break;
		case BOTTOM_TO_TOP:
			alignedY = (ob.getFinalY() - insets.getFromBottom()) - prefH;
			break;
		case RIGHT_TO_LEFT:
			alignedX = (ob.getFinalX() - insets.getFromRight()) - prefW;
			break;
		case LEFT_TO_RIGHT:
		case TOP_TO_BOTTOM:
		default:
		}
		int x = Math.max(alignedX, minX);
		int y = Math.max(alignedY, minY);
		
		// increase column sizes by Growth value
		growColumns();
		
		int cellX = x;
		int cellY = y;
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		int sizeCol;
		int sizeRow = cachedRowSize;
		int gapCol = getGapBetweenColumns();
		int gapRow = getGapBetweenRows();
		AlignmentX alignX;
		AlignmentY alignY;
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			PComponent[] row = rowList.get(rowIdx);
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				sizeCol = cachedColSizes[colIdx];
				alignX = colAlignX[colIdx];
				alignY = colAlignY[colIdx];
				int cellW, cellH;
				if (isHorizontal) {
					cellW = sizeRow;
					cellH = sizeCol;
				} else {
					cellW = sizeCol;
					cellH = sizeRow;
				}
				if (cellX + cellW > fx) {
					cellW = Math.max(0, fx - cellX);
				}
				if (cellY + cellH > fy) {
					cellH = Math.max(0, fy - cellY);
				}
				PComponent child = row[colIdx];
//				PSize childPrefSize = getPreferredSizeOf(child);
//				int childPrefW = childPrefSize.getWidth();
//				int childPrefH = childPrefSize.getHeight();
//				int childX = alignX.getLeftX(cellX, cellW, childPrefW);
//				int childY = alignY.getTopY(cellY, cellH, childPrefH);
//				int childW = alignX.getWidth(cellX, cellW, childPrefW);
//				int childH = alignY.getHeight(cellY, cellH, childPrefH);
//				int childFx = Math.min(childX + childW, fx);
//				int childFy = Math.min(childY + childH, fy);
//				childW = Math.max(childFx - childX, 0);
//				childH = Math.max(childFy - childY, 0);
				
//				setChildCellFilled(child, childX, childY, childW, childH);
				setChildCell(child, cellX, cellY, cellW, cellH, alignX, alignY);
				if (isHorizontal) {
					cellY += cellH + gapCol;
				} else {
					cellX += cellW + gapCol;
				}
			}
			if (isHorizontal) {
				cellX += sizeRow + gapRow;
				cellY = y;
			} else {
				cellX = x;
				cellY += sizeRow + gapRow;
			}
		}
	}
	
}