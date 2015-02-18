package edu.udo.piq.components;

public interface PTableSelectionObs {
	
	public void selectionAdded(PTableSelection selection, PTableCell cell);
	
	public void selectionRemoved(PTableSelection selection, PTableCell cell);
	
}