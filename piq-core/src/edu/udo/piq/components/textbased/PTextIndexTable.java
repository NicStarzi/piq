package edu.udo.piq.components.textbased;

public interface PTextIndexTable {
	
	public int getRowCount();
	
	public int getLastIndexInRow(int row);
	
	public default int getColumnCount(int row) {
		if (isInvalidRow(row)) {
			return -1;
		}
		if (row == 0) {
			return getLastIndexInRow(row) + 1;
		}
		int rowBefore = getLastIndexInRow(row - 1);
		return getLastIndexInRow(row) - rowBefore;
	}
	
	public default int getColumn(int index, int row) {
		if (isInvalidIndex(index) || isInvalidRow(row)) {
			return -1;
		}
		int firstIndexInRow = getFirstIndexInRow(row);
		return index - firstIndexInRow;
	}
	
	public default int getColumn(int index) {
		return getColumn(index, getRow(index));
	}
	
	public default int getRow(int index) {
		if (isInvalidIndex(index)) {
			return -1;
		}
		
		int idxMin = 0;
		int idxMax = getRowCount();
		while (idxMin <= idxMax) {
			int idxNow = idxMin + (idxMax - idxMin) / 2;
			
			int lastIndexInRow = getLastIndexInRow(idxNow);
			if (index > lastIndexInRow) {
				idxMin = idxNow + 1;
			} else {
				int firstIndexInRow = getFirstIndexInRow(idxNow);
				if (firstIndexInRow <= index) {
					return idxNow;
				} else {
					idxMax = idxNow - 1;
				}
			}
		}
		return -1;
	}
	
	public default int getIndex(int column, int row) {
		if (isInvalidRow(row) || isInvalidColumn(column, row)) {
			return -1;
		}
		int firstIndexInRow = getFirstIndexInRow(row);
		return firstIndexInRow + column;
	}
	
	public default int getFirstIndexInRow(int row) {
		if (row == 0) {
			return 0;
		}
		return getLastIndexInRow(row - 1) + 1;
	}
	
	public default boolean isInvalidColumn(int column, int row) {
		return column < 0 || column >= getColumnCount(row);
	}
	
	public default boolean isInvalidRow(int row) {
		return row < 0 || row >= getRowCount();
	}
	
	public default boolean isInvalidIndex(int index) {
		return getRowCount() == 0 || index < 0 
				|| index > getLastIndexInRow(getRowCount() - 1);
	}
}