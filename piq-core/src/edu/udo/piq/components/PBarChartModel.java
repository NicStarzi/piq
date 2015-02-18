package edu.udo.piq.components;

public interface PBarChartModel {
	
	public int getBarCount();
	
	public int getBarValue(int index);
	
	public void addObs(PBarChartModelObs obs);
	
	public void removeObs(PBarChartModelObs obs);
	
}