package edu.udo.piq.tools;

import edu.udo.piq.PInsets;

public class MutablePInsets extends AbstractPInsets implements PInsets {
	
	private int fromTop;
	private int fromBtm;
	private int fromLft;
	private int fromRgt;
	
	public MutablePInsets(int top, int bottom, int left, int right) {
		fromTop = top;
		fromBtm = bottom;
		fromLft = left;
		fromRgt = right;
	}
	
	public MutablePInsets(int horizontal, int vertical) {
		this(vertical, vertical, horizontal, horizontal);
	}
	
	public MutablePInsets(int allEqual) {
		this(allEqual, allEqual, allEqual, allEqual);
	}
	
	public MutablePInsets(PInsets other) {
		this(other.getFromTop(),
			other.getFromBottom(),
			other.getFromLeft(),
			other.getFromRight());
	}
	
	public MutablePInsets() {
		this(0, 0, 0, 0);
	}
	
	public void setFromTop(int value) {
		fromTop = value;
	}
	
	@Override
	public int getFromTop() {
		return fromTop;
	}
	
	public void setFromBottom(int value) {
		fromBtm = value;
	}
	
	@Override
	public int getFromBottom() {
		return fromBtm;
	}
	
	public void setFromLeft(int value) {
		fromLft = value;
	}
	
	@Override
	public int getFromLeft() {
		return fromLft;
	}
	
	public void setFromRight(int value) {
		fromRgt = value;
	}
	
	@Override
	public int getFromRight() {
		return fromRgt;
	}
	
	public void set(PInsets other) {
		setFromTop(other.getFromTop());
		setFromBottom(other.getFromBottom());
		setFromLeft(other.getFromLeft());
		setFromRight(other.getFromRight());
	}
	
	public void set(int allEqual) {
		setFromTop(allEqual);
		setFromBottom(allEqual);
		setFromLeft(allEqual);
		setFromRight(allEqual);
	}
	
	public void set(int fromTop, int fromBottom, int fromLeft, int fromRight) {
		setFromTop(fromTop);
		setFromBottom(fromBottom);
		setFromLeft(fromLeft);
		setFromRight(fromRight);
	}
	
}