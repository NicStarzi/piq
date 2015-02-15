package edu.udo.piq.components;

import edu.udo.piq.PComponent;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PTuple extends AbstractPLayoutOwner {
	
	public PTuple(PComponent first, PComponent second) {
		super();
		setLayout(new PTupleLayout(this));
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
		return getLayout().getAt(Constraint.FIRST);
	}
	
	public void setSecondComponent(PComponent component) {
		if (component == null) {
			getLayout().removeChild(Constraint.SECOND);
		} else {
			getLayout().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayout().getAt(Constraint.SECOND);
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}