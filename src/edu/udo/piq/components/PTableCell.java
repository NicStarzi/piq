package edu.udo.piq.components;

public class PTableCell {
	
	protected final int columnIndex;
	protected final int rowIndex;
	
	public PTableCell(int columnIndex, int rowIndex) {
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getColumnIndex();
		result = prime * result + getRowIndex();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof PTableCell)) {
			return false;
		}
		PTableCell other = (PTableCell) obj;
		return getColumnIndex() == other.getColumnIndex() && getRowIndex() == other.getRowIndex();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PTableCell [column=");
		builder.append(getColumnIndex());
		builder.append(", row=");
		builder.append(getRowIndex());
		builder.append("]");
		return builder.toString();
	}
	
}