package edu.udo.piq.components.defaults;

import edu.udo.piq.components.AbstractPSingleValueModel;
import edu.udo.piq.components.PButtonModel;

public class DefaultPButtonModel extends AbstractPSingleValueModel implements PButtonModel {
	
	protected boolean pressed = PButtonModel.DEFAULT_PRESSED_VALUE;
	
	@Override
	public void setPressed(boolean trueIfPressed) {
		if (pressed != trueIfPressed) {
			Object oldValue = getValue();
			pressed = trueIfPressed;
			fireChangeEvent(oldValue);
		}
	}
	
	@Override
	public boolean isPressed() {
		return pressed;
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		setPressed(newValue == Boolean.TRUE || Boolean.TRUE.equals(newValue));
	}
}