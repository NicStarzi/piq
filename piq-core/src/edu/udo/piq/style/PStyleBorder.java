package edu.udo.piq.style;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;

public interface PStyleBorder {
	
	public static final PStyleBorder DEFAULT_BORDER_STYLE = new PStyleBorder() {
		@Override
		public void addObs(PStyleObs obs) {}
		@Override
		public void removeObs(PStyleObs obs) {}
	};
	public static final PStyleBorder STYLE_EMPTY_BORDER = new PStyleBorder() {
		@Override
		public PInsets getInsetsFor(PBorder border,
				PComponent component) { return PInsets.ZERO_INSETS; }
		@Override
		public void render(PRenderer renderer,
				PBorder border, PComponent component) {}
		@Override
		public boolean fillsAllPixels(PBorder border,
				PComponent component) { return false; }
		@Override
		public void addObs(PStyleObs obs) {}
		@Override
		public void removeObs(PStyleObs obs) {}
	};
	
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
	
	public void addObs(PStyleObs obs);
	
	public void removeObs(PStyleObs obs);
	
}