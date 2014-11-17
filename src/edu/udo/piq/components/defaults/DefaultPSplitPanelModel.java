package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PSplitPanelModel;
import edu.udo.piq.components.PSplitPanelModelObs;

public class DefaultPSplitPanelModel implements PSplitPanelModel {
	
	private final List<PSplitPanelModelObs> obsList = new CopyOnWriteArrayList<>();
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