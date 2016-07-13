package edu.udo.piq.layouts;

import edu.udo.cs.util.ResizableTable;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PTableLayout3 extends AbstractMapPLayout {
	
	public static final int DEFAULT_COLUMN_WIDTH = 50;
	public static final int DEFAULT_ROW_HEIGHT = 16;
	
	protected final MutablePSize prefSize = new MutablePSize();
	private final ResizableTable<PComponent> table = new ResizableTable<>(0, 0);
	private int[]		colWidths	= new int[0];
	private boolean[]	colWidthSet	= new boolean[0];
	private int[]		rowHeights	= new int[0];
	private int cellGapW = 1;
	private int cellGapH = 1;
	
	static enum Mode {
		INV,
		LAY,
		GPS,
		;
	}
	Mode m;
	int c;
	private void push(Mode mode) {
		if (m == mode) {
			c++;
		} else {
			m = mode;
			c = 1;
		}
		System.out.println(m+" ("+c+")");
	}
	
	protected void onInvalidated() {
		push(Mode.INV);//FIXME
	}
	
	public PTableLayout3(PComponent component) {
		super(component);
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
//		DefaultPCellComponent cell = (DefaultPCellComponent) child;
//		System.out.println("PTableLayout3.childAdded cnstr="+constraint+", child="+cell.getElement());
		PTableCellIndex index = (PTableCellIndex) constraint;
		int col = index.getColumn();
		if (!colWidthSet[col]) {
			int childW = getPreferredSizeOf(child).getWidth();
//			System.out.println("check col width="+childW);
			if (colWidths[col] < childW) {
//				System.out.println("set col width="+childW);
				colWidths[col] = childW;
			}
		}
		table.set(col, index.getRow(), child);
		onInvalidated();
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
//		System.out.println("PTableLayout3.childRemoved cnstr="+constraint+", child="+child);
		PTableCellIndex index = (PTableCellIndex) constraint;
		table.set(index.getColumn(), index.getRow(), null);
	}
	
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
			
			// Refresh constraints for children
			for (int row = 0; row < rowCount; row++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				setChildConstraint(child, new PTableCellIndex(col, row));
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
//		System.out.println("PTableLayout3.removeColumn index="+index);
		int rowCount = getRowCount();
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
		// These columns must all be shifted to the right by 1
		for (int col = index; col < colCount; col++) {
			newColWidths[col] = colWidths[col + 1];
			newColWidthSet[col] = colWidthSet[col + 1];
			
			// Refresh constraints for children
			for (int row = 0; row < rowCount; row++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
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
//		System.out.println("PTableLayout3.addColumn index="+index);
		table.addRow(index);
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		
		// We need to re-create this
		int[] newRowHeights = new int[rowCount];
		
		// These columns keep their old index
		for (int row = 0; row < index; row++) {
			newRowHeights[row] = rowHeights[row];
		}
		// These columns must all be shifted to the right by 1
		for (int row = index + 1; row < rowCount; row++) {
			newRowHeights[row] = rowHeights[row - 1];
			
			// Refresh constraints for children
			for (int col = 0; col < colCount; col++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				setChildConstraint(child, new PTableCellIndex(col, row));
			}
		}
		// Set default values for new column
		newRowHeights[index] = DEFAULT_ROW_HEIGHT;
		// Update internal state and invalidate layout
		rowHeights = newRowHeights;
		invalidate();
	}
	
	public void removeRow(int index) {
//		System.out.println("PTableLayout3.removeColumn index="+index);
		int colCount = getColumnCount();
		for (int col = 0; col < colCount; col++) {
			removeChild(table.get(col, index));
		}
		table.removeRow(index);
		int rowCount = getRowCount();
		
		// We need to re-create this
		int[] newRowHeights = new int[rowCount];
		
		// These columns keep their old index
		for (int row = 0; row < index; row++) {
			newRowHeights[row] = rowHeights[row];
		}
		// These columns must all be shifted to the right by 1
		for (int row = index; row < rowCount; row++) {
			newRowHeights[row] = rowHeights[row + 1];
			
			// Refresh constraints for children
			for (int col = 0; col < colCount; col++) {
				PComponent child = table.get(col, row);
				if (child == null) {
					continue;
				}
				PTableCellIndex childIndex = new PTableCellIndex(col, row);
				setChildConstraint(child, childIndex);
			}
		}
		// Update internal state and invalidate layout
		rowHeights = newRowHeights;
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
	
	public void layOut() {
		push(Mode.LAY);//FIXME
		
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		
		int compX = x;
		int compY = y;
		for (int r = 0; r < getRowCount(); r++) {
			int rowH = 0;
			for (int c = 0; c < getColumnCount(); c++) {
				PComponent child = table.get(c, r);
				PSize childPrefSize = getPreferredSizeOf(child);
				int childW = colWidths[c];
				int childH = childPrefSize.getHeight();
				
				setChildBounds(child, compX, compY, childW, childH);
				if (rowH < childH) {
					rowH = childH;
				}
				compX += childW + cellGapW;
			}
			rowHeights[r] = rowH;
			compX = x;
			compY += rowH + cellGapH;
		}
	}
	
	public PSize getPreferredSize() {
		push(Mode.GPS);//FIXME
		
		int prefW = cellGapW * (getColumnCount() - 1);
		for (int c = 0; c < getColumnCount(); c++) {
			prefW += colWidths[c];
		}
		int prefH = cellGapH * (getRowCount() - 1);
		for (int r = 0; r < getRowCount(); r++) {
			int rowH = 0;
			for (int c = 0; c < getColumnCount(); c++) {
				PComponent child = table.get(c, r);
				PSize childPrefSize = getPreferredSizeOf(child);
				int childH = childPrefSize.getHeight();
				if (rowH < childH) {
					rowH = childH;
				}
			}
			prefH += rowH;
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public void onChildPrefSizeChanged(PComponent child) {
	}
	
}