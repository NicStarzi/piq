package edu.udo.piq.actions;

import edu.udo.piq.PRoot;

public interface PComponentAction {
	
	public default boolean isEnabled(PRoot root) {
		return true;
	}
	
	public default PAccelerator getAccelerator() {
		return null;
	}
	
	public void tryToPerform(PRoot root);
	
	public void addObs(PComponentActionObs obs);
	
	public void removeObs(PComponentActionObs obs);
	
}