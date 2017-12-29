package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.util.ThrowException;

public interface PSelectionComponent {
	
	public PSelection getSelection();
	
	public PModel getModel();
	
	public PModelIndex getIndexAt(int x, int y);
	
	public default void setSelected(Object value) {
		ThrowException.ifNull(getModel(), "getModel() == null");
		PModelIndex index = getModel().getIndexOf(value);
		ThrowException.ifNull(index, "getModel().getIndexOf(value) == null");
		
		PSelection sel = getSelection();
		if (sel == null) {
			return;
		}
		if (sel.isSelected(index)) {
			return;
		}
		getSelection().clearSelection();
		getSelection().addSelection(index);
	}
	
	public default void setSelected(PModelIndex index) {
		ThrowException.ifNull(index, "index == null");
		if (getSelection() != null) {
			getSelection().clearSelection();
			getSelection().addSelection(index);
		}
	}
	
	public default List<PModelIndex> getAllSelectedIndices() {
		if (getSelection() == null) {
			return Collections.emptyList();
		}
		return getSelection().getAllSelected();
	}
	
	public default PModelIndex getLastSelectedIndex() {
		if (getSelection() == null) {
			return null;
		}
		return getSelection().getLastSelected();
	}
	
	public default Object getLastSelectedContent() {
		if (getSelection() == null || getModel() == null) {
			return null;
		}
		PModelIndex index = getSelection().getLastSelected();
		if (index == null) {
			return null;
		}
		return getModel().get(index);
	}
	
	public default Object getContentAt(int x, int y) {
		PModel model = getModel();
		if (model == null) {
			return null;
		}
		PModelIndex index = getIndexAt(x, y);
		if (index == null) {
			return null;
		}
		return model.get(index);
	}
	
	public default List<Object> getAllSelectedContent() {
		PSelection selection = getSelection();
		List<PModelIndex> indices = selection.getAllSelected();
		if (indices.isEmpty()) {
			return Collections.emptyList();
		}
		PModel model = getModel();
		if (indices.size() == 1) {
			PModelIndex index = indices.get(0);
			Object element = model.get(index);
			return Collections.singletonList(element);
		}
		List<Object> result = new ArrayList<>(indices.size());
		for (PModelIndex index : indices) {
			result.add(model.get(index));
		}
		return result;
	}
	
	public default boolean isStrongFocusOwner() {
		return true;
	}
	
	public void addObs(PModelObs obs);
	
	public void removeObs(PModelObs obs);
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}