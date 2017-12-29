package edu.udo.piq.components.popup2;

public interface PMenuBodyObs {
	
	public void onMenuItemAction(PMenuBody body, AbstractPMenuItem item, int itemIndex);
	
	public void onCloseRequest(PMenuBody body);
	
}