package edu.udo.piq.tools;

import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

public class MutablePSize extends AbstractPSize implements PSize {
	
	private int w, h;
	
	public MutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	public MutablePSize() {
		this(0, 0);
	}
	
	public MutablePSize(PSize other) {
		this(other.getWidth(), other.getHeight());
	}
	
	public MutablePSize(PInsets insets) {
		this(insets.getWidth(), insets.getHeight());
	}
	
	public void set(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void set(PSize other) {
		ThrowException.ifNull(other, "other == null");
		set(other.getWidth(), other.getHeight());
	}
	
	public void set(PInsets insets) {
		ThrowException.ifNull(insets, "insets == null");
		set(insets.getWidth(), insets.getHeight());
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
	
	public void add(int width, int height) {
		addWidth(width);
		addHeight(height);
	}
	
	public void add(PSize other) {
		ThrowException.ifNull(other, "other == null");
		add(other.getWidth(), other.getHeight());
	}
	
	public void add(PInsets insets) {
		ThrowException.ifNull(insets, "insets == null");
		add(insets.getWidth(), insets.getHeight());
	}
	
	public void addWidth(int value) {
		setWidth(getWidth() + value);
	}
	
	public void addWidth(PSize other) {
		ThrowException.ifNull(other, "other == null");
		addWidth(other.getWidth());
	}
	
	public void addWidth(PInsets insets) {
		ThrowException.ifNull(insets, "insets == null");
		addWidth(insets.getWidth());
	}
	
	public void addHeight(int value) {
		setHeight(getHeight() + value);
	}
	
	public void addHeight(PSize other) {
		ThrowException.ifNull(other, "other == null");
		addHeight(other.getHeight());
	}
	
	public void addHeight(PInsets insets) {
		ThrowException.ifNull(insets, "insets == null");
		addHeight(insets.getHeight());
	}
	
}