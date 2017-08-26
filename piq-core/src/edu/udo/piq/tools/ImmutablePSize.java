package edu.udo.piq.tools;

import edu.udo.piq.PSize;

public class ImmutablePSize extends AbstractPSize implements PSize {
	
	private final int w, h;
	
	public ImmutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	@Override
	public int getWidth() {
		return w;
	}
	
	@Override
	public int getHeight() {
		return h;
	}
}