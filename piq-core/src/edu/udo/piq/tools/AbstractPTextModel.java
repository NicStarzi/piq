package edu.udo.piq.tools;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.textbased.PTextModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPTextModel implements PTextModel {
	
	protected final ObserverList<PTextModelObs> obsList
		= PiqUtil.createDefaultObserverList();
	
	public void addObs(PTextModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTextModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireTextChangeEvent() {
		obsList.fireEvent((obs) -> obs.onTextChanged(this));
	}
	
}