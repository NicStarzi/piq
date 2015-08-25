package edu.udo.piq.components.collections;

public interface PTableModel extends PModel {
	
	public int getColumnCount();
	
	public int getRowCount();
	
	public default PTableIndex getIndexOf(Object content) {
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		for (int c = 0; c < colCount; c++) {
			for (int r = 0; r < rowCount; r++) {
				if (content.equals(get(c, r))) {
					return new PTableIndex(c, r);
				}
			}
		}
		return null;
	}
	
	public default Object get(int column, int row) {
		return get(new PTableIndex(column, row));
	}
	
	public default boolean contains(PModelIndex index) {
		PTableIndex ti = asTableIndex(index);
		int column = ti.getColumn();
		int row = ti.getRow();
		return withinBounds(column, row) && get(index) != null;
	}
	
	public default boolean contains(int column, int row) {
		return withinBounds(column, row) && get(column, row) != null;
	}
	
	public default boolean canAdd(PModelIndex index, Object content) {
		PTableIndex ti = asTableIndex(index);
		int column = ti.getColumn();
		int row = ti.getRow();
		return content != null && withinBounds(column, row);
	}
	
	public default boolean canAdd(int column, int row, Object content) {
		return content != null && withinBounds(column, row);
	}
	
	public default void add(int column, int row, Object content) {
		add(new PTableIndex(column, row), content);
	}
	
	public default boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	public default boolean canRemove(int column, int row) {
		return contains(column, row);
	}
	
	public default void remove(int column, int row) {
		remove(new PTableIndex(column, row));
	}
	
	public default boolean withinBounds(int column, int row) {
		return column >= 0 && column < getColumnCount() 
				&& row >= 0 && row < getRowCount();
	}
	
	public default PTableIndex asTableIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTableIndex) {
			return (PTableIndex) index;
		}
		throw new WrongIndexType(index, PTableIndex.class);
	}
	
}