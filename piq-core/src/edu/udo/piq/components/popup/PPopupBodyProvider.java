package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.containers.PListPanel;

public interface PPopupBodyProvider {
	
	public PListPanel createBody(PComponent component);
	
}