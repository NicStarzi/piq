package edu.udo.piq.components;

public interface PListModelObs {
	
	public void elementAdded(PListModel model, Object element, int index);
	
	public void elementRemoved(PListModel model, Object element, int index);
	
	public void elementChanged(PListModel model, Object element, int index);
	
}