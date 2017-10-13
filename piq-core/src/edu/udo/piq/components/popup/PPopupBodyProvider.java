package edu.udo.piq.components.popup;

import java.util.function.Function;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.containers.PListPanel;

public interface PPopupBodyProvider extends Function<PComponent, PListPanel> {
	
	@Override
	public PListPanel apply(PComponent t);
	
}