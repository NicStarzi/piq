package edu.udo.piq.tools;

import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPModel implements PModel {
	
	protected final ObserverList<PModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
//	protected abstract PModel createEmptyInstance();
	
	public PModelHistory getHistory() {
		return null;
	}
	
//	public PModel createCopy(Iterable<PModelIndex> indices)
//			throws NullPointerException, WrongIndexType, IllegalIndex 
//	{
//		ThrowException.ifNull(indices, "indices == null");
//		PModel copy = createEmptyInstance();
//		for (PModelIndex index : this) {
//			Object content = this.get(index);
//			copy.add(index, content);
//		}
//		return copy;
//	}
	
	public void addObs(PModelObs obs) throws NullPointerException {
		obsList.add(obs);
	}
	
	public void removeObs(PModelObs obs) throws NullPointerException {
		obsList.remove(obs);
	}
	
	protected void fireAddEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentAdded(this, index, content));
	}
	
	protected void fireRemoveEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentRemoved(this, index, content));
	}
	
	public void fireChangeEvent(PModelIndex index, Object content) {
		obsList.fireEvent((obs) -> obs.onContentChanged(this, index, content));
	}
	
}