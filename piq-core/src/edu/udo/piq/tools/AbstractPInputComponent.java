package edu.udo.piq.tools;

import edu.udo.piq.PComponentAction;
import edu.udo.piq.components.util.PKeyInputMap;
import edu.udo.piq.components.util.PKeyInput;

public class AbstractPInputComponent extends AbstractPComponent {
	
	protected final PKeyInputMap inputMap = new PKeyInputMap(this);
	protected boolean enabled = true;
	
	public void defineInput(PKeyInput input, PComponentAction reaction) {
		defineInput(input.getDefaultIdentifier(), input, reaction);
	}
	
	public void defineInput(Object identifier, PKeyInput input, PComponentAction reaction) {
		inputMap.defineInput(identifier, input, reaction);
	}
	
	public void undefine(Object identifier) {
		inputMap.undefine(identifier);
	}
	
	public void setEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			fireReRenderEvent();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isFocusable() {
		return true;
	}
	
}