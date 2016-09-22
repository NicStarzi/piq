package edu.udo.piq.components.collections;

public interface PModelObs {
	
	public default void onContentAdded(PReadOnlyModel model, 
			PModelIndex index, Object newContent) {}
	
	public default void onContentRemoved(PReadOnlyModel model, 
			PModelIndex index, Object oldContent) {}
	
	public default void onContentChanged(PReadOnlyModel model, 
			PModelIndex index, Object oldContent) {}
	
	public default void onModelRestructure(PReadOnlyModel model) {}
	
}