package edu.udo.piq.components;

public interface PTextSelectionObs {
	
	public void selectionAdded(PTextSelection selection, int index);
	
	public void selectionRemoved(PTextSelection selection, int index);
	
	public void selectionChanged(PTextSelection selection);
	
}