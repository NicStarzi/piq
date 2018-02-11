package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.tools.AbstractPContainer;

public class PAnchorPanel extends AbstractPContainer<Integer> {
	
	public static final Object STYLE_ID = PPanel.STYLE_ID;
	{
		setStyleID(STYLE_ID);
	}
	
	public PAnchorPanel() {
		super();
		setLayout(new PAnchorLayout(this));
	}
	
	public PAnchorPanel(PInsets insets) {
		this(PAnchorLayout.DEFAULT_ALIGN_X, PAnchorLayout.DEFAULT_ALIGN_Y, insets);
	}
	
	public PAnchorPanel(AlignmentX alignX, AlignmentY alignY) {
		this(alignX, alignY, PListLayout.DEFAULT_INSETS);
	}
	
	public PAnchorPanel(AlignmentX alignX, AlignmentY alignY, PInsets insets) {
		this();
		getLayout().setAlignmentX(alignX);
		getLayout().setAlignmentY(alignY);
		getLayout().setInsets(insets);
	}
	
	public void setContent(PComponent component) {
		getLayout().setContent(component);
	}
	
	@Override
	public PAnchorLayout getLayout() {
		return (PAnchorLayout) super.getLayout();
	}
	
	public AlignmentX getAlignmentX() {
		return getLayout().getAlignmentX();
	}
	
	public AlignmentY getAlignmentY() {
		return getLayout().getAlignmentY();
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
	}
	
}