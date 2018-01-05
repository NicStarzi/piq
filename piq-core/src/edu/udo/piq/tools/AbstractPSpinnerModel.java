package edu.udo.piq.tools;

import edu.udo.piq.components.PSpinnerModel;
import edu.udo.piq.components.PSpinnerModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPSpinnerModel implements PSpinnerModel {
	
	protected final ObserverList<PSpinnerModelObs> obsList =
			PiqUtil.createDefaultObserverList();
	protected boolean enabled = true;
	
	@Override
	public void setEnabled(boolean value) {
		if (enabled != value) {
			enabled = value;
			fireValueChangedEvent(getValue());
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void addObs(PSpinnerModelObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PSpinnerModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireValueChangedEvent(Object oldValue) {
		obsList.fireEvent(obs -> obs.onValueChanged(this, oldValue));
	}
	
}