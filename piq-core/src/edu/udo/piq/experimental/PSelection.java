package edu.udo.piq.experimental;

import java.util.List;

public interface PSelection {
	
	public void addSelection(PModelIndex index);
	
	public void removeSelection(PModelIndex index);
	
	public void clearSelection();
	
	public List<PModelIndex> getAllSelected();
	
	public boolean isSelected(PModelIndex index);
	
}