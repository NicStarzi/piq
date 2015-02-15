package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PListModel;
import edu.udo.piq.components.PListModelObs;
import edu.udo.piq.components.util.PModelHistory;

public abstract class AbstractPListModel implements PListModel {
	
	protected final List<PListModelObs> obsList = new CopyOnWriteArrayList<>();
//	protected final PModelHistory history = new PModelHistory();
	
	public void addElement(Object element) {
		addElement(getElementCount(), element);
	}
	
	public void removeElement(Object element) {
		int index = getIndexOfElement(element);
		if (index == -1) {
			throw new IllegalArgumentException(element+" is not contained");
		}
		removeElement(index);
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
	public void addObs(PListModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PListModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireAddedEvent(Object element, int index) {
		for (PListModelObs obs : obsList) {
			obs.elementAdded(this, element, index);
		}
	}
	
	protected void fireRemovedEvent(Object element, int index) {
		for (PListModelObs obs : obsList) {
			obs.elementRemoved(this, element, index);
		}
	}
	
	protected void fireChangedEvent(Object element, int index) {
		for (PListModelObs obs : obsList) {
			obs.elementChanged(this, element, index);
		}
	}
}