package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface PSelectionComponent {
	
	public PSelection getSelection();
	
	public PModel getModel();
	
	public PModelIndex getIndexAt(int x, int y);
	
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
			return Collections.singletonList(model.get(indices.get(0)));
		}
		List<Object> result = new ArrayList<>(indices.size());
		for (PModelIndex index : indices) {
			result.add(model.get(index));
		}
		return result;
	}
	
	public void addObs(PModelObs obs);
	
	public void removeObs(PModelObs obs);
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}