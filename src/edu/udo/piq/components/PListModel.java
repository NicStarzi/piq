package edu.udo.piq.components;

public interface PListModel {
	
	public int getElementCount();
	
	public Object getElement(int index);
	
	public void addObs(PListModelObs obs);
	
	public void removeObs(PListModelObs obs);
	
}