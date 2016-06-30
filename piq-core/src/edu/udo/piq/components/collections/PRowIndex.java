package edu.udo.piq.components.collections;

import edu.udo.piq.util.ThrowException;

public class PRowIndex implements PModelIndex {
	
	private final int row;
	
	public PRowIndex(int rowIndex) {
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex="+rowIndex);
		}
		row = rowIndex;
	}
	
	public int getRow() {
		return row;
	}
	
	public PRowIndex withOffset(PModelIndex offset) {
		PRowIndex offsetTable = ThrowException.ifTypeCastFails(offset, 
				PRowIndex.class, "(offset instanceof PColumnIndex) == false");
		int row = offsetTable.getRow() + getRow();
		return new PRowIndex(row);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getRow());
		sb.append("]");
		return sb.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + row;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof PRowIndex) {
			return row == ((PRowIndex) obj).row;
		}
		return false;
	}
	
}