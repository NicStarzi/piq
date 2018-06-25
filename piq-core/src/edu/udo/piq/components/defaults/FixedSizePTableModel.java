package edu.udo.piq.components.defaults;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.table.PTableCellIndex;
import edu.udo.piq.tools.AbstractPTableModel;
import edu.udo.piq.util.ThrowException;

public class FixedSizePTableModel extends AbstractPTableModel implements PTableModel {
	
	protected final Object[] cells;
	protected final int cols;
	protected final int rows;
	protected Object[] header;
	protected boolean headerActive;
	
	public FixedSizePTableModel(int columnCount, int rowCount) {
		cols = columnCount;
		rows = rowCount;
		cells = new Object[cols * rows];
	}
	
	@Override
	public int getSize() {
		return cols * rows;
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
		ThrowException.ifNotWithin(header, column, "column < 0 || column >= getColumnCount()");
		if (header == null) {
			header = new Object[getColumnCount()];
		}
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
		if (!isHeaderEnabled()) {
			throw new IllegalStateException("hasHeader() == false");
		}
		return header[column];
	}
	
	@Override
	public int getColumnCount() {
		return cols;
	}
	
	@Override
	public int getRowCount() {
		return rows;
	}
	
	@Override
	public boolean canSet(PModelIndex index, Object content) {
		return index instanceof PTableCellIndex && contains(index);
	}
	
	@Override
	public boolean canAdd(PModelIndex index, Object content) {
		return false;
	}
	
	public boolean canAdd(int column, int row, Object content) {
		return false;
	}
	
	@Override
	public boolean canRemove(PModelIndex index) {
		return false;
	}
	
	public boolean canRemove(int column, int row) {
		return false;
	}
	
	@Override
	public void add(PModelIndex index, Object content) {
		throw new AddImpossible(this, index, content);
	}
	
	@Override
	public void remove(PModelIndex index) {
		throw new RemoveImpossible(this, index);
	}
	
	@Override
	public void set(int column, int row, Object content) {
		int id = id(column, row);
		Object old = cells[id];
		if (old != content || (old != null && old.equals(content))) {
			cells[id] = content;
			fireChangeEvent(new PTableCellIndex(column, row), old);
		}
	}
	
	@Override
	public Object get(int column, int row) {
		return cells[id(column, row)];
	}
	
	protected int id(int col, int row) {
		return col + row * cols;
	}
	
}