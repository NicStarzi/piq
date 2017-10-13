package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class DefaultPButtonModel implements PButtonModel {
	
	protected final ObserverList<PButtonModelObs> obsList
		= PiqUtil.createDefaultObserverList();
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
		obsList.fireEvent((obs) -> obs.onChange(this));
	}
	
}