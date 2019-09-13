package edu.udo.piq.swing;

import edu.udo.piq.util.Throw;

public class ArrayBasedGlyphMap implements GlyphMap {
	
	private final Glyph[] arr;
	private final char minChar;
	
	public ArrayBasedGlyphMap(char minChar, char maxChar) {
		int length = maxChar - minChar + 1;
		arr = new Glyph[length];
		this.minChar = minChar;
	}
	
	@Override
	public void add(char c, Glyph glyph) {
		Throw.ifLess(minChar, c, () -> "character less than smallest legal character. {c == '"+c+"'}");
		char maxChar = (char) (minChar + arr.length - 1);
		Throw.ifMore(maxChar, c, () -> "character greater than largest legal character. {c == '"+c+"'}");
		int index = c - minChar;
		arr[index] = glyph;
	}
	
	@Override
	public Glyph getGlyphFor(char character) {
		char maxChar = (char) (minChar + arr.length - 1);
		if (character >= minChar && character <= maxChar) {
			int index = character - minChar;
			return arr[index];
		}
		return null;
	}
	
}