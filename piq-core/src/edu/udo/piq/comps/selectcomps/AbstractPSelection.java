package edu.udo.piq.comps.selectcomps;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractPSelection implements PSelection {
	
	protected final List<PSelectionObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PSelectionObs obs) {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.add(obs);
	}
	
	public void removeObs(PSelectionObs obs) {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.remove(obs);
	}
	
	protected void fireSelectionAdded(PModelIndex index) {
		for (PSelectionObs obs : obsList) {
			obs.onSelectionAdded(this, index);
		}
	}
	
	protected void fireSelectionRemoved(PModelIndex index) {
		for (PSelectionObs obs : obsList) {
			obs.onSelectionRemoved(this, index);
		}
	}
	
}