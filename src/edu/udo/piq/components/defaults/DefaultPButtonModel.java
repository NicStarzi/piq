package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;

public class DefaultPButtonModel implements PButtonModel {
	
	protected final List<PButtonModelObs> obsList = new CopyOnWriteArrayList<>();
	protected boolean pressed;
	
	public void setPressed(boolean isPressed) {
		if (pressed != isPressed) {
			pressed = isPressed;
			fireChangeEvent();
		}
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void addObs(PButtonModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PButtonModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent() {
		for (PButtonModelObs obs : obsList) {
			obs.onChange(this);
		}
	}
	
}