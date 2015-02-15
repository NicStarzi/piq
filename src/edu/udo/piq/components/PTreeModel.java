package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PTreeModel {
	
	public Object getRoot();
	
	public Object getParentOf(Object child);
	
	public int getChildCount(Object parent);
	
	public Object getChild(Object parent, int childIndex);
	
	public int getChildIndex(Object parent, Object child);
	
	public boolean canAddChild(Object parent, Object child, int index);
	
	public void addChild(Object parent, Object child, int index);
	
	public boolean canRemoveChild(Object parent, int index);
	
	public void removeChild(Object parent, int index);
	
	public PModelHistory getHistory();
	
	public void addObs(PTreeModelObs obs);
	
	public void removeObs(PTreeModelObs obs);
	
}