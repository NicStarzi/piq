package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PBarChartModel;
import edu.udo.piq.components.PBarChartModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class DefaultPBarChartModel implements PBarChartModel {
	
	protected final ObserverList<PBarChartModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final int[] barHeights;
	
	public DefaultPBarChartModel(int barCount) {
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