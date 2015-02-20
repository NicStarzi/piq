package edu.udo.piq.scroll;

public class DefaultPScrollBarModel extends AbstractPScrollBarModel implements PScrollBarModel {
	
	private double scroll;
	private int prefSize;
	private int size;
	
	public void setScroll(double value) {
		double oldValue = getScroll();
		scroll = value;
		fireScrollChangedEvent(oldValue);
	}
	
	public double getScroll() {
		return scroll;
	}
	
	public void setPreferredSize(int value) {
		int oldValue = getPreferredSize();
		prefSize = value;
		firePreferredSizeChangedEvent(oldValue);
	}
	
	public int getPreferredSize() {
		return prefSize;
	}
	
	public void setSize(int value) {
		int oldValue = getSize();
		size = value;
		fireSizeChangedEvent(oldValue);
	}
	
	public int getSize() {
		return size;
	}
	
}