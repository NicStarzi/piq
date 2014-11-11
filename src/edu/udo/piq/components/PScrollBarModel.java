package edu.udo.piq.components;

public interface PScrollBarModel {
	
	public int getContentSize();
	
	public int getViewportSize();
	
	public void setScroll(int value);
	
	public int getScroll();
	
	public void addObs(PScrollBarModelObs obs);
	
	public void removeObs(PScrollBarModelObs obs);
	
}