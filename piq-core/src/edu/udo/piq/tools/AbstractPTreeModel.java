package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTreeModel;
import edu.udo.piq.components.PTreeModelObs;

public abstract class AbstractPTreeModel implements PTreeModel {
	
	protected final List<PTreeModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public boolean canAddChild(Object parent, Object child) {
		return canAddChild(parent, child, getChildCount(parent));
	}
	
	public void addChild(Object parent, Object child) {
		addChild(parent, child, getChildCount(parent));
	}
	
	public boolean canRemoveChild(Object parent, Object child) {
		return canRemoveChild(parent, getChildIndex(parent, child));
	}
	
	public void removeChild(Object parent, Object child) {
		removeChild(parent, getChildIndex(parent, child));
	}
	
	public void addObs(PTreeModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTreeModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireAddedEventForBranch(Object parent, Object child, int index) {
		fireAddedEvent(parent, child, index);
		Object current = child;
		for (int i = 0; i < getChildCount(current); i++) {
			Object grandChild = getChild(current, i);
			fireAddedEventForBranch(child, grandChild, i);
		}
	}
	
	protected void fireAddedEvent(Object parent, Object child, int index) {
		for (PTreeModelObs obs : obsList) {
			obs.nodeAdded(this, parent, child, index);
		}
	}
	
	protected void fireRemovedEventForBranch(Object parent, Object child, int index) {
		Object current = child;
		for (int i = 0; i < getChildCount(current); i++) {
			Object grandChild = getChild(current, i);
			fireRemovedEventForBranch(child, grandChild, i);
		}
		fireRemovedEvent(parent, child, index);
	}
	
	protected void fireRemovedEvent(Object parent, Object child, int index) {
		for (PTreeModelObs obs : obsList) {
			obs.nodeRemoved(this, parent, child, index);
		}
	}
	
	protected void fireChangedEventForBranch(Object parent, Object child, int index) {
		fireChangedEvent(parent, child, index);
		Object current = child;
		for (int i = 0; i < getChildCount(current); i++) {
			Object grandChild = getChild(current, i);
			fireChangedEventForBranch(child, grandChild, i);
		}
	}
	
	protected void fireChangedEvent(Object parent, Object child, int index) {
		for (PTreeModelObs obs : obsList) {
			obs.nodeChanged(this, parent, child, index);
		}
	}
	
}