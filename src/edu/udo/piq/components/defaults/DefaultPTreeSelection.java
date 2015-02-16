package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.tools.AbstractPTreeSelection;

public class DefaultPTreeSelection extends AbstractPTreeSelection implements PTreeSelection {
	
	private final Set<Object> selection = new HashSet<>();
	
	public void addSelection(Object node) {
		if (selection.add(node)) {
			fireSelectionAddedEvent(node);
		}
	}
	
	public void removeSelection(Object node) {
		if (selection.remove(node)) {
			fireSelectionRemovedEvent(node);
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