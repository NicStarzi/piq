package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PLabelModel;
import edu.udo.piq.components.PLabelModelObs;

public abstract class AbstractPLabelModel implements PLabelModel {
	
	private final List<PLabelModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PLabelModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PLabelModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireTextChangeEvent() {
		for (PLabelModelObs obs : obsList) {
			obs.textChanged(this);
		}
	}
	
}