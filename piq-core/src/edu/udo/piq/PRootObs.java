package edu.udo.piq;

public interface PRootObs {
	
	public default void onComponentAddedToGui(PComponent addedComponent) {}
	
	public default void onComponentRemovedFromGui(PComponent parent, PComponent removedComponent) {}
	
	public default void onStyleSheetChanged(PRoot root, PStyleSheet oldStyleSheet) {}
	
}