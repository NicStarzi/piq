package edu.udo.piq.components.collections;

public class PTableCellIndex extends PTableIndex implements PModelIndex {
	
	private final int column;
	private final int row;
	
	public PTableCellIndex(int columnIndex, int rowIndex) {
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
	
}