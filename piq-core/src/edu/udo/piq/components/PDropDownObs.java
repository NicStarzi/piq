package edu.udo.piq.components;

public interface PDropDownObs {
	
	public default void onBodyShown(PDropDown dropDown) {}
	
	public default void onBodyHidden(PDropDown dropDown) {}
	
}