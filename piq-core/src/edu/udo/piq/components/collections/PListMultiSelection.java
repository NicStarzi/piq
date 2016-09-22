package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.tools.AbstractPSelection;

public class PListMultiSelection extends AbstractPSelection implements PListSelection {
	
	protected final List<PModelIndex> indices = new ArrayList<>();
	
	public void addSelection(PModelIndex index) {
		setLastSelected(index);
		if (indices.contains(index)) {
			return;
		}
		if (indices.add(index)) {
			fireSelectionAdded(index);
		}
	}
	
	@SuppressWarnings("null")
	public void removeSelection(PModelIndex index) {
		if (indices.remove(index)) {
			if (index != null && index.equals(getLastSelected())) {
				setLastSelected(null);
			}
			int indexVal = ((PListIndex) index).getIndexValue();
			
			List<PListIndex> indicesToRemove = null;
			List<PListIndex> indicesToAdd = null;
			
			for (PModelIndex otherIndex : indices) {
				PListIndex listIndex = (PListIndex) otherIndex;
				int listIndexVal = listIndex.getIndexValue();
				
				if (indexVal < listIndexVal) {
					if (indicesToRemove == null) {
						indicesToRemove = new ArrayList<>();
						indicesToAdd = new ArrayList<>();
					}
					indicesToRemove.add(listIndex);
					indicesToAdd.add(new PListIndex(listIndexVal - 1));
				}
			}
			if (indicesToRemove != null) {
				List<PListIndex> indicesToRemove2 = new ArrayList<>(indicesToRemove);
				indicesToRemove.removeAll(indicesToAdd);
				indicesToAdd.removeAll(indicesToRemove2);
				for (PListIndex indexToRemove : indicesToRemove) {
					indices.remove(indexToRemove);
					fireSelectionRemoved(indexToRemove);
				}
				for (PListIndex indexToAdd : indicesToAdd) {
					indices.add(indexToAdd);
					fireSelectionAdded(indexToAdd);
				}
			} else {
				fireSelectionRemoved(index);
			}
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
	
	public PListIndex getLastSelected() {
		return (PListIndex) super.getLastSelected();
	}
	
	public boolean hasSelection() {
		return !indices.isEmpty();
	}
	
	public boolean isSelected(PModelIndex index) {
		return indices.contains(index);
	}
	
}