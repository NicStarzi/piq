package edu.udo.piq.style;

import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class MutablePStyle implements PStyle {
	
	protected final ObserverList<PStyleObs> obsList = PiqUtil.createDefaultObserverList();
	
	@Override
	public void addObs(PStyleObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PStyleObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireReRenderEvent() {
		obsList.fireEvent(obs -> obs.onReRenderEvent());
	}
	
	protected void fireSizeChangedEvent() {
		obsList.fireEvent(obs -> obs.onSizeChangedEvent());
	}
	
}