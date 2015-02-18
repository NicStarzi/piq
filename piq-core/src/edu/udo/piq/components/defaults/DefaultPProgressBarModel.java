package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PProgressBarModel;
import edu.udo.piq.components.PProgressBarModelObs;

public class DefaultPProgressBarModel implements PProgressBarModel {
	
	public static final int DEFAULT_MAX_VALUE = 100;
	public static final int DEFAULT_VALUE = 0;
	
	private final List<PProgressBarModelObs> obsList = new CopyOnWriteArrayList<>();
	private int maxValue = DEFAULT_MAX_VALUE;
	private int value = DEFAULT_VALUE;
	
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
	
	public int getValue() {
		return value;
	}
	
	public void setMaximum(Object valAsObj) {
		int value = ((Integer) valAsObj).intValue();
		if (value <= 0) {
			throw new IllegalArgumentException("value="+value);
		}
		this.maxValue = value;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void addObs(PProgressBarModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PProgressBarModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireValueChangedEvent() {
		for (PProgressBarModelObs obs : obsList) {
			obs.valueChanged(this);
		}
	}
	
}