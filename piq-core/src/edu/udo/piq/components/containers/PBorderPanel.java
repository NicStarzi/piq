package edu.udo.piq.components.containers;

import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.BorderLayoutConstraint;
import edu.udo.piq.tools.AbstractPContainer;

public class PBorderPanel extends AbstractPContainer<BorderLayoutConstraint> {
	
	public static final Object STYLE_ID = PPanel.STYLE_ID;
	{
		setStyleID(STYLE_ID);
	}
	
	public PBorderPanel() {
		super();
		setLayout(new PBorderLayout(this));
	}
	
	public PBorderPanel(int gap) {
		this(PBorderLayout.DEFAULT_INSETS, gap);
	}
	
	public PBorderPanel(PInsets insets) {
		this(insets, PBorderLayout.DEFAULT_GAP);
	}
	
	public PBorderPanel(PInsets insets, int gap) {
		this();
		getLayout().setInsets(insets);
		getLayout().setGap(gap);
	}
	
	@Override
	public PBorderLayout getLayout() {
		return (PBorderLayout) super.getLayout();
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
	}
	
	public int getGap() {
		return getLayout().getGap();
	}
	
}