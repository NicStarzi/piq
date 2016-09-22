package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.tools.AbstractListBasedPSelection;

public class PTableMultiSelection extends AbstractListBasedPSelection<PTableCellIndex> implements PTableSelection {
	
	public PTableMultiSelection() {
		super(PTableCellIndex.class);
	}
	
	protected List<PModelIndex> createList() {
		return new ArrayList<>();
	}
	
	public void removeSelection(PModelIndex index) {
		if (indices.remove(index)) {
			if (index != null && index.equals(getLastSelected())) {
				setLastSelected(null);
			}
			fireSelectionRemoved(index);
		}
	}
	
}