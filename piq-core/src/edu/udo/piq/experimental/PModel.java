package edu.udo.piq.experimental;

public interface PModel extends Iterable<Object> {
	
	public Object get(PModelIndex index) 
			throws WrongIndexType, IllegalIndex;
	
	public boolean contains(PModelIndex index) 
			throws WrongIndexType;
	
	public PModelIndex getIndexOf(Object content);
	
	public boolean canAdd(PModelIndex index, Object content) 
			throws WrongIndexType;
	
	public void add(PModelIndex index, Object content) 
			throws WrongIndexType, AddImpossible;
	
	public boolean canRemove(PModelIndex index) 
			throws WrongIndexType;
	
	public void remove(PModelIndex index) 
			throws WrongIndexType, RemoveImpossible;
	
	public void addObs(PModelObs obs) 
			throws NullPointerException;
	
	public void removeObs(PModelObs obs) 
			throws NullPointerException;
	
}