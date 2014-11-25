package edu.udo.piq.components;

public interface PTextModel {
	
	public void setText(Object text);
	
	public Object getText();
	
	public void addObs(PTextModelObs obs);
	
	public void removeObs(PTextModelObs obs);
	
}