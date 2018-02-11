package edu.udo.piq.components.containers;

import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.tools.AbstractPContainer;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PPanel extends AbstractPContainer<Object> {
	
	public static final Object STYLE_ID = PPanel.class;
	{
		setStyleID(STYLE_ID);
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner}
	 * public to give access to the user.<br>
	 */
	@Override
	public void setLayout(PLayout layout) {
		super.setLayout(layout);
	}
}