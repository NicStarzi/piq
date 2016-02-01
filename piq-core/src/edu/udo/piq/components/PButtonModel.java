package edu.udo.piq.components;

public interface PButtonModel {
	
	public default void setValue(Object obj) {
		if (Boolean.TRUE.equals(obj)) {
			setPressed(true);
		} else if (Boolean.FALSE.equals(obj)) {
			setPressed(false);
		}
	}
	
	public default Object getValue() {
		return Boolean.valueOf(isPressed());
	}
	
	public void setPressed(boolean isPressed);
	
	public boolean isPressed();
	
	public void addObs(PButtonModelObs obs);
	
	public void removeObs(PButtonModelObs obs);
	
}