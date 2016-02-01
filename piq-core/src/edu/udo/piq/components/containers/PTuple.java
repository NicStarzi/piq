package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;

public class PTuple extends AbstractPLayoutOwner {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(2);
	
	public PTuple(PComponent first, PComponent second) {
		super();
		setLayout(new PTupleLayout(this));
		getLayout().setInsets(DEFAULT_INSETS);
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PTupleLayout getLayout() {
		return (PTupleLayout) layout;
	}
	
	public void setFirstComponent(PComponent component) {
		if (component == null) {
			getLayout().removeChild(Constraint.FIRST);
		} else {
			getLayout().addChild(component, Constraint.FIRST);
		}
	}
	
	public PComponent getFirstComponent() {
		return getLayout().getFirst();
	}
	
	public void setSecondComponent(PComponent component) {
		if (component == null) {
			getLayout().removeChild(Constraint.SECOND);
		} else {
			getLayout().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayout().getSecond();
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}