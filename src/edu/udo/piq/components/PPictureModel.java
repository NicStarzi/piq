package edu.udo.piq.components;

public interface PPictureModel {
	
	public void setImagePath(String path);
	
	public String getImagePath();
	
	public void addObs(PPictureModelObs obs);
	
	public void removeObs(PPictureModelObs obs);
	
}