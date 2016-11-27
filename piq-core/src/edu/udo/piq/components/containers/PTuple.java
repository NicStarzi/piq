package edu.udo.piq.components.containers;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.layouts.PTupleLayout.Distribution;
import edu.udo.piq.layouts.PTupleLayout.Orientation;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;

public class PTuple extends AbstractPLayoutOwner {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(2);
	
	public PTuple(Orientation orientation) {
		this();
		setOrientation(orientation);
	}
	
	public PTuple(Orientation orientation, Distribution distribution) {
		this(orientation);
		setDistribution(distribution);
	}
	
	public PTuple(Orientation orientation, PComponent first, PComponent second) {
		this(orientation);
		setOrientation(orientation);
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PTuple(Orientation orientation, Distribution distribution,
			PComponent first, PComponent second)
	{
		this(orientation, distribution);
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PTuple(PComponent first, PComponent second) {
		this();
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayout().setInsets(DEFAULT_INSETS);
	}
	
	@Override
	public void setBorder(PBorder border) {
		super.setBorder(border);
	}
	
	@Override
	public PTupleLayout getLayout() {
		return (PTupleLayout) layout;
	}
	
	public void setOrientation(Orientation orientation) {
		getLayout().setOrientation(orientation);
	}
	
	public Orientation getOrientation() {
		return getLayout().getOrientation();
	}
	
	public void setDistribution(Distribution distribution) {
		getLayout().setDistribution(distribution);
	}
	
	public Distribution getDistribution() {
		return getLayout().getDistribution();
	}
	
	public void setSecondaryDistribution(Distribution distribution) {
		getLayout().setSecondaryDistribution(distribution);
	}
	
	public Distribution getSecondaryDistribution() {
		return getLayout().getSecondaryDistribution();
	}
	
	public void setGap(int value) {
		getLayout().setGap(value);
	}
	
	public int getGap() {
		return getLayout().getGap();
	}
	
	public void setInsets(PInsets insets) {
		getLayout().setInsets(insets);
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
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
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}