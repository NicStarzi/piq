package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTablePosition;
import edu.udo.piq.components.PTableModel;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.components.PTableSelectionObs;

public abstract class AbstractPTableSelection implements PTableSelection {
	
	private final List<PTableSelectionObs> obsList = new CopyOnWriteArrayList<>();
	private SelectionMode mode = DEFAULT_SELECTION_MODE;
	private PTableModel model;
	
	public void setSelectionMode(SelectionMode selectionMode) {
		clearSelection();
		mode = selectionMode;
	}
	
	public SelectionMode getSelectionMode() {
		return mode;
	}
	
	public void setModel(PTableModel model) {
		clearSelection();
		this.model = model;
	}
	
	public PTableModel getModel() {
		return model;
	}
	
	public void addObs(PTableSelectionObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTableSelectionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSelectionAddedEvent(PTablePosition cell) {
		for (PTableSelectionObs obs : obsList) {
			obs.selectionAdded(this, cell);
		}
	}
	
	protected void fireSelectionRemovedEvent(PTablePosition cell) {
		for (PTableSelectionObs obs : obsList) {
			obs.selectionRemoved(this, cell);
		}
	}
	
}