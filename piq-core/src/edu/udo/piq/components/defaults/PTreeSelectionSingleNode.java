package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.tools.AbstractPTreeSelection;

public class PTreeSelectionSingleNode extends AbstractPTreeSelection implements PTreeSelection {
	
	private final List<Object> oneElemList = Arrays.asList(new Object[] {null});
	
	public void addSelection(Object node) {
		clearSelection();
		oneElemList.set(0, node);
		fireSelectionAddedEvent(node);
	}
	
	public void removeSelection(Object node) {
		if (isSelected(node)) {
			clearSelection();
		}
	}
	
	public void clearSelection() {
		Object elem = getNode();
		if (elem != null) {
			oneElemList.set(0, null);
			fireSelectionRemovedEvent(elem);
		}
	}
	
	public List<Object> getSelection() {
		return Collections.unmodifiableList(oneElemList);
	}
	
	public boolean isSelected(Object node) {
		return oneElemList.get(0) == node;
	}
	
	protected Object getNode() {
		return oneElemList.get(0);
	}
	
}