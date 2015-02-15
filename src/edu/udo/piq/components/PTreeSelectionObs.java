package edu.udo.piq.components;

public interface PTreeSelectionObs {
	
	public void selectionAdded(PTreeSelection selection, Object node);
	
	public void selectionRemoved(PTreeSelection selection, Object node);
}