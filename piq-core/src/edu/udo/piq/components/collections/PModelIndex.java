package edu.udo.piq.components.collections;

public interface PModelIndex {
	
	public default Object get(PModel model) 
			throws WrongIndexType, IllegalIndex 
	{
		return model.get(this);
	}
	
	public default boolean isContained(PModel model) 
			throws WrongIndexType 
	{
		return model.contains(this);
	}
	
	public default boolean canAdd(PModel model, Object object) 
			throws WrongIndexType 
	{
		return model.canAdd(this, object);
	}
	
	public default void add(PModel model, Object object) 
			throws WrongIndexType, AddImpossible 
	{
		model.add(this, object);
	}
	
	public default boolean canRemove(PModel model) 
			throws WrongIndexType 
	{
		return model.canRemove(this);
	}
	
	public default void remove(PModel model) 
			throws WrongIndexType, RemoveImpossible 
	{
		model.remove(this);
	}
	
}