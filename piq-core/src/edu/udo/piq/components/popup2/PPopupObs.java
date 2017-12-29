package edu.udo.piq.components.popup2;

import edu.udo.piq.PComponent;

public interface PPopupObs {
	
	public default void onPopupShown(PPopup popup, int clickX, 
			int clickY, PComponent bodyComponent) {}
	
	public default void onPopupHidden(PPopup popup, int clickX, 
			int clickY, PComponent bodyComponent) {}
	
}