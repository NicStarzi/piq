package edu.udo.piq.tools;

import java.util.Collection;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PReadOnlyLayout;

public abstract class AbstractPContainer extends AbstractPLayoutOwner {
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public void setLayout(PLayout layout) {
		super.setLayout(layout);
	}
	
	public PLayout getLayout() {
		return (PLayout) super.getLayout();
	}
	
	/**
	 * Delegates to the layout of this container.<br> 
	 * This method exists solely for convenience.<br>
	 * @see #getLayout()
	 * @see PReadOnlyLayout#addChild(PComponent, Object)
	 */
	public void addChild(PComponent component, Object constraint) {
		getLayout().addChild(component, constraint);
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public Collection<PComponent> getChildren() {
		return super.getChildren();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public boolean hasChildren() {
		return super.hasChildren();
	}
	
}