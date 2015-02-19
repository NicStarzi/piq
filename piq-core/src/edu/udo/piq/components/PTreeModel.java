package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PTreeModel {
	
	public void setRoot(Object node);
	
	public Object getRoot();
	
	public Object getParentOf(Object child);
	
	public int getChildCount(Object parent);
	
	public Object getChild(Object parent, int childIndex);
	
	public int getChildIndex(Object parent, Object child);
	
	public boolean canAddChild(Object parent, Object child, int index);
	
	public void addChild(Object parent, Object child, int index);
	
	public boolean canRemoveChild(Object parent, int index);
	
	public void removeChild(Object parent, int index);
	
	public default PTreePosition getPositionFor(Object node) {
		return new PTreePosition(this, node);
	}
	
	public default PTreePosition getPositionAt(Object parent, int index) {
		return new PTreePosition(this, parent, index);
	}
	
	public PModelHistory getHistory();
	
	public void addObs(PTreeModelObs obs);
	
	public void removeObs(PTreeModelObs obs);
	
}