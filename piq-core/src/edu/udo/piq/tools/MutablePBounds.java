package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public class MutablePBounds extends AbstractPBounds implements PBounds {
	
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	
	public MutablePBounds() {
		this(0, 0, 0, 0);
	}
	
	public MutablePBounds(int width, int height) {
		this(0, 0, width, height);
	}
	
	public MutablePBounds(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}
	
	public void setX(int value) {
		x = value;
	}
	
	public int getX() {
		return x;
	}
	
	public void setY(int value) {
		y = value;
	}
	
	public int getY() {
		return y;
	}
	
	public void setWidth(int value) {
		w = value;
	}
	
	public int getWidth() {
		return w;
	}
	
	public void setHeight(int value) {
		h = value;
	}
	
	public int getHeight() {
		return h;
	}
	
}