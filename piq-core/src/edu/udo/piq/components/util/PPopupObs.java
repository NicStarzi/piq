package edu.udo.piq.components.util;

import edu.udo.piq.PComponent;

public interface PPopupObs {
	
	public default void onPopupShown(PPopup popup, PComponent popupComp) {}
	
	public default void onPopupHidden(PPopup popup, PComponent popupComp) {}
	
}