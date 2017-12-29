package edu.udo.piq.components.popup2;

import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PComponentAction;

public interface PComponentActionIndicator {
	
	public Object getActionKey();
	
	public default Object getIconValue(PRoot root, PComponentAction action) {
		return getClass().getSimpleName();
	}
	
	public default Object getLabelValue(PRoot root, PComponentAction action) {
		return getClass().getSimpleName();
	}
	
	public default PAccelerator getDefaultAccelerator() {
		return null;
	}
	
}