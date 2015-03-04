package edu.udo.piq.scroll;

public interface PScrollBarModel {
	
	public void setScroll(double value);
	
	public double getScroll();
	
	public void addSmallStep();
	
	public void addBigStep();
	
	public void subSmallStep();
	
	public void subBigStep();
	
	public void setPreferredSize(int value);
	
	public int getPreferredSize();
	
	public void setSize(int value);
	
	public int getSize();
	
	public void addObs(PScrollBarModelObs obs);
	
	public void removeObs(PScrollBarModelObs obs);
	
}