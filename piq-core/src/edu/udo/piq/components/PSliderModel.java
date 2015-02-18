package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PSliderModel {
	
	public void setValue(int value);
	
	public void setValuePercent(double value);
	
	public int getValue();
	
	public double getValuePercent();
	
	public void setMinValue(int value);
	
	public int getMinValue();
	
	public void setMaxValue(int value);
	
	public int getMaxValue();
	
	public void setPressed(boolean isPressed);
	
	public boolean isPressed();
	
	public PModelHistory getHistory();
	
	public void addObs(PSliderModelObs obs);
	
	public void removeObs(PSliderModelObs obs);
	
}