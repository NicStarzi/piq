package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;

public interface PPopupObs {
	
	public default void onPopupShown(PPopup popup, PComponent popupComp) {}
	
	public default void onPopupHidden(PPopup popup, PComponent popupComp) {}
	
}