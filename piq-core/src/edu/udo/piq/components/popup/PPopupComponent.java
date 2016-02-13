package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;

public interface PPopupComponent extends PComponent {
	
	public void setHighlighted(boolean value);
	
	public boolean isHighlighted();
	
	public void setEnabled(boolean value);
	
	public boolean isEnabled();
	
	public void addObs(PPopupComponentObs obs);
	
	public void removeObs(PPopupComponentObs obs);
	
}