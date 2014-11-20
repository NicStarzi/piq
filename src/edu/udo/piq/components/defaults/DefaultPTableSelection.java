package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PTableCell;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.tools.AbstractPTableSelection;

public class DefaultPTableSelection extends AbstractPTableSelection implements PTableSelection {
	
	private final Set<PTableCell> selection = new HashSet<>();
	
	public void addSelection(PTableCell cell) {
		if (selection.add(cell)) {
			fireSelectionAddedEvent(cell);
		}
	}
	
	public void removeSelection(PTableCell cell) {
		if (selection.remove(cell)) {
			fireSelectionRemovedEvent(cell);
		}
	}
	
	public void clearSelection() {
		if (!selection.isEmpty()) {
			List<PTableCell> copy = new ArrayList<>(selection);
			selection.clear();
			for (PTableCell cell : copy) {
				fireSelectionRemovedEvent(cell);
			}
		}
	}
	
	public Set<PTableCell> getSelection() {
		return Collections.unmodifiableSet(selection);
	}
	
	public boolean isSelected(PTableCell cell) {
		return selection.contains(cell);
	}
	
}