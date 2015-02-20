package edu.udo.piq.components;

public interface PTreeModelObs {
	
	public void nodeAdded(PTreeModel model, Object parent, Object child, int index);
	
	public void nodeRemoved(PTreeModel model, Object parent, Object child, int index);
	
	public void nodeChanged(PTreeModel model, Object parent, Object child, int index);
	
}