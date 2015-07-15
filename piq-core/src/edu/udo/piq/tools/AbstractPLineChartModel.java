package edu.udo.piq.tools;

import edu.udo.piq.components.PLineChartModel;
import edu.udo.piq.components.PLineChartModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPLineChartModel implements PLineChartModel {
	
	protected final ObserverList<PLineChartModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	public boolean canAddDataPoint(Object value) {
		return canAddDataPoint(getDataCount(), value);
	}
	
	public void addDataPoint(Object value) {
		addDataPoint(getDataCount(), value);
	}
	
	public void addObs(PLineChartModelObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.add(obs);
	}
	
	public void removeObs(PLineChartModelObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.remove(obs);
	}
	
	protected void fireDataAddedEvent(int index) {
		for (PLineChartModelObs obs : obsList) {
			obs.dataPointAdded(this, index);
		}
	}
	
	protected void fireDataRemovedEvent(int index) {
		for (PLineChartModelObs obs : obsList) {
			obs.dataPointRemoved(this, index);
		}
	}
	
	protected void fireDataChangedEvent(int index) {
		for (PLineChartModelObs obs : obsList) {
			obs.dataPointChanged(this, index);
		}
	}
	
}