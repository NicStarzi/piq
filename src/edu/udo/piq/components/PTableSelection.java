package edu.udo.piq.components;

public interface PTableSelection extends PSelection<PTableCell> {
	
	public void setSelectionMode(SelectionMode selectionMode);
	
	public SelectionMode getSelectionMode();
	
	public void addObs(Object obs);
	
	public void removeObs(Object obs);
	
	public static enum SelectionMode {
	}
	
}