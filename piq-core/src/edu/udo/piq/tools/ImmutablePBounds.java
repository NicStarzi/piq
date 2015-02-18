package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public class ImmutablePBounds extends AbstractPBounds implements PBounds {
	
	private final int x, y, w, h;
	
	public ImmutablePBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
}