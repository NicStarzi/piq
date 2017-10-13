package edu.udo.piq.components.popup;

import java.util.List;
import java.util.function.Function;

import edu.udo.piq.PComponent;

public interface PPopupOptionsProvider extends Function<PComponent, List<PComponent>> {
	
	@Override
	public List<PComponent> apply(PComponent component);
	
}