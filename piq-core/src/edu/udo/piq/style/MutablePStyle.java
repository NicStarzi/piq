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
		fireReRenderEvent(null);
	}
	
	protected void fireReRenderEvent(PStyleable<?> styleable) {
		obsList.fireEvent(obs -> obs.onReRenderEvent(styleable));
	}
	
	protected void fireSizeChangedEvent() {
		fireSizeChangedEvent(null);
	}
	
	protected void fireSizeChangedEvent(PStyleable<?> styleable) {
		obsList.fireEvent(obs -> obs.onSizeChangedEvent(styleable));
	}
	
}