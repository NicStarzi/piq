package edu.udo.piq.swing;

import java.util.HashMap;

public class HashBasedGlyphMap implements GlyphMap {
	
	private final HashMap<Character, Glyph> map;
	
	public HashBasedGlyphMap() {
		map = new HashMap<>();
	}
	
	public HashBasedGlyphMap(int capacity) {
		map = new HashMap<>(capacity);
	}
	
	@Override
	public void add(char c, Glyph glyph) {
		map.put(Character.valueOf(c), glyph);
	}
	
	@Override
	public Glyph getGlyphFor(char character) {
		return map.get(Character.valueOf(character));
	}
	
}