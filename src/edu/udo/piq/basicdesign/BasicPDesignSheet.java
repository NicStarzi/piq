package edu.udo.piq.basicdesign;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.tools.AbstractPDesignSheet;

public class BasicPDesignSheet extends AbstractPDesignSheet implements PDesignSheet {
	
	protected static final Map<Class<?>, PDesign> designMap = new HashMap<>();
	static {
		designMap.put(PPanel.class, new BasicPPanelDesign());
		designMap.put(PLabel.class, new BasicPLabelDesign());
		designMap.put(PButton.class, new BasicPButtonDesign());
		designMap.put(PCheckBox.class, new BasicPCheckBoxDesign());
	}
	
	protected PDesign getDesignInternally(PComponent component) {
		PDesign design = designMap.get(component.getClass());
		if (design != null) {
			return design;
		}
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
}