package edu.udo.piq.components.collections;

public interface PModelObs {
	
	public default void onContentAdded(PModel model, 
			PModelIndex index, Object newContent) {}
	
	public default void onContentRemoved(PModel model, 
			PModelIndex index, Object oldContent) {}
	
	public default void onContentChanged(PModel model, 
			PModelIndex index, Object oldContent) {}
	
}