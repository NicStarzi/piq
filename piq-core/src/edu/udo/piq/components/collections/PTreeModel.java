package edu.udo.piq.components.collections;

public interface PTreeModel extends PModel {
	
	public Object getRoot();
	
	public int getChildCount(PTreeIndex index);
	
}