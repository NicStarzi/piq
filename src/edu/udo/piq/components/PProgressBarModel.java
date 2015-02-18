package edu.udo.piq.components;

public interface PProgressBarModel {
	
	public void setValue(Object value);
	
	public int getValue();
	
	public void setMaximum(Object value);
	
	public int getMaxValue();
	
	public void addObs(PProgressBarModelObs obs);
	
	public void removeObs(PProgressBarModelObs obs);
	
}