package edu.udo.piq.tools;

import java.util.Collection;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;

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
	 * @param component		the component that will be added as a child
	 * @param constraint	the constraint used to add the child. 
	 * 						Valid constraints depend on the layout being used.
	 * @see #getLayout()
	 * @see PLayout#addChild(PComponent, Object)
	 */
	public void addChild(PComponent component, Object constraint) {
		getLayout().addChild(component, constraint);
	}
	
	public void removeChild(PComponent component) {
		getLayout().removeChild(component);
	}
	
	public void removeChild(Object constraint) {
		getLayout().removeChild(constraint);
	}
	
	public void clearChildren() {
		getLayout().clearChildren();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public Collection<PComponent> getChildren() {
		return super.getChildren();
	}
	
	public int getChildCount() {
		return getLayout().getChildCount();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner} 
	 * public to give access to the user.<br>
	 */
	public boolean hasChildren() {
		return super.hasChildren();
	}
	
}