package edu.udo.piq.tools;

import edu.udo.piq.PInsets;

public class ImmutablePInsets extends AbstractPInsets implements PInsets {
	
	private final int fromTop;
	private final int fromBtm;
	private final int fromLft;
	private final int fromRgt;
	
	public ImmutablePInsets(int top, int bottom, int left, int right) {
		fromTop = top;
		fromBtm = bottom;
		fromLft = left;
		fromRgt = right;
	}
	
	public ImmutablePInsets(int horizontal, int vertical) {
		this(vertical, vertical, horizontal, horizontal);
	}
	
	public ImmutablePInsets(int allEqual) {
		this(allEqual, allEqual, allEqual, allEqual);
	}
	
	public ImmutablePInsets(PInsets other) {
		this(other.getFromTop(), 
			other.getFromBottom(), 
			other.getFromLeft(), 
			other.getFromRight());
	}
	
	public ImmutablePInsets() {
		this(0, 0, 0, 0);
	}
	
	public int getFromTop() {
		return fromTop;
	}
	
	public int getFromBottom() {
		return fromBtm;
	}
	
	public int getFromLeft() {
		return fromLft;
	}
	
	public int getFromRight() {
		return fromRgt;
	}
	
}