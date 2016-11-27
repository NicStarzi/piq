package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;

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
	
	public MutablePBounds(PBounds other) {
		this(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	
	public MutablePBounds(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		w = width;
		h = height;
	}
	
	public void set(PBounds other) {
		setX(other.getX());
		setY(other.getY());
		setWidth(other.getWidth());
		setHeight(other.getHeight());
	}
	
	public void setX(int value) {
		x = value;
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	public void setY(int value) {
		y = value;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	public void setWidth(int value) {
		w = value;
	}
	
	@Override
	public int getWidth() {
		return w;
	}
	
	public void setHeight(int value) {
		h = value;
	}
	
	@Override
	public int getHeight() {
		return h;
	}
	
	public void add(PSize size) {
		w += size.getWidth();
		h += size.getHeight();
	}
	
	public void subtract(PSize size) {
		w -= size.getWidth();
		h -= size.getHeight();
	}
	
	public void subtract(PInsets insets) {
		x += insets.getFromLeft();
		y += insets.getFromTop();
		w -= insets.getWidth();
		h -= insets.getHeight();
	}
	
}