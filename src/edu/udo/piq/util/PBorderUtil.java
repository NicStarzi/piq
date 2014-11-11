package edu.udo.piq.util;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.components.PLineBorder;

public class PBorderUtil {
	
	public static PLineBorder addLineBorder(PComponent component) {
		PComponent parent = component.getParent();
		if (parent == null) {
			return new PLineBorder(component);
		}
		PLayout layout = parent.getLayout();
		Object constraint = layout.getChildConstraint(component);
		layout.removeChild(component);
		PLineBorder border = new PLineBorder(component);
		layout.addChild(border, constraint);
		return border;
	}
	
}