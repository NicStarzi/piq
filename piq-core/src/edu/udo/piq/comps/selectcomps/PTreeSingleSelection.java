package edu.udo.piq.comps.selectcomps;

import java.util.Collections;
import java.util.List;

public class PTreeSingleSelection extends AbstractPSelection implements PTreeSelection {
	
	protected final List<PModelIndex> indices = new SingletonList<PModelIndex>();
	
	public void addSelection(PModelIndex index) {
		if (indices.get(0) == index) {
			return;
		}
		setLastSelected(index);
		fireRemovedIfNeeded();
		indices.set(0, index);
		fireSelectionAdded(indices.get(0));
	}
	
	public void removeSelection(PModelIndex index) {
		if (indices.get(0) == index) {
			setLastSelected(null);
			fireSelectionRemoved(indices.get(0));
			indices.set(0, null);
		}
	}
	
	public void clearSelection() {
		if (indices.get(0) != null) {
			PModelIndex index = indices.get(0);
			indices.set(0, null);
			fireSelectionRemoved(index);
		}
	}
	
	public List<PModelIndex> getAllSelected() {
		return Collections.unmodifiableList(indices);
	}
	
	public PTreeIndex getLastSelected() {
		return (PTreeIndex) super.getLastSelected();
	}
	
	public boolean isSelected(PModelIndex index) {
		return indices.get(0) == index;
	}
	
	protected void fireRemovedIfNeeded() {
		if (indices.get(0) != null) {
			fireSelectionRemoved(indices.get(0));
		}
	}
	
}