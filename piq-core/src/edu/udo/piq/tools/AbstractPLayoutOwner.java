package edu.udo.piq.tools;

import edu.udo.piq.CallSuper;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.layouts.PLayoutObs;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPLayoutOwner extends AbstractPComponent {
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		@Override
		public void onChildRemoved(PReadOnlyLayout layout, PComponentLayoutData data) {
			AbstractPLayoutOwner.this.onChildRemoved(data);
		}
		@Override
		public void onChildAdded(PReadOnlyLayout layout, PComponentLayoutData data) {
			AbstractPLayoutOwner.this.onChildAdded(data);
		}
		@Override
		public void onChildLaidOut(PReadOnlyLayout layout, PComponentLayoutData data) {
			AbstractPLayoutOwner.this.onChildLaidOut(data);
		}
		@Override
		public void onLayoutInvalidated(PReadOnlyLayout layout) {
			AbstractPLayoutOwner.this.onLayoutInvalidated();
		}
	};
	protected PLayout layout;
	
	/**
	 * Saves the given {@link PLayout} as the layout for this component.<br>
	 * If this component had a layout before it is cleared and disposed.<br>
	 * This method registers an observer at the given layout to react to
	 * changes with a re-render.<br>
	 * 
	 * @param layout
	 */
	protected <LAYOUT_T extends PLayout> LAYOUT_T setLayout(LAYOUT_T layout) {
		PLayout oldLayout = (PLayout) getLayout();
		if (oldLayout != null) {
			oldLayout.removeObs(layoutObs);
//			oldLayout.setInheritedStyle(null);
			oldLayout.clearChildren();
			oldLayout.dispose();
		}
		this.layout = layout;
		if (getLayout() != null) {
			ThrowException.ifNotEqual(this, getLayout().getOwner(),
					"getLayout().getOwner() != this");
//			PStyleComponent style = getStyle();
//			if (style != null) {
//				getLayout().setInheritedStyle(style.getLayoutStyle(this, getLayout()));
//			}
			getLayout().addObs(layoutObs);
			fireReLayOutEvent();
		}
		fireLayoutChangedEvent(oldLayout);
		return layout;
	}
	
	@Override
	public PReadOnlyLayout getLayout() {
		return layout;
	}
	
	protected PLayout getLayoutInternal() {
		return layout;
	}
	
	/**
	 * Returns true if the {@link PReadOnlyLayout} of this container has at least
	 * one child.<br>
	 * 
	 * @return true if this container has any children
	 */
	protected boolean hasChildren() {
		return getLayout().isEmpty();
	}
	
	@TemplateMethod
	@CallSuper
	protected void onChildRemoved(PComponentLayoutData data) {
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	@CallSuper
	protected void onChildAdded(PComponentLayoutData data) {
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	@CallSuper
	protected void onChildLaidOut(PComponentLayoutData data) {
		fireReRenderEvent();
	}
	
	@TemplateMethod
	@CallSuper
	protected void onLayoutInvalidated() {
		fireReLayOutEvent();
	}
	
}