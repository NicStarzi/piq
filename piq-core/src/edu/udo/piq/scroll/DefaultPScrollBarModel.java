package edu.udo.piq.scroll;

public class DefaultPScrollBarModel extends AbstractPScrollBarModel implements PScrollBarModel {
	
	private double advanceStepSmall = 25;
	private double advanceStepBig = 100;
	private double scroll;
	private int prefSize;
	private int size;
	
	public void setAdvanceSmallStep(double value) {
		advanceStepSmall = value;
	}
	
	public double getAdvanceSmallStep() {
		return advanceStepSmall;
	}
	
	public void setAdvanceBigStep(double value) {
		advanceStepBig = value;
	}
	
	public double getAdvanceBigStep() {
		return advanceStepBig;
	}
	
	public void setScroll(double value) {
//		System.out.println("setScroll="+value);
		if (getSize() >= getPreferredSize()) {
			value = 0;
		}
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		double oldValue = getScroll();
//		System.out.println("model.setScroll="+value+", old="+oldValue);
		if (value != oldValue) {
			scroll = value;
//			System.out.println("scroll="+value);
			fireScrollChangedEvent(oldValue);
		}
	}
	
	public double getScroll() {
		return scroll;
	}
	
	public void addSmallStep() {
		modScrollByAdvance(getAdvanceSmallStep());
	}
	
	public void addBigStep() {
		modScrollByAdvance(getAdvanceBigStep());
	}
	
	public void subSmallStep() {
		modScrollByAdvance(-getAdvanceSmallStep());
	}
	
	public void subBigStep() {
		modScrollByAdvance(-getAdvanceBigStep());
	}
	
	protected void modScrollByAdvance(double advance) {
		int maxOffset = getPreferredSize() - getSize();
		double scrollStep = advance / maxOffset;
		setScroll(getScroll() + scrollStep);
	}
	
	public void setPreferredSize(int value) {
//		System.out.println("model.setPreferredSize="+value);
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
//		System.out.println("model.setSize="+value);
		int oldValue = getSize();
		if (value != oldValue) {
			size = value;
			fireSizeChangedEvent(oldValue);
//			System.out.println("size="+getSize()+" >= pref="+getPreferredSize());
			if (getSize() >= getPreferredSize()) {
				setScroll(0);
			}
		}
	}
	
	public int getSize() {
		return size;
	}
	
}