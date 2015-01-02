package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public class PTextPositionTable {
	
	private PBounds[] boundsTable;
	
	public void invalidate() {
		boundsTable = null;
	}
	
	public boolean isInvalid() {
		return boundsTable == null;
	}
	
	public void setToSize(int minCap) {
		if (boundsTable == null || boundsTable.length < minCap) {
			boundsTable = new PBounds[minCap];
		}
	}
	
	public void setBounds(int index, PBounds bounds) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
		boundsTable[index] = bounds;
	}
	
	public PBounds getBounds(int index) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
		return boundsTable[index];
	}
	
	public int getIndexAt(int x, int y) {
		if (isInvalid()) {
			throw new IllegalStateException("isInvalid() == true");
		}
//		System.out.println("getIndexAt x="+x+", y="+y);
		// No characters
		if (boundsTable.length == 0) {
//			System.out.println("boundsTable.length == 0");
			return 0;
		}
		// Position is above text
		if (y < boundsTable[0].getY()) {
//			System.out.println("y < boundsTable[0].getY()");
			return 0;
		}
		// Position is below text
		if (y >= boundsTable[boundsTable.length - 1].getFinalY()) {
//			System.out.println("y >= boundsTable[boundsTable.length - 1].getFinalY()");
			return boundsTable.length - 1;
		}
		// We store the last index on the correct line as a best guess return value
		int lastGoodBoundsIndex = 0;
		for (int i = 0; i < boundsTable.length; i++) {
			// Bounds are sorted line by line
			PBounds bnds = boundsTable[i];
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
	
//	private PFontResource font;
//	private List<String> lines;
//	private PBounds[] posTable;
//	
//	public void setFont(PFontResource font) {
//		this.font = font;
//		posTable = null;
//	}
//	
//	public PFontResource getFont() {
//		return font;
//	}
//	
//	public void setTextLines(List<String> textLines) {
//		lines = textLines;
//		posTable = null;
//	}
//	
//	public List<String> getTextLines() {
//		return lines;
//	}
//	
//	public boolean isValid() {
//		return lines != null && font != null;
//	}
//	
//	public int getIndex(int x, int y) {
//		if (posTable == null) {
//			buildTextPositions();
//		}
//		if (posTable == null) {
//			return -1;
//		}
//		if (y < posTable[0].getY()) {
//			return 0;
//		}
//		if (y > posTable[posTable.length - 1].getFinalY()) {
//			return posTable.length - 1;
//		}
//		for (int i = 0; i < posTable.length; i++) {
//			if (posTable[i].contains(x, y)) {
//				return i;
//			}
//		}
//		return -1;
//	}
//	
//	public PBounds getBoundsOf(int index) {
//		if (index == -1) {
//			return null;
//		}
//		if (posTable == null) {
//			buildTextPositions();
//		}
//		return posTable[index];
//	}
//	
//	protected void buildTextPositions() {
//		PFontResource font = getFont();
//		List<String> lines = getTextLines();
//		
//		int letterCount = 0;
//		for (int lineID = 0; lineID < lines.size(); lineID++) {
//			letterCount += lines.get(lineID).length();
//		}
//		
//		posTable = new PBounds[letterCount];
//		
//		int x = 0;
//		int y = 0;
//		int lineH = 0;
//		int posID = 0;
//		for (int lineID = 0; lineID < lines.size(); lineID++) {
//			String line = lines.get(lineID);
//			for (int letterID = 0; letterID < line.length(); letterID++) {
//				String letter = line.substring(letterID, letterID + 1);
//				PSize letterSize = font.getSize(letter);
//				
//				int w = letterSize.getWidth();
//				int h = letterSize.getHeight();
//				posTable[posID++] = new ImmutablePBounds(x, y, w, h);
//				
//				x += w;
//				if (lineH < h) {
//					lineH = h;
//				}
//			}
//			x = 0;
//			y += lineH;
//			
//		}
//	}
	
}