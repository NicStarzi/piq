package edu.udo.piq.tools;

import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPCheckBoxModel implements PCheckBoxModel {
	
	protected final ObserverList<PCheckBoxModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	public void addObs(PCheckBoxModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent() {
		obsList.fireEvent((obs) -> obs.onChange(this));
	}
	
}