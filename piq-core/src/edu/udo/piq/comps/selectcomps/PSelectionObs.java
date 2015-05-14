package edu.udo.piq.comps.selectcomps;

public interface PSelectionObs {
	
	public void onSelectionAdded(PSelection selection, PModelIndex index);
	
	public void onSelectionRemoved(PSelection selection, PModelIndex index);
	
}