package edu.udo.piq.components;

import java.util.Objects;

import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPSingleValueModel<VALUE_TYPE> implements PSingleValueModel<VALUE_TYPE> {
	
	protected final ObserverList<PSingleValueModelObs<VALUE_TYPE>> obsList
		= PiqUtil.createDefaultObserverList();
	
	@Override
	public void setValue(Object value) {
		VALUE_TYPE oldValue = getValue();
		if (!Objects.equals(value, oldValue)) {
			setValueInternal(value);
			if (!Objects.equals(getValue(), oldValue)) {
				fireChangeEvent(oldValue);
			}
		}
	}
	
	protected abstract void setValueInternal(Object newValue);
	
	@Override
	public void addObs(PSingleValueModelObs<VALUE_TYPE> obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PSingleValueModelObs<VALUE_TYPE> obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent(VALUE_TYPE oldValue) {
		VALUE_TYPE newValue = getValue();
		obsList.fireEvent(obs -> obs.onValueChanged(this, oldValue, newValue));
	}
	
}