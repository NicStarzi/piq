package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTreeModel;
import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.components.PTreeSelectionObs;

public abstract class AbstractPTreeSelection implements PTreeSelection {
	
	private final List<PTreeSelectionObs> obsList = new CopyOnWriteArrayList<>();
//	private SelectionMode mode = DEFAULT_SELECTION_MODE;
	private PTreeModel model;
	
	public void setModel(PTreeModel model) {
		this.model = model;
		clearSelection();
	}
	
	public PTreeModel getModel() {
		return model;
	}
	
//	public void setSelectionMode(SelectionMode selectionMode) {
//		mode = selectionMode;
//		clearSelection();
//	}
//	
//	public SelectionMode getSelectionMode() {
//		return mode;
//	}
	
	public void addObs(PTreeSelectionObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTreeSelectionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSelectionAddedEvent(Object element) {
		for (PTreeSelectionObs obs : obsList) {
			obs.selectionAdded(this, element);
		}
	}
	
	protected void fireSelectionRemovedEvent(Object element) {
		for (PTreeSelectionObs obs : obsList) {
			obs.selectionRemoved(this, element);
		}
	}
	
}