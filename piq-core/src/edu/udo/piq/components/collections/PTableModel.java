package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.util.ThrowException;

public interface PTableModel extends PModel {
	
	public void setHeaderEnabled(boolean value);
	
	public boolean isHeaderEnabled();
	
	public void setHeader(int column, Object element);
	
	public Object getHeader(int column);
	
	public int getColumnCount();
	
	public int getRowCount();
	
	public void set(int column, int row, Object content);
	
	public default void set(PModelIndex index, Object content) {
		PTableCellIndex ti = asTableCellIndex(index);
		int column = ti.getColumn();
		int row = ti.getRow();
		set(column, row, content);
	}
	
	public default PTableCellIndex getIndexOf(Object content) {
		int colCount = getColumnCount();
		int rowCount = getRowCount();
		for (int c = 0; c < colCount; c++) {
			for (int r = 0; r < rowCount; r++) {
				if (content.equals(get(c, r))) {
					return new PTableCellIndex(c, r);
				}
			}
		}
		return null;
	}
	
	public default Object get(PColumnIndex columnIndex, PRowIndex rowIndex) {
		return get(columnIndex.getColumn(), rowIndex.getRow());
	}
	
	public default Object get(PColumnIndex index) {
		int col = index.getColumn();
		List<Object> result = new ArrayList<>(getRowCount());
		for (int row = 0; row < getRowCount(); row++) {
			result.add(get(col, row));
		}
		return result;
	}
	
	public default Object get(PRowIndex index) {
		int row = index.getRow();
		List<Object> result = new ArrayList<>(getColumnCount());
		for (int col = 0; col < getColumnCount(); col++) {
			result.add(get(col, row));
		}
		return result;
	}
	
	public default Object get(PModelIndex index) {
		if (index instanceof PRowIndex) {
			return get(asRowIndex(index));
		} else if (index instanceof PColumnIndex) {
			return get(asColumnIndex(index));
		} else {
			PTableCellIndex idx = asTableCellIndex(index);
			return get(idx.getColumn(), idx.getRow());
		}
	}
	
	public Object get(int column, int row);
	
	public default boolean contains(int column, int row) {
		return column >= 0 && column < getColumnCount() 
				&& row >= 0 && row < getRowCount();
	}
	
	public default boolean contains(PModelIndex index) {
		PTableIndex tableIndex = asTableIndex(index);
		int col = tableIndex.getColumn();
		int row = tableIndex.getRow();
		return contains(col, row);
	}
	
	public default boolean canAdd(PModelIndex index, Object content) {
		ThrowException.ifNull(index, "index == null");
		if (index instanceof PRowIndex) {
			int row = asRowIndex(index).getRow();
			return row >= 0 && row <= getRowCount();
		}
		if (index instanceof PColumnIndex) {
			int col = asColumnIndex(index).getColumn();
			return col >= 0 && col <= getColumnCount();
		}
		throw new WrongIndexType(index, PTableIndex.class);
	}
	
	public default boolean canRemove(PModelIndex index) {
		return contains(index);
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
	
	public default PTableCellIndex asTableCellIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTableCellIndex) {
			return (PTableCellIndex) index;
		}
		throw new WrongIndexType(index, PTableCellIndex.class);
	}
	
	public default PColumnIndex asColumnIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PColumnIndex) {
			return (PColumnIndex) index;
		}
		throw new WrongIndexType(index, PColumnIndex.class);
	}
	
	public default PRowIndex asRowIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PRowIndex) {
			return (PRowIndex) index;
		}
		throw new WrongIndexType(index, PRowIndex.class);
	}
	
}