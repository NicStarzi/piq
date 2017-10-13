package edu.udo.piq.components.popup;

import java.util.function.Function;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;

public interface PPopupBorderProvider extends Function<PComponent, PBorder> {
	
	@Override
	public PBorder apply(PComponent component);
	
}