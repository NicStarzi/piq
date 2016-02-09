package edu.udo.piq.components.popup;

import java.util.List;

import edu.udo.piq.PComponent;

public interface PPopupOptionsProvider {
	
	public List<PComponent> createOptions(PComponent component);
	
}