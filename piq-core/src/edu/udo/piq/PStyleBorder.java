package edu.udo.piq;

public interface PStyleBorder {
	
	public static final PStyleBorder DEFAULT_BORDER_STYLE = new PStyleBorder() {};
	
	public default PInsets getInsetsFor(PBorder border,
			PComponent component)
	{
		return border.getDefaultInsets(component);
	}
	
	public default void render(PRenderer renderer,
			PBorder border, PComponent component)
	{
		border.defaultRender(renderer, component);
	}
	
	public default boolean fillsAllPixels(PBorder border,
			PComponent component)
	{
		return border.defaultFillsAllPixels(component);
	}
	
}