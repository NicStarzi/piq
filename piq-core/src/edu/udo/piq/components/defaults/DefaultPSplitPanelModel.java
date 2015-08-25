package edu.udo.piq.components.defaults;

import edu.udo.piq.components.containers.PSplitPanelModel;
import edu.udo.piq.components.containers.PSplitPanelModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class DefaultPSplitPanelModel implements PSplitPanelModel {
	
	protected final ObserverList<PSplitPanelModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected double position;
	
	public void setSplitPosition(Object value) {
		if (value == null) {
			throw new NullPointerException("value="+value);
		}
		if (!(value instanceof Double)) {
			throw new IllegalArgumentException("value="+value);
		}
		double newValue = ((Double) value).doubleValue();
		if (newValue < 0) {
			newValue = 0;
		} else if (newValue > 1) {
			newValue = 1;
		}
		if (position != newValue) {
			position = newValue;
			firePositionChangedEvent();
		}
	}
	
	public double getSplitPosition() {
		return position;
	}
	
	public void addObs(PSplitPanelModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PSplitPanelModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void firePositionChangedEvent() {
		for (PSplitPanelModelObs obs : obsList) {
			obs.positionChanged(this);
		}
	}
	
}