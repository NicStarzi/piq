package edu.udo.piq.tools;

import edu.udo.piq.PBorder;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PLayout;

public abstract class AbstractPContainer<CONSTRAINT_CLASS> extends AbstractPLayoutOwner {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(20, 20);
	
	@Override
	public PLayout getLayout() {
		return (PLayout) super.getLayout();
	}
	
	@Override
	public void setBorder(PBorder border) {
		super.setBorder(border);
	}
	
	@Override
	public void setFocusTraversal(PFocusTraversal focusTraversal) {
		super.setFocusTraversal(focusTraversal);
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
	public void addChild(PComponent component, CONSTRAINT_CLASS constraint) {
		getLayout().addChild(component, constraint);
	}
	
	public void removeChild(PComponent component) {
		getLayout().removeChild(component);
	}
	
	public void removeChild(CONSTRAINT_CLASS constraint) {
		getLayout().removeChild(constraint);
	}
	
	public void clearChildren() {
		getLayout().clearChildren();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner}
	 * public to give access to the user.<br>
	 */
	@Override
	public Iterable<PComponent> getChildren() {
		return super.getChildren();
	}
	
	public PComponent getChildForConstraint(CONSTRAINT_CLASS constraint) {
		return getLayout().getChildForConstraint(constraint);
	}
	
	@Override
	public int getChildCount() {
		return getLayout().getChildCount();
	}
	
	/**
	 * Makes the protected method from {@link AbstractPLayoutOwner}
	 * public to give access to the user.<br>
	 */
	@Override
	public boolean hasChildren() {
		return super.hasChildren();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBoundsWithoutBorder());
	}
	
	@Override
	protected PSize getConstantDefaultPreferredSize() {
		return AbstractPContainer.DEFAULT_PREFERRED_SIZE;
	}
	
}