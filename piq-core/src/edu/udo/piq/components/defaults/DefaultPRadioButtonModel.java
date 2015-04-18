package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PRadioButtonModelObs;
import edu.udo.piq.components.PRadioButtonModel;
import edu.udo.piq.components.util.PModelHistory;

public class DefaultPRadioButtonModel implements PRadioButtonModel {
	
	protected final List<PRadioButtonModelObs> obsList = new CopyOnWriteArrayList<>();
	protected boolean selected;
	
	public void setSelected(final boolean isSelected) {
		final boolean oldIsSelected = selected;
		if (oldIsSelected != isSelected) {
			selected = isSelected;
			fireChangeEvent();
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
	public void addObs(PRadioButtonModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PRadioButtonModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent() {
		for (PRadioButtonModelObs obs : obsList) {
			obs.onChange(this);
		}
	}
	
}