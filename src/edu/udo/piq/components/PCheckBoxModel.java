package edu.udo.piq.components;

public interface PCheckBoxModel {
	
	public void setChecked(boolean value);
	
	public boolean isChecked();
	
	public void addObs(PCheckBoxModelObs obs);
	
	public void removeObs(PCheckBoxModelObs obs);
	
	public void fireClickEvent();
	
	public void fireChangeEvent();
	
}