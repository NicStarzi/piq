package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.AbstractPListSelection;

public class DefaultPListSelection extends AbstractPListSelection implements PListSelection {
	
	private final Set<Integer> selection = new HashSet<>();
	
	public void addSelection(int index) {
		Integer trueIndex = Integer.valueOf(index);
		if (!selection.contains(trueIndex)) {
			selection.add(trueIndex);
			fireSelectionAddedEvent(index);
		}
	}
	
	public void removeSelection(int index) {
		Integer trueIndex = Integer.valueOf(index);
		if (selection.contains(trueIndex)) {
			selection.remove(trueIndex);
			fireSelectionRemovedEvent(index);
		}
	}
	
	public void clearSelection() {
		if (!selection.isEmpty()) {
			List<Integer> copy = new ArrayList<>(selection);
			selection.clear();
			for (Integer index : copy) {
				fireSelectionRemovedEvent(index.intValue());
			}
		}
	}
	
	public Set<Integer> getSelection() {
		return Collections.unmodifiableSet(selection);
	}
	
	public boolean isSelected(int index) {
		return selection.contains(Integer.valueOf(index));
	}
	
}