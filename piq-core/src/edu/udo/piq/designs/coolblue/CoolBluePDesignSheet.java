package edu.udo.piq.designs.coolblue;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.components.PButton;
import edu.udo.piq.tools.AbstractPDesignSheet;
import edu.udo.piq.tools.ImmutablePColor;

public class CoolBluePDesignSheet extends AbstractPDesignSheet implements PDesignSheet {
	
	protected static final PColor LIGHT_BLUE = new ImmutablePColor(192, 192, 255);
	protected static final PColor MEDIUM_BLUE = new ImmutablePColor(128, 128, 192);
	protected static final PColor DARK_BLUE = new ImmutablePColor(0, 0, 160);
	private final CoolBluePButtonDesign btnDesign = new CoolBluePButtonDesign();
	
	protected PDesign getDesignInternally(PComponent component) {
		Class<?> compClass = component.getClass();
		if (compClass == PButton.class) {
			return btnDesign;
		}
		return super.getDesignInternally(component);
	}
	
}