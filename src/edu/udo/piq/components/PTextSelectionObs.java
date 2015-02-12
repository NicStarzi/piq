package edu.udo.piq.components;

public interface PTextSelectionObs {
	
	public default void selectionAdded(PTextSelection selection, int index) {}
	
	public default void selectionRemoved(PTextSelection selection, int index) {}
	
	public default void selectionChanged(PTextSelection selection) {}
	
}