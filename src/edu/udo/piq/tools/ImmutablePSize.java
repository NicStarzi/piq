package edu.udo.piq.tools;

import edu.udo.piq.PSize;

public class ImmutablePSize extends AbstractPSize implements PSize {
	
	private final int w, h;
	
	public ImmutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
}