package edu.udo.piq.components.defaults;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.tools.AbstractPTableModel;
import edu.udo.piq.util.ThrowException;

public class FixedSizePTableModel extends AbstractPTableModel implements PTableModel {
	
	private final Object[] cells;
	private final int cols;
	private final int rows;
	private Object[] header;
	private boolean headerActive;
	
	public FixedSizePTableModel(int columnCount, int rowCount) {
		cols = columnCount;
		rows = rowCount;
		cells = new Object[cols * rows];
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
	
	public Object getHeader(int column) {
		if (!isHeaderEnabled()) {
			throw new IllegalStateException("hasHeader() == false");
		}
		return header[column];
	}
	
	public int getColumnCount() {
		return cols;
	}
	
	public int getRowCount() {
		return rows;
	}
	
	public boolean canSet(PModelIndex index, Object content) {
		return index instanceof PTableCellIndex && contains(index);
	}
	
	public boolean canAdd(PModelIndex index, Object content) {
		return false;
	}
	
	public boolean canAdd(int column, int row, Object content) {
		return false;
	}
	
	public boolean canRemove(PModelIndex index) {
		return false;
	}
	
	public boolean canRemove(int column, int row) {
		return false;
	}
	
	public void add(PModelIndex index, Object content) {
		throw new AddImpossible(this, index, content);
	}
	
	public void remove(PModelIndex index) {
		throw new RemoveImpossible(this, index);
	}
	
	public void set(int column, int row, Object content) {
		int id = id(column, row);
		Object old = cells[id];
		if (old != content || (old != null && old.equals(content))) {
			cells[id] = content;
			fireChangeEvent(new PTableCellIndex(column, row), old);
		}
	}
	
	public Object get(int column, int row) {
		return cells[id(column, row)];
	}
	
	protected int id(int col, int row) {
		return col + row * cols;
	}
	
}