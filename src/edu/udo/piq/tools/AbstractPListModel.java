package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PListModel;
import edu.udo.piq.components.PListModelObs;

public abstract class AbstractPListModel implements PListModel {
	
	protected final List<PListModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PListModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PListModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireAddedEvent(Object element, Integer index) {
		for (PListModelObs obs : obsList) {
			obs.elementAdded(this, element, index);
		}
	}
	
	protected void fireRemovedEvent(Object element, Integer index) {
		for (PListModelObs obs : obsList) {
			obs.elementRemoved(this, element, index);
		}
	}
	
	protected void fireChangedEvent(Object element, Integer index) {
		for (PListModelObs obs : obsList) {
			obs.elementChanged(this, element, index);
		}
	}
}