package edu.udo.piq.components;

public interface PDropDownSelectionObs {
	
	public void selectionChanged(
			PDropDownSelection selection, 
			Object oldSelection, 
			Object newSelection);
	
}