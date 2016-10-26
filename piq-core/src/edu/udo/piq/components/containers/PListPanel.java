package edu.udo.piq.components.containers;

import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPContainer;

public class PListPanel extends AbstractPContainer<Integer> {
	
	public PListPanel() {
		super();
		setLayout(new PListLayout(this));
	}
	
	public PListPanel(ListAlignment alignment) {
		this(alignment, PListLayout.DEFAULT_INSETS, PListLayout.DEFAULT_GAP);
	}
	
	public PListPanel(PInsets insets) {
		this(PListLayout.DEFAULT_ALIGNMENT, insets, PListLayout.DEFAULT_GAP);
	}
	
	public PListPanel(int gap) {
		this(PListLayout.DEFAULT_ALIGNMENT, PListLayout.DEFAULT_INSETS, gap);
	}
	
	public PListPanel(ListAlignment alignment, PInsets insets, int gap) {
		this();
		getLayout().setAlignment(alignment);
		getLayout().setInsets(insets);
		getLayout().setGap(gap);
	}
	
	public PListLayout getLayout() {
		return (PListLayout) super.getLayout();
	}
	
	public int getGap() {
		return getLayout().getGap();
	}
	
	public ListAlignment getAlignment() {
		return getLayout().getAlignment();
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
	}
	
}