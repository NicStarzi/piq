package edu.udo.piq.experimental;

public class PTableIndex implements PModelIndex {
	
	private final int col, row;
	
	public PTableIndex(int column, int row) {
		this.col = column;
		this.row = row;
	}
	
	public int getColumn() {
		return col;
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
	
}