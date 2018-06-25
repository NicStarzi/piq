package edu.udo.piq.components.defaults;

import edu.udo.cs.util.ResizableTable;
import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.table.PTableCellIndex;
import edu.udo.piq.components.collections.table.PTableIndex;
import edu.udo.piq.tools.AbstractPTableModel;
import edu.udo.piq.util.ThrowException;

public class DefaultPTableModel extends AbstractPTableModel implements PTableModel {
	
	protected final ResizableTable<Object> table;
	protected Object[] header;
	protected boolean headerActive;
	protected int size;
	
	public DefaultPTableModel() {
		this(3, 3);
	}
	
	public DefaultPTableModel(int columnCount, int rowCount) {
		table = new ResizableTable<>(columnCount, rowCount);
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public void setHeaderEnabled(boolean value) {
		if (headerActive != value) {
			headerActive = value;
			fireHeaderStatusChangedEvent();
		}
	}
	
	@Override
	public boolean isHeaderEnabled() {
		return headerActive;
	}
	
	@Override
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
	
	@Override
	public Object getHeader(int column) {
		ThrowException.ifFalse(isHeaderEnabled(), "isHeaderEnabled() == false");
		return header[column];
	}
	
	@Override
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	@Override
	public int getRowCount() {
		return table.getRowCount();
	}
	
	@Override
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
		size++;
		fireAddEvent(index, content);
	}
	
	@Override
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
		size--;
		fireRemoveEvent(index, oldContent);
	}
	
	@Override
	public void set(int column, int row, Object content) {
		table.set(column, row, content);
		fireChangeEvent(new PTableCellIndex(column, row), content);
	}
	
	@Override
	public Object get(int column, int row) {
		return table.get(column, row);
	}
	
}