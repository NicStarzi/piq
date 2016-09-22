package edu.udo.piq.components.defaults;

import edu.udo.cs.util.ResizableTable;
import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.components.collections.PTableIndex;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.tools.AbstractPTableModel;
import edu.udo.piq.util.ThrowException;

public class DefaultPTableModel extends AbstractPTableModel implements PTableModel {
	
	private final ResizableTable<Object> table;
	private Object[] header;
	private boolean headerActive;
	
	public DefaultPTableModel() {
		this(3, 3);
	}
	
	public DefaultPTableModel(int columnCount, int rowCount) {
		table = new ResizableTable<>(columnCount, rowCount);
	}
	
	public void setHeaderEnabled(boolean value) {
		if (headerActive != value) {
			headerActive = value;
			fireHeaderStatusChangedEvent();
		}
	}
	
	public boolean isHeaderEnabled() {
		return headerActive;
	}
	
	public void setHeader(int column, Object element) {
		if (header == null) {
			header = new Object[getColumnCount()];
		}
		ThrowException.ifNotWithin(header, column, "column < 0 || column >= getColumnCount()");
		Object oldElement = header[column];
		if (oldElement != element || (oldElement != null 
				&& oldElement.equals(element))) 
		{
			header[column] = element;
			fireHeaderElementChangedEvent(column, oldElement);
		}
	}
	
	public Object getHeader(int column) {
		ThrowException.ifFalse(isHeaderEnabled(), "isHeaderEnabled() == false");
		return header[column];
	}
	
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		PTableIndex tableIndex = asTableIndex(index);
		if (tableIndex.isRowIndex()) {
			int row = tableIndex.getRow();
			table.addRow(row);
		} else {
			int col = tableIndex.getColumn();
			table.addColumn(col);
		}
		fireAddEvent(index, content);
	}
	
	public void remove(PModelIndex index) {
		if (!canRemove(index)) {
			throw new RemoveImpossible(this, index);
		}
		Object oldContent = get(index);
		PTableIndex tableIndex = asTableIndex(index);
		if (tableIndex.isRowIndex()) {
			int row = tableIndex.getRow();
			table.removeRow(row);
		} else {
			int col = tableIndex.getColumn();
			table.removeColumn(col);
		}
		fireRemoveEvent(index, oldContent);
	}
	
	public void set(int column, int row, Object content) {
		table.set(column, row, content);
		fireChangeEvent(new PTableCellIndex(column, row), content);
	}
	
	public Object get(int column, int row) {
		return table.get(column, row);
	}
	
}