package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PSliderModel {
	
	public void setValue(int value);
	
	public default void setValuePercent(double value) {
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		int min = getMinValue();
		int max = getMaxValue();
		int intValue = min + (int) (value * (max - min) + 0.5);
		setValue(intValue);
	}
	
	public int getValue();
	
	public default double getValuePercent() {
		int min = getMinValue();
		double value = getValue() - min;
		double max = getMaxValue() - min;
		return value / max;
	}
	
	public void setMinValue(int value);
	
	public int getMinValue();
	
	public void setMaxValue(int value);
	
	public int getMaxValue();
	
	public void setPressed(boolean isPressed);
	
	public boolean isPressed();
	
	public default PModelHistory getHistory() {
		return null;
	}
	
	public void addObs(PSliderModelObs obs);
	
	public void removeObs(PSliderModelObs obs);
	
}