package edu.udo.piq.experimental;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PAbstractModel implements PModel {
	
	protected final List<PModelObs> obsList = new CopyOnWriteArrayList<>();
	
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
	
	protected void fireChangeEvent(PModelIndex index, Object content) {
		for (PModelObs obs : obsList) {
			obs.onContentChanged(this, index, content);
		}
	}
	
}