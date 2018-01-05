package edu.udo.piq.components;

import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PLabeledPicture extends AbstractPLayoutOwner {
	
	public PLabeledPicture() {
		this(null, null);
	}
	
	public PLabeledPicture(Object labelValue) {
		this(null, labelValue);
	}
	
	public PLabeledPicture(Object iconID, Object labelValue) {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PPicture(iconID), Constraint.FIRST);
		getLayoutInternal().addChild(new PLabel(labelValue), Constraint.SECOND);
	}
	
	public PLabeledPicture(PLabel label) {
		this(new PPicture(), label);
	}
	
	public PLabeledPicture(PPicture picture) {
		this(picture, new PLabel());
	}
	
	public PLabeledPicture(PPicture picture, PLabel label) {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(picture, Constraint.FIRST);
		getLayoutInternal().addChild(label, Constraint.SECOND);
	}
	
	protected void setLayout(PTupleLayout layout) {
		if (getLayout() != null) {
			getLayout().removeObs(layoutObs);
		}
		super.setLayout(layout);
		if (getLayout() != null) {
			getLayout().addObs(layoutObs);
		}
	}
	
	@Override
	protected PTupleLayout getLayoutInternal() {
		return (PTupleLayout) super.getLayout();
	}
	
	public PPicture getIcon() {
		return (PPicture) getLayoutInternal().getFirst();
	}
	
	public PLabel getLabel() {
		return (PLabel) getLayoutInternal().getSecond();
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}