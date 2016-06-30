package edu.udo.piq.components.collections;

import edu.udo.piq.util.ThrowException;

public class PColumnIndex implements PModelIndex {
	
	private final int column;
	
	public PColumnIndex(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("columnIndex="+columnIndex);
		}
		column = columnIndex;
	}
	
	public int getColumn() {
		return column;
	}
	
	public PColumnIndex withOffset(PModelIndex offset) {
		PColumnIndex offsetTable = ThrowException.ifTypeCastFails(offset, 
				PColumnIndex.class, "(offset instanceof PColumnIndex) == false");
		int col = offsetTable.getColumn() + getColumn();
		return new PColumnIndex(col);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getColumn());
		sb.append("]");
		return sb.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof PColumnIndex) {
			return column == ((PColumnIndex) obj).column;
		}
		return false;
	}
	
}