package edu.udo.piq.components;

public interface PButtonModel extends PSingleValueModel<Boolean> {
	
	public static final boolean DEFAULT_PRESSED_VALUE = false;
	
	@Override
	public default void setValue(Object obj) {
		if (Boolean.TRUE.equals(obj)) {
			setPressed(true);
		} else if (Boolean.FALSE.equals(obj)) {
			setPressed(false);
		}
	}
	
	@Override
	public default Boolean getValue() {
		return Boolean.valueOf(isPressed());
	}
	
	public void setPressed(boolean trueIfPressed);
	
	public boolean isPressed();
	
}