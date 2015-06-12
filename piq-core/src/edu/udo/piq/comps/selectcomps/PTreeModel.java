package edu.udo.piq.comps.selectcomps;

public interface PTreeModel extends PModel {
	
	public Object getRoot();
	
	public int getChildCount(PTreeIndex index);
	
}