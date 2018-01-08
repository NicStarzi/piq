package edu.udo.piq.components.popup;

import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;

public interface PComponentActionIndicator {
	
	public PActionKey getActionKey();
	
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