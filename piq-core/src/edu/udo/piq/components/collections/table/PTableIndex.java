package edu.udo.piq.components.collections.table;

import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.util.ThrowException;

public abstract class PTableIndex implements PModelIndex {
	
	public abstract int getColumn();
	
	public abstract int getRow();
	
	public boolean isColumnIndex() {
		return getColumn() >= 0;
	}
	
	public boolean isRowIndex() {
		return getRow() >= 0;
	}
	
	@Override
	public PTableIndex withOffset(PModelIndex offset) {
		PTableIndex offsetIndex = ThrowException.ifTypeCastFails(offset,
				PTableIndex.class, "(offset instanceof PTableIndex) == false");
		
		int col = -1;
		int row = -1;
		if (isColumnIndex() || offsetIndex.isColumnIndex()) {
			col = offsetIndex.getColumn() + getColumn();
		}
		if (isRowIndex() || offsetIndex.isRowIndex()) {
			row = offsetIndex.getRow() + getRow();
		}
		if (col >= 0 && row >= 0) {
			return new PTableCellIndex(col, row);
		}
		if (col >= 0) {
			return new PColumnIndex(col);
		} else {
			return new PRowIndex(row);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getColumn());
		sb.append(", ");
		sb.append(getRow());
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getColumn();
		result = prime * result + getRow();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof PTableIndex) {
			return getColumn() == ((PTableIndex) obj).getColumn()
					&& getRow() == ((PTableIndex) obj).getRow();
		}
		return false;
	}
	
}