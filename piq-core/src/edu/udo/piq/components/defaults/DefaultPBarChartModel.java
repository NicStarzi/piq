package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PBarChartModel;
import edu.udo.piq.components.PBarChartModelObs;

public class DefaultPBarChartModel implements PBarChartModel {
	
	private final List<PBarChartModelObs> obsList;
	private final int[] barHeights;
	
	public DefaultPBarChartModel(int barCount) {
		obsList = new CopyOnWriteArrayList<>();
		barHeights = new int [barCount];
	}
	
	public int getBarCount() {
		return barHeights.length;
	}
	
	public void setBarValue(int index, int value) {
		barHeights[index] = value;
		fireChangeEvent(index);
	}
	
	public int getBarValue(int index) {
		return barHeights[index];
	}
	
	public void addObs(PBarChartModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PBarChartModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent(int index) {
		for (PBarChartModelObs obs : obsList) {
			obs.barValueChanged(index);
		}
	}
	
}