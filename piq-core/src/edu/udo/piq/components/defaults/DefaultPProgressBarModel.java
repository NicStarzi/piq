package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PProgressBarModel;
import edu.udo.piq.components.PProgressBarModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class DefaultPProgressBarModel implements PProgressBarModel {
	
	public static final int DEFAULT_MAX_VALUE = 100;
	public static final int DEFAULT_VALUE = 0;
	
	protected final ObserverList<PProgressBarModelObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected int maxValue = DEFAULT_MAX_VALUE;
	protected int value = DEFAULT_VALUE;
	
	public void addValue(int value) {
		setValue(getValue() + value);
	}
	
	@Override
	public void setValue(Object valAsObj) {
		int value = ((Integer) valAsObj).intValue();
		if (value < 0) {
			value = 0;
		}
		int max = getMaxValue();
		if (value >= max) {
			value = max;
		}
		if (getValue() != value) {
			this.value = value;
			fireValueChangedEvent();
		}
	}
	
	@Override
	public int getValue() {
		return value;
	}
	
	@Override
	public void setMaximum(Object valAsObj) {
		int value = ((Integer) valAsObj).intValue();
		if (value <= 0) {
			throw new IllegalArgumentException("value="+value);
		}
		maxValue = value;
	}
	
	@Override
	public int getMaxValue() {
		return maxValue;
	}
	
	@Override
	public void addObs(PProgressBarModelObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PProgressBarModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireValueChangedEvent() {
		obsList.fireEvent((obs) -> obs.onValueChanged(this));
	}
	
}