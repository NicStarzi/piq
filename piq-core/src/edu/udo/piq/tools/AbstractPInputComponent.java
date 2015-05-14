package edu.udo.piq.tools;

import edu.udo.piq.components.util.PInput;
import edu.udo.piq.components.util.PInputMap;

public class AbstractPInputComponent extends AbstractPComponent {
	
	protected final PInputMap inputMap = new PInputMap(this);
	protected boolean enabled = true;
	
	protected void defineInput(Object identifier, PInput input, Runnable reaction) {
		inputMap.defineInput(identifier, input, reaction);
	}
	
	protected void undefine(Object identifier) {
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
	
}