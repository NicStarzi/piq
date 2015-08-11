package edu.udo.piq.comps.selectcomps;

public interface PSelectionObs {
	
	public default void onSelectionAdded(PSelection selection, PModelIndex index) {}
	
	public default void onSelectionRemoved(PSelection selection, PModelIndex index) {}
	
	public default void onLastSelectedChanged(PSelection selection, 
			PModelIndex prevLastSelected, PModelIndex newLastSelected) {}
	
}