package edu.udo.piq.scroll;

public interface PScrollBarModelObs {
	
	public void scrollChanged(PScrollBarModel model, double oldValue, double newValue);
	
	public void preferredSizeChanged(PScrollBarModel model, int oldValue, int newValue);
	
	public void sizeChanged(PScrollBarModel model, int oldValue, int newValue);
	
}