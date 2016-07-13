package edu.udo.piq.components;

public interface PSliderModelObs {
	
	public default void onRangeChanged(PSliderModel model) {}
	
	public default void onValueChanged(PSliderModel model) {}
	
}