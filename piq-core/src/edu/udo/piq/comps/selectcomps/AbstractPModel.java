package edu.udo.piq.comps.selectcomps;

import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPModel implements PModel {
	
	protected final ObserverList<PModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	public PModelHistory getHistory() {
		return null;
	}
	
	public void addObs(PModelObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.add(obs);
	}
	
	public void removeObs(PModelObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.remove(obs);
	}
	
	protected void fireAddEvent(PModelIndex index, Object content) {
		for (PModelObs obs : obsList) {
			obs.onContentAdded(this, index, content);
		}
	}
	
	protected void fireRemoveEvent(PModelIndex index, Object content) {
		for (PModelObs obs : obsList) {
			obs.onContentRemoved(this, index, content);
		}
	}
	
	public void fireChangeEvent(PModelIndex index, Object content) {
		for (PModelObs obs : obsList) {
			obs.onContentChanged(this, index, content);
		}
	}
	
}