package edu.udo.piq.tools;

import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;

public class PTextPositionTable {
	
	private PFontResource font;
	private List<String> lines;
	private PBounds[] posTable;
	
	public void setFont(PFontResource font) {
		this.font = font;
		posTable = null;
	}
	
	public PFontResource getFont() {
		return font;
	}
	
	public void setTextLines(List<String> textLines) {
		lines = textLines;
		posTable = null;
	}
	
	public List<String> getTextLines() {
		return lines;
	}
	
	public boolean isValid() {
		return lines != null && font != null;
	}
	
	public int getIndex(int x, int y) {
		if (posTable == null) {
			buildTextPositions();
		}
		if (posTable == null) {
			return -1;
		}
		for (int i = 0; i < posTable.length; i++) {
			if (posTable[i].contains(x, y)) {
				return i;
			}
		}
		return -1;
	}
	
	public PBounds getBoundsOf(int index) {
		if (index == -1) {
			return null;
		}
		if (posTable == null) {
			buildTextPositions();
		}
		return posTable[index];
	}
	
	protected void buildTextPositions() {
		PFontResource font = getFont();
		List<String> lines = getTextLines();
		
		int letterCount = 0;
		for (int lineID = 0; lineID < lines.size(); lineID++) {
			letterCount += lines.get(lineID).length();
		}
		
		posTable = new PBounds[letterCount];
		
		int x = 0;
		int y = 0;
		int lineH = 0;
		int posID = 0;
		for (int lineID = 0; lineID < lines.size(); lineID++) {
			String line = lines.get(lineID);
			for (int letterID = 0; letterID < line.length(); letterID++) {
				String letter = line.substring(letterID, letterID + 1);
				PSize letterSize = font.getSize(letter);
				
				int w = letterSize.getWidth();
				int h = letterSize.getHeight();
				posTable[posID++] = new ImmutablePBounds(x, y, w, h);
				
				x += w;
				if (lineH < h) {
					lineH = h;
				}
			}
			x = 0;
			y += lineH;
			
		}
	}
	
}