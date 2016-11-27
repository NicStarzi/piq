package edu.udo.piq;

public interface PStyleComponent {
	
	public static final PStyleComponent DEFAULT_COMPONENT_STYLE = new PStyleComponent() {};
	
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
	
	public default PStyleLayout getLayoutStyle(PComponent component, PReadOnlyLayout layout) {
		return PStyleLayout.DEFAULT_LAYOUT_STYLE;
	}
	
}