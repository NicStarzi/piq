package edu.udo.piq;

import edu.udo.piq.style.PStyleSheet;

public interface PRootObs {
	
	public default void onComponentAddedToGui(PComponent addedComponent) {}
	
	public default void onComponentRemovedFromGui(PComponent parent, PComponent removedComponent) {}
	
	public default void onStyleSheetChanged(PRoot root, PStyleSheet oldStyleSheet) {}
	
}