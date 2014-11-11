package edu.udo.piq.components;

public interface PLabelModel {
	
	public void setText(Object text);
	
	public Object getText();
	
	public void addObs(PLabelModelObs obs);
	
	public void removeObs(PLabelModelObs obs);
	
}