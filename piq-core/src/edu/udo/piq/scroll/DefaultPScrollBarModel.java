package edu.udo.piq.scroll;

public class DefaultPScrollBarModel extends AbstractPScrollBarModel implements PScrollBarModel {
	
	private double scroll;
	private int prefSize;
	private int size;
	
	public void setScroll(double value) {
		System.out.println("setScroll="+value);
		if (getSize() >= getPreferredSize()) {
			value = 0;
		}
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		double oldValue = getScroll();
		if (value != oldValue) {
			scroll = value;
			System.out.println("scroll="+value);
			fireScrollChangedEvent(oldValue);
		}
	}
	
	public double getScroll() {
		return scroll;
	}
	
	public void addSmallStep() {
		setScroll(getScroll() + 0.1);
	}
	
	public void addBigStep() {
		setScroll(getScroll() + 0.3);
	}
	
	public void subSmallStep() {
		setScroll(getScroll() - 0.1);
	}
	
	public void subBigStep() {
		setScroll(getScroll() - 0.3);
	}
	
	public void setPreferredSize(int value) {
		System.out.println("model.setPreferredSize="+value);
		int oldValue = getPreferredSize();
		if (value != oldValue) {
			prefSize = value;
			firePreferredSizeChangedEvent(oldValue);
			if (getSize() >= getPreferredSize()) {
				setScroll(0);
			}
		}
	}
	
	public int getPreferredSize() {
		return prefSize;
	}
	
	public void setSize(int value) {
		System.out.println("model.setSize="+value);
		int oldValue = getSize();
		if (value != oldValue) {
			size = value;
			fireSizeChangedEvent(oldValue);
			if (getSize() >= getPreferredSize()) {
				setScroll(0);
			}
		}
	}
	
	public int getSize() {
		return size;
	}
	
}