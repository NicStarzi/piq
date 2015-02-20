package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.AbstractPListSelection;

public class PListSelectionArbitraryRows extends AbstractPListSelection implements PListSelection {
	
	private final List<Object> selection = new ArrayList<>();
	
	public void addSelection(Object element) {
		if (!selection.contains(element)) {
			selection.add(element);
			fireSelectionAddedEvent(element);
		}
	}
	
	public void removeSelection(Object element) {
		if (selection.remove(element)) {
			fireSelectionRemovedEvent(element);
		}
	}
	
	public void clearSelection() {
		if (!selection.isEmpty()) {
			Object[] copy = selection.toArray();
			selection.clear();
			for (int i = 0; i < copy.length; i++) {
				fireSelectionRemovedEvent(copy[i]);
			}
		}
	}
	
	public List<Object> getSelection() {
		return Collections.unmodifiableList(selection);
	}
	
	public boolean isSelected(Object element) {
		return selection.contains(element);
	}
	
}