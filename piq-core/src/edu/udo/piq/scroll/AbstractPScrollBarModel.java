package edu.udo.piq.scroll;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractPScrollBarModel implements PScrollBarModel {
	
	private final List<PScrollBarModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PScrollBarModelObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.add(obs);
	}
	
	public void removeObs(PScrollBarModelObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.remove(obs);
	}
	
	protected void fireScrollChangedEvent(double oldValue) {
		for (PScrollBarModelObs obs : obsList) {
			obs.scrollChanged(this, oldValue, getScroll());
		}
	}
	
	protected void firePreferredSizeChangedEvent(int oldValue) {
		for (PScrollBarModelObs obs : obsList) {
			obs.preferredSizeChanged(this, oldValue, getPreferredSize());
		}
	}
	
	protected void fireSizeChangedEvent(int oldValue) {
		for (PScrollBarModelObs obs : obsList) {
			obs.sizeChanged(this, oldValue, getSize());
		}
	}
	
}