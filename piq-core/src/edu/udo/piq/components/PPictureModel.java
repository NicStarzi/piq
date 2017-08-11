package edu.udo.piq.components;

public interface PPictureModel {
	
	public void setValue(Object obj);
	
	public Object getValue();
	
	public void addObs(PPictureModelObs obs);
	
	public void removeObs(PPictureModelObs obs);
	
}