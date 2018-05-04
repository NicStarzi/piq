package edu.udo.piq.components.collections.table;

import edu.udo.piq.components.collections.PModelIndex;

public class PColumnIndex extends PTableIndex implements PModelIndex {
	
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
	
	public int getRow() {
		return -1;
	}
	
}