package edu.udo.piq.tools;

import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractListBasedPSelection
	<INDEX_TYPE extends PModelIndex> 
	extends AbstractPSelection 
{
	
	protected final Class<INDEX_TYPE> idxCls;
	protected final List<PModelIndex> indices;
	
	protected AbstractListBasedPSelection(Class<INDEX_TYPE> indexClass) {
		idxCls = indexClass;
		indices = createList();
	}
	
	protected abstract List<PModelIndex> createList();
	
	public void addSelection(PModelIndex index) {
		setLastSelected(index);
		if (indices.contains(index)) {
			return;
		}
		if (indices.add(index)) {
			fireSelectionAdded(index);
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
	
	public void setLastSelected(PModelIndex index) {
		ThrowException.ifTypeCastFails(index, idxCls, "index instanceof INDEX_TYPE == false");
		super.setLastSelected(index);
	}
	
	@SuppressWarnings("unchecked")
	public INDEX_TYPE getLastSelected() {
		return (INDEX_TYPE) super.getLastSelected();
	}
	
	public boolean hasSelection() {
		return !indices.isEmpty();
	}
	
	public boolean isSelected(PModelIndex index) {
		return indices.contains(index);
	}
}