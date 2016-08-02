package edu.udo.piq.tools;

import edu.udo.piq.PSize;

public class MutablePSize extends AbstractPSize implements PSize {
	
	private int w, h;
	
	public MutablePSize() {
		this(0, 0);
	}
	
	public MutablePSize(PSize other) {
		this(other.getWidth(), other.getHeight());
	}
	
	public MutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	public void set(PSize other) {
		setWidth(other.getWidth());
		setHeight(other.getHeight());
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