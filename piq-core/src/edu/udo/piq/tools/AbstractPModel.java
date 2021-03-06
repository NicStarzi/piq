package edu.udo.piq.tools;

import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPModel implements PModel {
	
	protected final ObserverList<PModelObs> obsList
		= PiqUtil.createDefaultObserverList();
	
	@Override
	public void addObs(PModelObs obs) throws NullPointerException {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PModelObs obs) throws NullPointerException {
		obsList.remove(obs);
	}
	
	protected void fireAddEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentAdded(this, index, content));
	}
	
	protected void fireRemoveEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentRemoved(this, index, content));
	}
	
	protected void fireRestructureEvent() {
		obsList.fireEvent((obs) -> obs.onModelRestructure(this));
	}
	
	public void fireChangeEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentChanged(this, index, content));
	}
	
}