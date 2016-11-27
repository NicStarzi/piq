package edu.udo.piq.style.standard;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PStyleBorder;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.PStyleLayout;
import edu.udo.piq.borders.PButtonBorder;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.tools.AbstractPStyleSheet;

public class StandardStyleSheet extends AbstractPStyleSheet {
	
	protected final Map<Object, PStyleComponent> styleMapComp = new HashMap<>();
	{
		styleMapComp.put(PButton.class, new StandardButtonStyle());
		styleMapComp.put(PLabel.class, new StandardLabelStyle());
	}
	protected final Map<Object, PStyleLayout> styleMapLayout = new HashMap<>();
	{
	}
	protected final Map<Object, PStyleBorder> styleMapBorder = new HashMap<>();
	{
		styleMapBorder.put(PButtonBorder.class, new StandardButtonBorderStyle());
	}
	
	@Override
	public PStyleComponent getStyleFor(PComponent component) {
		Object key = component.getStyleID();
		if (key == null) {
			return PStyleComponent.DEFAULT_COMPONENT_STYLE;
		}
		// Style might be null. Thats okay.
		return styleMapComp.get(key);
	}
	
	public PStyleLayout getStyleFor(PReadOnlyLayout layout) {
		Object key = layout.getStyleID();
		if (key == null) {
			return PStyleLayout.DEFAULT_LAYOUT_STYLE;
		}
		// Style might be null. Thats okay.
		return styleMapLayout.get(key);
	}
	
	public PStyleBorder getStyleFor(PBorder border) {
		Object key = border.getStyleID();
		if (key == null) {
			return PStyleBorder.DEFAULT_BORDER_STYLE;
		}
		// Style might be null. Thats okay.
		return styleMapBorder.get(key);
	}
	
	@Override
	protected void onComponentAdded(PComponent addedComponent) {
		addedComponent.setStyle(getStyleFor(addedComponent));
	}
}