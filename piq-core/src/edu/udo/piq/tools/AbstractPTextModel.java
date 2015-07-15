package edu.udo.piq.tools;

import edu.udo.piq.components.PTextModel;
import edu.udo.piq.components.PTextModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPTextModel implements PTextModel {
	
	protected final ObserverList<PTextModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
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