package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PRadioButtonModel;
import edu.udo.piq.components.PRadioButtonModelObs;
import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class DefaultPRadioButtonModel implements PRadioButtonModel {
	
	protected final ObserverList<PRadioButtonModelObs> obsList
		= PiqUtil.createDefaultObserverList();
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
		obsList.fireEvent((obs) -> obs.onChange(this));
	}
	
}