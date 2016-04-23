package edu.udo.piq.components.charts;

public interface PBarChartModel {
	
	public int getBarCount();
	
	public void setBarValue(int index, int value);
	
	public int getBarValue(int index);
	
	public void addObs(PBarChartModelObs obs);
	
	public void removeObs(PBarChartModelObs obs);
	
}