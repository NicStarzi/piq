package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.AbstractPListSelection;

public class DefaultPListSelection extends AbstractPListSelection implements PListSelection {
	
	private final Set<Object> selection = new HashSet<>();
	
	public void addSelection(Object element) {
		if (getSelectionMode() == SelectionMode.SINGLE_ROW 
				&& !selection.contains(element)) 
		{
			clearSelection();
		} if (selection.add(element)) {
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
			List<Object> copy = new ArrayList<>(selection);
			selection.clear();
			for (Object element : copy) {
				fireSelectionRemovedEvent(element);
			}
		}
	}
	
	public Set<Object> getSelection() {
		return Collections.unmodifiableSet(selection);
	}
	
	public boolean isSelected(Object element) {
		return selection.contains(element);
	}
	
}