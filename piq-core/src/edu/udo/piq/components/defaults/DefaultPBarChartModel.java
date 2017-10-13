package edu.udo.piq.components.defaults;

import java.util.ArrayList;

import edu.udo.piq.components.charts.PBarChartModel;
import edu.udo.piq.components.charts.PBarChartModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class DefaultPBarChartModel implements PBarChartModel {
	
	protected final ObserverList<PBarChartModelObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final ArrayList<Integer> barValues = new ArrayList<>();
//	private final int[] barHeights;
	
	public DefaultPBarChartModel() {
		this(0);
	}
	
	public DefaultPBarChartModel(int barCount) {
		barValues.ensureCapacity(barCount);
		
		Integer initialValue = Integer.valueOf(0);
		for (int i = 0; i < barCount; i++) {
			barValues.add(initialValue);
		}
	}
	
	public int getBarCount() {
		return barValues.size();
	}
	
	public void setBarValue(int index, int value) {
		barValues.set(index, Integer.valueOf(value));
		fireChangeEvent(index);
	}
	
	public int getBarValue(int index) {
		ThrowException.ifNotWithin(barValues, index, 
				"index < 0 || index >= getBarCount()");
		return barValues.get(index).intValue();
	}
	
	public void addObs(PBarChartModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PBarChartModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent(int index) {
		obsList.fireEvent((obs) -> obs.onBarValueChanged(this, index));
	}
	
}