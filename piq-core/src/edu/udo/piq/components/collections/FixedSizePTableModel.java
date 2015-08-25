package edu.udo.piq.components.collections;

import edu.udo.piq.tools.AbstractPTableModel;

public class FixedSizePTableModel extends AbstractPTableModel implements PTableModel {
	
	private final Object[] cells;
	private final int cols;
	private final int rows;
	
	public FixedSizePTableModel(int columnCount, int rowCount) {
		cols = columnCount;
		rows = rowCount;
		cells = new Object[cols * rows];
	}
	
	public int getColumnCount() {
		return cols;
	}
	
	public int getRowCount() {
		return rows;
	}
	
	public Object get(PModelIndex index) throws WrongIndexType,
			NullPointerException, IllegalIndex 
	{
//		if (!contains(index)) {
//			throw new IllegalIndex(index);
//		}
		PTableIndex ti = asTableIndex(index);
		int id = id(ti.getColumn(), ti.getRow());
		return cells[id];
	}
	
	protected Object getAndSet(int column, int row, Object newContent) {
		int id = id(column, row);
		Object old = cells[id];
		cells[id] = newContent;
		return old;
	}
	
	protected int id(int col, int row) {
		return col + row * cols;
	}
	
}