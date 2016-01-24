package edu.udo.piq.components.charts;

public interface PLineChartModelObs {
	
	public void onDataPointAdded(PLineChartModel model, int index);
	
	public void onDataPointRemoved(PLineChartModel model, int index);
	
	public void onDataPointChanged(PLineChartModel model, int index);
	
}