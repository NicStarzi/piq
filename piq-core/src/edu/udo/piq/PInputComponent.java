package edu.udo.piq;

import edu.udo.piq.components.util.PInput;

public interface PInputComponent extends PComponent {
	
	public void defineInput(Object identifier, PInput input, Runnable reaction);
	
	public void undefine(Object identifier);
	
	public void setEnabled(boolean isEnabled);
	
	public boolean isEnabled();
	
}