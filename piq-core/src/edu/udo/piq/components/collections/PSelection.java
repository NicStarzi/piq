package edu.udo.piq.components.collections;

import java.util.List;

import edu.udo.piq.util.ThrowException;

public interface PSelection {
	
	public void addSelection(PModelIndex index);
	
	public void removeSelection(PModelIndex index);
	
	public void clearSelection();
	
	public List<PModelIndex> getAllSelected();
	
	public PModelIndex getLastSelected();
	
	public default PModelIndex getOneSelected() {
		if (!hasSelection()) {
			return null;
		}
		List<PModelIndex> all = getAllSelected();
		ThrowException.ifEqual(0, all.size(), "getAllSelected().size() == 0");
		return all.get(0);
	}
	
	public boolean isSelected(PModelIndex index);
	
	public boolean hasSelection();
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}