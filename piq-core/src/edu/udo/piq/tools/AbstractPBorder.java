package edu.udo.piq.tools;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBorderObs;
import edu.udo.piq.style.AbstractPStylable;
import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPBorder extends AbstractPStylable<PStyleBorder> implements PBorder {
	
	protected final ObserverList<PBorderObs> obsList = PiqUtil.createDefaultObserverList();
	
	@Override
	protected void onStyleReRenderEvent() {
		fireReRenderEvent();
	}
	
	@Override
	protected void onStyleSizeChangedEvent() {
		fireInsetsChangedEvent();
	}
	
	@Override
	public void addObs(PBorderObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PBorderObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireInsetsChangedEvent() {
		obsList.fireEvent(obs -> obs.onInsetsChanged(this));
	}
	
	protected void fireReRenderEvent() {
		obsList.fireEvent(obs -> obs.onReRender(this));
	}
	
}