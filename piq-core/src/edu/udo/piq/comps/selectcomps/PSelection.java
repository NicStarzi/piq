package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.List;

public interface PSelection {
	
	public void addSelection(PModelIndex index);
	
	public void removeSelection(PModelIndex index);
	
	public void clearSelection();
	
	public List<PModelIndex> getAllSelected();
	
	public PModelIndex getLastSelected();
	
	public default List<PModelIndex> copyAllSelected() {
		return new ArrayList<>(getAllSelected());
	}
	
	public boolean isSelected(PModelIndex index);
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}