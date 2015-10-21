package edu.udo.piq.components.charts;

public interface PLineChartModelObs {
	
	public void dataPointAdded(PLineChartModel model, int index);
	
	public void dataPointRemoved(PLineChartModel model, int index);
	
	public void dataPointChanged(PLineChartModel model, int index);
	
}