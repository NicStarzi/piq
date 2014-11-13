package edu.udo.piq.basicdesign;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.tools.AbstractPDesignSheet;

public class BasicPDesignSheet extends AbstractPDesignSheet implements PDesignSheet {
	
	protected PDesign getDesignInternally(PComponent component) {
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
}