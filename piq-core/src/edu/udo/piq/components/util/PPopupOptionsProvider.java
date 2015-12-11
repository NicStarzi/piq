package edu.udo.piq.components.util;

import java.util.List;

import edu.udo.piq.PComponent;

public interface PPopupOptionsProvider {
	
	public List<PComponent> createOptions(PComponent component);
	
}