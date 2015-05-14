package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PListMultiSelection extends AbstractPSelection implements PListSelection {
	
	protected final List<PModelIndex> indices = new ArrayList<>();
	
	public void addSelection(PModelIndex index) {
		if (indices.contains(index)) {
			return;
		}
		if (indices.add(index)) {
			fireSelectionAdded(index);
		}
	}
	
	public void removeSelection(PModelIndex index) {
		if (indices.remove(index)) {
			fireSelectionRemoved(index);
		}
	}
	
	public void clearSelection() {
		PModelIndex[] removedIndices = indices.toArray(
				new PModelIndex[indices.size()]);
		indices.clear();
		for (PModelIndex index : removedIndices) {
			fireSelectionRemoved(index);
		}
	}
	
	public List<PModelIndex> getAllSelected() {
		return Collections.unmodifiableList(indices);
	}
	
	public boolean isSelected(PModelIndex index) {
		return indices.contains(index);
	}
	
}