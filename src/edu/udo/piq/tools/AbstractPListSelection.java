package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PListSelection;
import edu.udo.piq.components.PListSelectionObs;

public abstract class AbstractPListSelection implements PListSelection {
	
	private final List<PListSelectionObs> obsList = new CopyOnWriteArrayList<>();
	private SelectionMode mode = DEFAULT_SELECTION_MODE;
	
	public void setSelectionMode(SelectionMode selectionMode) {
		mode = selectionMode;
		clearSelection();
	}
	
	public SelectionMode getSelectionMode() {
		return mode;
	}
	
	public void addObs(PListSelectionObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PListSelectionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSelectionAddedEvent(Object element) {
		for (PListSelectionObs obs : obsList) {
			obs.selectionAdded(this, element);
		}
	}
	
	protected void fireSelectionRemovedEvent(Object element) {
		for (PListSelectionObs obs : obsList) {
			obs.selectionRemoved(this, element);
		}
	}
	
}