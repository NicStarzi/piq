package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTextModel;
import edu.udo.piq.components.PTextModelObs;

public abstract class AbstractPTextModel implements PTextModel {
	
	private final List<PTextModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PTextModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTextModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireTextChangeEvent() {
		for (PTextModelObs obs : obsList) {
			obs.textChanged(this);
		}
	}
	
}