package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public class PTextIndexTable {
	
	private PTextIndex[] indexTable;
	
	public void invalidate() {
		indexTable = null;
	}
	
	public boolean isInvalid() {
		return indexTable == null;
	}
	
	public void setToSize(int minCap) {
		if (indexTable == null || indexTable.length < minCap) {
			indexTable = new PTextIndex[minCap + 1];
		}
	}
	
	public void setBounds(int index, PTextIndex textIndex) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
		indexTable[index] = textIndex;
		if (index == indexTable.length - 2) {
			int x = textIndex.getX();
			int y = textIndex.getY();
			int w = textIndex.getWidth();
			int h = textIndex.getHeight();
			int row = textIndex.getRow();
			int col = textIndex.getColumn();
			
			indexTable[index + 1] = new PTextIndex(x + w, y, 1, h, row, col + 1);
		}
	}
	
	public PBounds getBounds(int index) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
		return indexTable[index];
	}
	
	public int getRowOf(int index) {
		return indexTable[index].getRow();
	}
	
	public int getColumnOf(int index) {
		return indexTable[index].getColumn();
	}
	
	public int getIndex(int row, int column) {
		if (row < 0) {
			return 0;
		}
		int lastIndexInRow = -1;
		for (int i = 0; i < indexTable.length; i++) {
			int curRow = indexTable[i].getRow();
			if (curRow < row) {
				continue;
			}
			if (curRow > row) {
				break;
			}
			if (curRow == row) {
				lastIndexInRow = i;
				if (indexTable[i].getColumn() == column) {
					return i;
				}
			}
		}
		if (lastIndexInRow == -1) {
			lastIndexInRow = indexTable.length - 1;
		}
		return lastIndexInRow;
	}
	
	public int getIndexAt(int x, int y) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
//		System.out.println("getIndexAt x="+x+", y="+y);
		// No characters
		if (indexTable.length == 0) {
//			System.out.println("boundsTable.length == 0");
			return 0;
		}
		// Position is above text
		if (y < indexTable[0].getY()) {
//			System.out.println("y < boundsTable[0].getY()");
			return 0;
		}
		// Position is below text
		if (y >= indexTable[indexTable.length - 1].getFinalY()) {
//			System.out.println("y >= boundsTable[boundsTable.length - 1].getFinalY()");
			return indexTable.length - 1;
		}
		// We store the last index on the correct line as a best guess return value
		int lastGoodBoundsIndex = 0;
		for (int i = 0; i < indexTable.length; i++) {
			// Bounds are sorted line by line
			PBounds bnds = indexTable[i];
//			System.out.println("LOOP i="+i+" bnds="+bnds);
			// Bounds is part of a line above point
			if (bnds.getFinalY() < y) {
//				System.out.println("bnds.getFinalY() < y");
				continue;
			} else {
				// No bounds on correct line fit, last bounds in line
				if (bnds.getY() > y) {
//					System.out.println("bnds.getY() > y");
					return lastGoodBoundsIndex;
				}
//				System.out.println(bnds.getX()+" >= "+x+" && "+bnds.getFinalX()+" <= "+x);
				// Bounds fit in both x and y axis
				if (bnds.getX() <= x && bnds.getFinalX() >= x) {
//					System.out.println("EXIT i="+i);
					return i;
				}
				// Advance last known good bounds
				lastGoodBoundsIndex = i;
			}
		}
		// In case we want the end of the string
		return lastGoodBoundsIndex;
	}
	
}