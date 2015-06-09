package edu.udo.piq.comps.selectcomps;

public interface PListModel extends PModel {
	
	public int getSize();
	
	public Object get(int indexVal);
	
	public boolean contains(int index);
	
	public boolean canAdd(int index, Object content);
	
	public void add(int index, Object content);
	
	public boolean canRemove(int index);
	
	public void remove(int index);
	
}