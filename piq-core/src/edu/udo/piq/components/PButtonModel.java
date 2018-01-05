package edu.udo.piq.components;

public interface PButtonModel extends PSingleValueModel {
	
	public static final boolean DEFAULT_PRESSED_VALUE = false;
	public static final boolean DEFAULT_ENABLED_VALUE = true;
	
	@Override
	public default void setValue(Object obj) {
		if (Boolean.TRUE.equals(obj)) {
			setPressed(true);
		} else if (Boolean.FALSE.equals(obj)) {
			setPressed(false);
		}
	}
	
	@Override
	public default Object getValue() {
		return Boolean.valueOf(isPressed());
	}
	
	public void setPressed(boolean trueIfPressed);
	
	public boolean isPressed();
	
	public void setEnabled(boolean trueIfEnabled);
	
	public boolean isEnabled();
	
}