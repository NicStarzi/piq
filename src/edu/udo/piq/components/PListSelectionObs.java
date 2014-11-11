package edu.udo.piq.components;

public interface PListSelectionObs {
	
	public void selectionAdded(PListSelection selection, int index);
	
	public void selectionRemoved(PListSelection selection, int index);
	
}