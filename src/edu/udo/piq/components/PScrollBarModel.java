package edu.udo.piq.components;

public interface PScrollBarModel {
	
	public void setContentSize(int value);
	
	public int getContentSize();
	
	public void setViewportSize(int value);
	
	public int getViewportSize();
	
	public void setScroll(int value);
	
	public int getScroll();
	
	public int getMaxScroll();
	
	public void addObs(PScrollBarModelObs obs);
	
	public void removeObs(PScrollBarModelObs obs);
	
}