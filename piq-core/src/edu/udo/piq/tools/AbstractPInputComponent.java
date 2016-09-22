package edu.udo.piq.tools;

import java.util.function.Consumer;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInputMap;

public class AbstractPInputComponent 
	extends AbstractPComponent 
{
	
	protected final PKeyInputMap inputMap = new PKeyInputMap(this);
	protected boolean enabled = true;
	
	public <COMP_TYPE extends PComponent> void defineInput(
			PKeyInput<COMP_TYPE> input, Consumer<COMP_TYPE> reaction) 
	{
		defineInput(input.getDefaultIdentifier(), input, reaction);
	}
	
	public <COMP_TYPE extends PComponent> void defineInput(
			Object identifier, PKeyInput<COMP_TYPE> input, Consumer<COMP_TYPE> reaction) 
	{
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