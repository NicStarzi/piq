package edu.udo.piq.comps.selectcomps;

import java.util.AbstractList;

import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPSelection implements PSelection {
	
	protected final ObserverList<PSelectionObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected PModelIndex lastSelected = null;
	
	protected void setLastSelected(PModelIndex index) {
		if (index != lastSelected) {
			PModelIndex prevLastSelected = lastSelected;
			lastSelected = index;
			fireLastSelectionChanged(prevLastSelected, lastSelected);
		}
	}
	
	public PModelIndex getLastSelected() {
		return lastSelected;
	}
	
	public void addObs(PSelectionObs obs) {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.add(obs);
	}
	
	public void removeObs(PSelectionObs obs) {
		if (obs == null) {
			throw new NullPointerException();
		}
		obsList.remove(obs);
	}
	
	protected void fireSelectionAdded(PModelIndex index) {
		for (PSelectionObs obs : obsList) {
			obs.onSelectionAdded(this, index);
		}
	}
	
	protected void fireSelectionRemoved(PModelIndex index) {
		for (PSelectionObs obs : obsList) {
			obs.onSelectionRemoved(this, index);
		}
	}
	
	protected void fireLastSelectionChanged(PModelIndex prevLastIndex, 
			PModelIndex newLastIndex) 
	{
		for (PSelectionObs obs : obsList) {
			obs.onLastSelectedChanged(this, prevLastIndex, newLastIndex);
		}
	}
	
	protected static class SingletonList<K> extends AbstractList<K> {
		
		private K content = null;
		
		public K set(int index, K element) {
			if (index != 0) {
				throw new IndexOutOfBoundsException();
			}
			K oldContent = content;
			content = element;
			return oldContent;
		}
		
		public K get(int index) {
			if (index != 0) {
				throw new IndexOutOfBoundsException();
			}
			return content;
		}
		
		public int size() {
			return content == null ? 0 : 1;
		}
		
	}
	
}