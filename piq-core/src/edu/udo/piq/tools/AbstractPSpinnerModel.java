package edu.udo.piq.tools;

import edu.udo.piq.components.PSpinnerModel;
import edu.udo.piq.components.PSpinnerModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPSpinnerModel implements PSpinnerModel {
	
	protected final ObserverList<PSpinnerModelObs> obsList = 
			PiqUtil.createDefaultObserverList();
	
	public void addObs(PSpinnerModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PSpinnerModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireValueChangedEvent(Object oldValue) {
		obsList.fireEvent((obs) -> obs.onValueChanged(this, oldValue));
	}
	
}