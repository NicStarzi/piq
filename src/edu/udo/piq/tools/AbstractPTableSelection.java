package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTableCell;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.components.PTableSelectionObs;

public abstract class AbstractPTableSelection implements PTableSelection {
	
	private final List<PTableSelectionObs> obsList = new CopyOnWriteArrayList<>();
	private SelectionMode mode = DEFAULT_SELECTION_MODE;
	
	public void setSelectionMode(SelectionMode selectionMode) {
		mode = selectionMode;
		clearSelection();
	}
	
	public SelectionMode getSelectionMode() {
		return mode;
	}
	
	public void addObs(PTableSelectionObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTableSelectionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSelectionAddedEvent(PTableCell cell) {
		for (PTableSelectionObs obs : obsList) {
			obs.selectionAdded(this, cell);
		}
	}
	
	protected void fireSelectionRemovedEvent(PTableCell cell) {
		for (PTableSelectionObs obs : obsList) {
			obs.selectionRemoved(this, cell);
		}
	}
	
}