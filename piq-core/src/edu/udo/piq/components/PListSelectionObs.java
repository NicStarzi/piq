package edu.udo.piq.components;

public interface PListSelectionObs {
	
	public void selectionAdded(PListSelection selection, Object element);
	
	public void selectionRemoved(PListSelection selection, Object element);
	
}