package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.layouts.PComponentLayoutData;
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
	protected final PComponentObs childObs = new PComponentObs() {
		@Override
		public void onScrollRequest(PComponent component, int offsetX, int offsetY) {
			onChildRequestedScroll(component, offsetX, offsetY);
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
	protected void setLayout(PLayout layout) {
		PLayout oldLayout = (PLayout) getLayout();
		if (oldLayout != null) {
			oldLayout.removeObs(layoutObs);
			oldLayout.setStyle(null);
			oldLayout.clearChildren();
			oldLayout.dispose();
		}
		this.layout = layout;
		if (getLayout() != null) {
			ThrowException.ifNotEqual(this, getLayout().getOwner(),
					"getLayout().getOwner() != this");
			PStyleComponent style = getStyle();
			if (style != null) {
				getLayout().setStyle(style.getLayoutStyle(this, getLayout()));
			}
			getLayout().addObs(layoutObs);
			fireReLayOutEvent();
		}
		fireLayoutChangedEvent(oldLayout);
	}
	
	@Override
	public PReadOnlyLayout getLayout() {
		return layout;
	}
	
	protected PLayout getLayoutInternal() {
		return layout;
	}
	
	@Override
	public void setStyle(PStyleComponent style) {
		super.setStyle(style);
		PReadOnlyLayout layout = getLayout();
		if (layout == null) {
			return;
		}
		PStyleComponent newStyle = getStyle();
		if (newStyle == null) {
			getLayout().setStyle(null);
		} else {
			getLayout().setStyle(newStyle.getLayoutStyle(this, getLayout()));
		}
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
	
	protected void onChildRemoved(PComponentLayoutData data) {
		data.getComponent().removeObs(childObs);
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	protected void onChildAdded(PComponentLayoutData data) {
		data.getComponent().addObs(childObs);
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	protected void onChildLaidOut(PComponentLayoutData data) {
		fireReRenderEvent();
	}
	
	protected void onLayoutInvalidated() {
		fireReLayOutEvent();
	}
	
	protected void onChildRequestedScroll(PComponent child, int offsetX, int offsetY) {
		fireScrollRequestEvent(child, offsetX, offsetY);
	}
	
}