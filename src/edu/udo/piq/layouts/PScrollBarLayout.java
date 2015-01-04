package edu.udo.piq.layouts;

import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PScrollBarHorizontal;
import edu.udo.piq.tools.AbstractPLayout;

public class PScrollBarLayout extends AbstractPLayout {
	
	public PScrollBarLayout(PScrollBarHorizontal component) {
		super(component);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return true;
	}
	
	public void layOut() {
	}
	
	public PSize getPreferredSize() {
		return null;
	}
	
	public static enum Constraint {
		BACKGROUND, 
		BTN_DECREMENT, 
		BTN_INCREMENT, 
		SLIDER, 
		;
	}
	
}