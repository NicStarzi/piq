package edu.udo.piq.components.textbased;

import java.util.Arrays;

import edu.udo.piq.util.ThrowException;

public class PTextIndexTableMultiLine implements PTextIndexTable {
	
	private static final int[] EMPTY = new int[0];
	
	private int[] lastIndices = EMPTY;
	private int size;
	
	public void setRowCount(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		size = value;
		if (lastIndices.length >= value) {
			Arrays.fill(lastIndices, 0);
		} else {
			lastIndices = new int[value];
		}
	}
	
	public void clear() {
		Arrays.fill(lastIndices, 0);
		size = 0;
	}
	
	public void setLastIndexInRow(int row, int value) {
		if (row >= lastIndices.length) {
			int newCap = Math.max(lastIndices.length * 2, row + 5);
			lastIndices = Arrays.copyOf(lastIndices, newCap);
		}
		if (row >= size) {
			Arrays.fill(lastIndices, size, row + 1, 0);
			size = row + 1;
		}
		ThrowException.ifLess(0, value, "value < 0");
		lastIndices[row] = value;
	}
	
	public int getRowCount() {
		return size;
	}
	
	public int getLastIndexInRow(int row) {
		return lastIndices[row];
	}
}