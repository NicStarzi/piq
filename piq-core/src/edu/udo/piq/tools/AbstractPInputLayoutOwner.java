package edu.udo.piq.tools;

import java.util.function.Consumer;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.util.PAccelerator;
import edu.udo.piq.components.util.PAcceleratorMap;

public class AbstractPInputLayoutOwner extends AbstractPLayoutOwner {
	
	protected final PAcceleratorMap inputMap = new PAcceleratorMap(this);
	protected boolean enabled = true;
	
	public <COMP_TYPE extends PComponent> void defineInput(
			PAccelerator<COMP_TYPE> input, Consumer<COMP_TYPE> reaction) 
	{
		defineInput(input.getDefaultIdentifier(), input, reaction);
	}
	
	public <COMP_TYPE extends PComponent> void defineInput(
			Object identifier, PAccelerator<COMP_TYPE> input, Consumer<COMP_TYPE> reaction) 
	{
		inputMap.defineInput(identifier, input, reaction);
	}
	
	public void undefine(Object identifier) {
		inputMap.undefine(identifier);
	}
	
	public void setThisIsEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			fireReRenderEvent();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
}