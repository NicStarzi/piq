package edu.udo.piq.components.collections;

public class PTableIndex implements PModelIndex {
	
	private final int column;
	private final int row;
	
	public PTableIndex(int columnIndex, int rowIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("columnIndex="+columnIndex);
		}
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex="+rowIndex);
		}
		column = columnIndex;
		row = rowIndex;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	
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
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof PTableIndex) {
			return column == ((PTableIndex) obj).column
					&& row == ((PTableIndex) obj).row;
		}
		return false;
	}
	
}