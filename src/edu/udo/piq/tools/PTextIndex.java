package edu.udo.piq.tools;

public class PTextIndex extends ImmutablePBounds {
	
	private final int row;
	private final int col;
	
	public PTextIndex(int x, int y, int width, int height, int line, int column) {
		super(x, y, width, height);
		row = line;
		col = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return col;
	}
	
}