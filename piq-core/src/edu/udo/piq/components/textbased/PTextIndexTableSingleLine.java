package edu.udo.piq.components.textbased;

public class PTextIndexTableSingleLine implements PTextIndexTable {
	
	private int lastIndex;
	
	public void setLastIndexInRow(int value) {
		lastIndex = value;
	}
	
	public int getRowCount() {
		return 1;
	}
	
	public int getLastIndexInRow(int row) {
		return lastIndex;
	}
}