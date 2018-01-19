package edu.udo.piq.components;

import java.util.Objects;

import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPSingleValueModel implements PSingleValueModel {
	
	protected final ObserverList<PSingleValueModelObs> obsList
		= PiqUtil.createDefaultObserverList();
	
	@Override
	public void setValue(Object value) {
		Object oldValue = getValue();
		if (!Objects.equals(value, oldValue)) {
			setValueInternal(value);
			if (!Objects.equals(getValue(), oldValue)) {
				fireChangeEvent(oldValue);
			}
		}
	}
	
	protected abstract void setValueInternal(Object newValue);
	
	@Override
	public void addObs(PSingleValueModelObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PSingleValueModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent(Object oldValue) {
		Object newValue = getValue();
		obsList.fireEvent(obs -> obs.onValueChanged(this, oldValue, newValue));
	}
	
}