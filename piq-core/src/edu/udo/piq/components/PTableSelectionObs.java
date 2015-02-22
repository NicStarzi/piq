package edu.udo.piq.components;

public interface PTableSelectionObs {
	
	public void selectionAdded(PTableSelection selection, PTablePosition cell);
	
	public void selectionRemoved(PTableSelection selection, PTablePosition cell);
	
}