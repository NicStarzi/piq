package edu.udo.piq.components.containers;

public interface PSplitPanelModel {
	
	public void setSplitPosition(Object value);
	
	public double getSplitPosition();
	
	public void addObs(PSplitPanelModelObs obs);
	
	public void removeObs(PSplitPanelModelObs obs);
	
}