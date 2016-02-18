package edu.udo.piq.components.collections;

import java.util.List;

public interface PSelection {
	
	public void addSelection(PModelIndex index);
	
	public void removeSelection(PModelIndex index);
	
	public void clearSelection();
	
	public List<PModelIndex> getAllSelected();
	
	public PModelIndex getLastSelected();
	
	public boolean isSelected(PModelIndex index);
	
	public boolean hasSelection();
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}