package edu.udo.piq.layouts;

import edu.udo.cs.util.ResizableTable;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.table.PTableCellIndex;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PTableLayout3 extends AbstractMapPLayout {
	
	public static final int DEFAULT_COLUMN_WIDTH = 50;
	public static final int DEFAULT_ROW_HEIGHT = 16;
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(1);
	
	private final ResizableTable<PComponent> table = new ResizableTable<>(0, 0);
	private PInsets		insets		= DEFAULT_INSETS;
	private int[]		colWidths	= new int[0];
	private boolean[]	colWidthSet	= new boolean[0];
	private int[]		rowHeights	= new int[0];
	private int			cellGapW	= 1;
	private int			cellGapH	= 1;
	
	public PTableLayout3(PComponent component) {
		super(component);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
//		DefaultPCellComponent cell = (DefaultPCellComponent) child;
//		System.out.println("PTableLayout3.childAdded cnstr="+constraint+", child="+cell.getElement());
		PTableCellIndex index = (PTableCellIndex) data.getConstraint();
		int col = index.getColumn();
		if (!colWidthSet[col]) {
			int childW = getPreferredSizeOf(data.getComponent()).getWidth();
//			System.out.println("check col width="+childW);
			if (colWidths[col] < childW) {
//				System.out.println("set col width="+childW);
				colWidths[col] = childW;
			}
		}
		table.set(col, index.getRow(), data.getComponent());
		invalidate();
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
//		System.out.println("PTableLayout3.childRemoved cnstr="+constraint+", child="+child);
		PTableCellIndex index = (PTableCellIndex) data.getConstraint();
		table.set(index.getColumn(), index.getRow(), null);
		// We do not need to invalidate here because the size of the table
		// is not changed by a removed component
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint != null && constraint instanceof PTableCellIndex) {
			PTableCellIndex tableIdx = (PTableCellIndex) constraint;
			return tableIdx.getColumn() >= 0 && tableIdx.getColumn() < getColumnCount()
					&& tableIdx.getRow() >= 0 && tableIdx.getRow() < getRowCount();
//					&& getChildForConstraint(constraint) == null;
		}
		return false;
	}
	
	public void setColumnWidth(int index, int value) {
		colWidths[index] = value;
		colWidthSet[index] = true;
		invalidate();
	}
	
	public int getColumnWidth(int index) {
		return colWidths[index];
	}
	
	public void unsetColumnWidth(int index) {
		colWidthSet[index] = false;
		colWidths[index] = DEFAULT_COLUMN_WIDTH;
		for (int r = 0; r < getRowCount(); r++) {
			PComponent child = table.get(index, r);
			int childW = getPreferredSizeOf(child).getWidth();
			if (colWidths[index] < childW) {
				colWidths[index] = childW;
			}
		}
		invalidate();
	}
	
	public void addColumn() {
		addColumn(getColumnCount());
	}
	
	public void addColumn(int index) {
//		System.out.println("PTableLayout3.addColumn index="+index);
		table.addColumn(index);
		int colCount = table.getColumnCount();
		int rowCount = getRowCount();
		
		// We need to re-create these
		int[] newColWidths = new int[colCount];
		boolean[] newColWidthSet = new boolean[colCount];
		
		// These columns keep their old index
		for (int col = 0; col < index; col++) {
			newColWidths[col] = colWidths[col];
			newColWidthSet[col] = colWidthSet[col];
		}
		// These columns must all be shifted to the right by 1
		for (int col = index + 1; col < colCount; col++) {
			newColWidths[col] = colWidths[col - 1];
			newColWidthSet[col] = colWidthSet[col - 1];
			
			for (int row = 0; row < rowCount; row++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				// Refresh constraints for children
				PTableCellIndex childIndex = new PTableCellIndex(col, row);
				setChildConstraint(child, childIndex);
			}
		}
		// Set default values for new column
		newColWidths[index] = DEFAULT_COLUMN_WIDTH;
		newColWidthSet[index] = false;
		// Update internal state and invalidate layout
		colWidths = newColWidths;
		colWidthSet = newColWidthSet;
		invalidate();
	}
	
	public void removeColumn(int index) {
		int rowCount = getRowCount();
		// Make sure to call removeChild before removing the child from the model
		for (int row = 0; row < rowCount; row++) {
			removeChild(table.get(index, row));
		}
		table.removeColumn(index);
		int colCount = table.getColumnCount();
		
		// We need to re-create these
		int[] newColWidths = new int[colCount];
		boolean[] newColWidthSet = new boolean[colCount];
		
		// These columns keep their old index
		for (int col = 0; col < index; col++) {
			newColWidths[col] = colWidths[col];
			newColWidthSet[col] = colWidthSet[col];
		}
		// These columns must all be shifted to the left by 1
		for (int col = index; col < colCount; col++) {
			newColWidths[col] = colWidths[col + 1];
			newColWidthSet[col] = colWidthSet[col + 1];
			
			for (int row = 0; row < rowCount; row++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				// Refresh constraints for children
				PTableCellIndex childIndex = new PTableCellIndex(col, row);
				setChildConstraint(child, childIndex);
			}
		}
		// Update internal state and invalidate layout
		colWidths = newColWidths;
		colWidthSet = newColWidthSet;
		invalidate();
	}
	
	public int getRowHeight(int row) {
		return rowHeights[row];
	}
	
	public void addRow() {
		addRow(getRowCount());
	}
	
	public void addRow(int index) {
		table.addRow(index);
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		
		// These rows must all be shifted to the bottom by 1
		for (int row = index + 1; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				// Refresh constraints for children
				PTableCellIndex childIndex = new PTableCellIndex(col, row);
				setChildConstraint(child, childIndex);
			}
		}
		invalidate();
	}
	
	public void removeRow(int index) {
		int colCount = getColumnCount();
		// Make sure to call removeChild before removing the child from the model
		for (int col = 0; col < colCount; col++) {
			removeChild(table.get(col, index));
		}
		table.removeRow(index);
		int rowCount = getRowCount();
		
		// These rows must all be shifted to the top by 1
		for (int row = index; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				// Refresh constraints for children
				PTableCellIndex childIndex = new PTableCellIndex(col, row);
				setChildConstraint(child, childIndex);
			}
		}
		invalidate();
	}
	
	public void removeAllColumnsAndRows() {
		clearChildren();
		table.setSize(0, 0);
		colWidths = new int[0];
		colWidthSet = new boolean[0];
		rowHeights = new int[0];
		invalidate();
	}
	
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	public int getCellGapWidth() {
		return cellGapW;
	}
	
	public int getCellGapHeight() {
		return cellGapH;
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
	
	public PTableCellIndex getIndexAt(int x, int y) {
		int rowIndex = -1;
		int rowFy = 0;
		for (int r = 0; r < getRowCount(); r++) {
			rowFy += rowHeights[r];
			if (rowFy > y) {
				rowIndex = r;
				break;
			}
		}
		int colIndex = -1;
		int colFx = 0;
		for (int c = 0; c < getColumnCount(); c++) {
			colFx += colWidths[c];
			if (colFx > x) {
				colIndex = c;
				break;
			}
		}
		if (rowIndex < 0 || colIndex < 0) {
			return null;
		}
		return new PTableCellIndex(colIndex, rowIndex);
	}
	
	@Override
	protected void onInvalidated() {
		int rowCount = getRowCount();
		int colCount = getColumnCount();
		int gapX = getCellGapWidth();
		int gapY = getCellGapHeight();
		PInsets insets = getInsets();
		
		int prefW = insets.getHorizontal() + gapX * (colCount - 1);
		for (int c = 0; c < colCount; c++) {
			prefW += colWidths[c];
		}
		int prefH = insets.getVertical() + gapY * (rowCount - 1);
		if (rowHeights.length != rowCount) {
			rowHeights = new int[rowCount];
		}
		for (int r = 0; r < getRowCount(); r++) {
			int rowH = 0;
			for (int c = 0; c < colCount; c++) {
				PComponent child = table.get(c, r);
				PSize childPrefSize = getPreferredSizeOf(child);
				int childH = childPrefSize.getHeight();
				if (rowH < childH) {
					rowH = childH;
				}
			}
			rowHeights[r] = rowH;
			prefH += rowH;
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		
		int gapX = getCellGapWidth();
		int gapY = getCellGapHeight();
		
		int compX = x;
		int compY = y;
		for (int r = 0; r < getRowCount(); r++) {
			int rowHeight = rowHeights[r];
			for (int c = 0; c < getColumnCount(); c++) {
				PComponent child = table.get(c, r);
				int childW = colWidths[c];
				
				setChildCellFilled(child, compX, compY, childW, rowHeight);
				compX += childW + gapX;
			}
			compX = x;
			compY += rowHeight + gapY;
		}
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
	}
	
}