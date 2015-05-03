package edu.udo.piq.experimental;

public interface PModelObs {
	
	public void onContentAdded(PModel model, PModelIndex index, Object newContent);
	
	public void onContentRemoved(PModel model, PModelIndex index, Object oldContent);
	
	public void onContentChanged(PModel model, PModelIndex index, Object oldContent);
	
}