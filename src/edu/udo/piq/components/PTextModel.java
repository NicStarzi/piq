package edu.udo.piq.components;

public interface PTextModel {
	
	public void setValue(Object value);
	
	public Object getValue();
	
	public String getText();
	
	public void addObs(PTextModelObs obs);
	
	public void removeObs(PTextModelObs obs);
	
}