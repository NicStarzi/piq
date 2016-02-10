package edu.udo.piq.components;

public interface PPictureModel {
	
	public void setImageID(Object obj);
	
	public Object getImageID();
	
	public void addObs(PPictureModelObs obs);
	
	public void removeObs(PPictureModelObs obs);
	
}