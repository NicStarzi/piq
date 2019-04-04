package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PSize;

public class ImmutablePBounds extends AbstractPBounds implements PBounds {
	
	private final int x, y, w, h;
	
	public ImmutablePBounds(PBounds other) {
		this(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	
	public ImmutablePBounds(PSize size) {
		this(0, 0, size.getWidth(), size.getHeight());
	}
	
	public ImmutablePBounds(int x, int y, PSize size) {
		this(x, y, size.getWidth(), size.getHeight());
	}
	
	public ImmutablePBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		w = width;
		h = height;
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
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