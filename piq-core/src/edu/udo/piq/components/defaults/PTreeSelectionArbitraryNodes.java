package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.tools.AbstractPTreeSelection;

public class PTreeSelectionArbitraryNodes extends AbstractPTreeSelection implements PTreeSelection {
	
	private final List<Object> nodeList = new ArrayList<>();
	
	public void addSelection(Object node) {
		if (!nodeList.contains(node)) {
			nodeList.add(node);
			fireSelectionAddedEvent(node);
		}
	}
	
	public void removeSelection(Object node) {
		if (nodeList.remove(node)) {
			fireSelectionRemovedEvent(node);
		}
	}
	
	public void clearSelection() {
		if (!nodeList.isEmpty()) {
			Object[] nodes = nodeList.toArray(new Object[nodeList.size()]);
			nodeList.clear();
			for (int i = 0; i < nodes.length; i++) {
				fireSelectionRemovedEvent(nodes[i]);
			}
		}
	}
	
	public List<Object> getSelection() {
		return Collections.unmodifiableList(nodeList);
	}
	
	public boolean isSelected(Object node) {
		return nodeList.contains(node);
	}
	
}