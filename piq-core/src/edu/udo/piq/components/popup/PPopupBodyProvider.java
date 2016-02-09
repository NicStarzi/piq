package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;
import edu.udo.piq.tools.AbstractPContainer;

public interface PPopupBodyProvider {
	
	public AbstractPContainer createBody(PComponent component);
	
}