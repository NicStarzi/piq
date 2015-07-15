package edu.udo.piq.tools;

import edu.udo.piq.components.PTextModel;
import edu.udo.piq.components.PTextSelection;
import edu.udo.piq.components.PTextSelectionObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPTextSelection implements PTextSelection {
	
	protected final ObserverList<PTextSelectionObs> obsList
		= PCompUtil.createDefaultObserverList();
	private PTextModel model;
	
	public void setModel(PTextModel model) {
		this.model = model;
		clearSelection();
	}
	
	public PTextModel getModel() {
		return model;
	}
	
	public void addObs(PTextSelectionObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTextSelectionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSelectionAddedEvent(int index) {
		for (PTextSelectionObs obs : obsList) {
			obs.selectionAdded(this, index);
		}
	}
	
	protected void fireSelectionRemovedEvent(int index) {
		for (PTextSelectionObs obs : obsList) {
			obs.selectionRemoved(this, index);
		}
	}
	
	protected void fireSelectionChangedEvent() {
		for (PTextSelectionObs obs : obsList) {
			obs.selectionChanged(this);
		}
	}
	
}