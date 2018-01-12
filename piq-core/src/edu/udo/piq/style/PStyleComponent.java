package edu.udo.piq.style;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;

public interface PStyleComponent extends PStyle {
	
	public static final PStyleComponent DEFAULT_COMPONENT_STYLE = new PStyleComponent() {
		@Override
		public void addObs(PStyleObs obs) {}
		@Override
		public void removeObs(PStyleObs obs) {}
	};
	
	public default boolean fillsAllPixels(PComponent component) {
		return component.defaultFillsAllPixels();
	}
	
	public default void render(PRenderer renderer, PComponent component) {
		component.defaultRender(renderer);
	}
	
	public default PSize getPreferredSize(PComponent component) {
		return component.getDefaultPreferredSize();
	}
	
	public default PStyleBorder getBorderStyle(PComponent component, PBorder border) {
		return PStyleBorder.DEFAULT_BORDER_STYLE;
	}
	
//	public default PStyleLayout getLayoutStyle(PComponent component, PReadOnlyLayout layout) {
//		return PStyleLayout.DEFAULT_LAYOUT_STYLE;
//	}
	
	@TemplateMethod
	public default void addStyledComponent(PComponent component) {}
	
	@TemplateMethod
	public default void removeStyledComponent(PComponent component) {}
	
}