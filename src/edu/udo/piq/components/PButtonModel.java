package edu.udo.piq.components;

public interface PButtonModel {
	
	public void setPressed(boolean isPressed);
	
	public boolean isPressed();
	
	public void addObs(PButtonModelObs obs);
	
	public void removeObs(PButtonModelObs obs);
	
}