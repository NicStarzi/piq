package edu.udo.piq.components.containers;

import edu.udo.piq.PLayout;
import edu.udo.piq.tools.AbstractPContainer;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PPanel extends AbstractPContainer<Object> {
	
	public PPanel() {
		super();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public void setLayout(PLayout layout) {
		super.setLayout(layout);
	}
}