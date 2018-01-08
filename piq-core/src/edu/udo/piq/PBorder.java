package edu.udo.piq;

import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.style.PStyleable;

public interface PBorder extends PStyleable<PStyleBorder> {
	
	public PInsets getDefaultInsets(PComponent component);
	
	public default PInsets getInsets(PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			return getDefaultInsets(component);
		} else {
			return style.getInsetsFor(this, component);
		}
	}
	
	public void defaultRender(PRenderer renderer, PComponent component);
	
	public default void render(PRenderer renderer, PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			defaultRender(renderer, component);
		} else {
			style.render(renderer, this, component);
		}
	}
	
	public boolean defaultFillsAllPixels(PComponent component);
	
	public default boolean fillsAllPixels(PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			return defaultFillsAllPixels(component);
		} else {
			return style.fillsAllPixels(this, component);
		}
	}
	
	public void addObs(PBorderObs obs);
	
	public void removeObs(PBorderObs obs);
	
}