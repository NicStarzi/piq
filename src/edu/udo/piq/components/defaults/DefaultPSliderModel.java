package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;

public class DefaultPSliderModel implements PSliderModel {
	
	protected final List<PSliderModelObs> obsList = new CopyOnWriteArrayList<>();
	protected boolean pressed;
	protected int value = 0;
	protected int max = 100;
	protected int min = 0;
	
	public void setValue(int value) {
		value = adjustValue(value);
		if (this.value != value) {
			this.value = value;
			fireValueChangedEvent();
		}
	}
	
	public void setValuePercent(double value) {
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		int min = getMinValue();
		int max = getMaxValue();
		int inValue = min + (int) (value * (max - min) + 0.5);
		setValue(inValue);
	}
	
	public int getValue() {
		return value;
	}
	
	public double getValuePercent() {
		int min = getMinValue();
		double value = getValue() - min;
		double max = getMaxValue() - min;
		return value / max;
	}
	
	public void setMinValue(int value) {
		if (min > max) {
			throw new IllegalArgumentException("Min value must not be bigger than max value: min="+min+", max="+max);
		}
		if (min != value) {
			min = value;
			adjustValueToRange();
			fireBoundsChangedEvent();
		}
	}
	
	public int getMinValue() {
		return min;
	}
	
	public void setMaxValue(int value) {
		if (max < min) {
			throw new IllegalArgumentException("Max value must not be less than min value: max="+max+", min="+min);
		}
		if (max != value) {
			max = value;
			adjustValueToRange();
			fireBoundsChangedEvent();
		}
	}
	
	public int getMaxValue() {
		return max;
	}
	
	protected int adjustValue(int value) {
		if (value < getMinValue()) {
			value = getMinValue();
		} else if (value > getMaxValue()) {
			value = getMaxValue();
		}
		return value;
	}
	
	protected void adjustValueToRange() {
		if (value < getMinValue()) {
			setValue(getMinValue());
		} else if (value > getMaxValue()) {
			setValue(getMaxValue());
		}
	}
	
	public void setPressedNoEvent(boolean isPressed) {
		pressed = isPressed;
	}
	
	public void setPressed(boolean isPressed) {
		if (pressed != isPressed) {
			pressed = isPressed;
		}
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void addObs(PSliderModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PSliderModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireValueChangedEvent() {
		for (PSliderModelObs obs : obsList) {
			obs.valueChanged(this);
		}
	}
	
	protected void fireBoundsChangedEvent() {
		for (PSliderModelObs obs : obsList) {
			obs.boundsChanged(this);
		}
	}
	
}