package edu.udo.piq.components.collections.table;

import edu.udo.piq.components.collections.PModelIndex;

public class PRowIndex extends PTableIndex implements PModelIndex {
	
	private final int row;
	
	public PRowIndex(int rowIndex) {
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex="+rowIndex);
		}
		row = rowIndex;
	}
	
	public int getColumn() {
		return -1;
	}
	
	public int getRow() {
		return row;
	}
	
}