package edu.udo.piq.components.containers;

import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PMenuBar extends AbstractPLayoutOwner {
	
	public PMenuBar() {
		super();
		setLayout(new PWrapLayout(this, ListAlignment.LEFT_TO_RIGHT));
	}
	
}