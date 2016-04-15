package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPLayoutOwner extends AbstractPComponent {
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		public void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			AbstractPLayoutOwner.this.onChildRemoved(child, constraint);
		}
		public void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			AbstractPLayoutOwner.this.onChildAdded(child, constraint);
		}
		public void onChildLaidOut(PReadOnlyLayout layout, PComponent child, Object constraint) {
			AbstractPLayoutOwner.this.onChildLaidOut(child, constraint);
		}
		public void onLayoutInvalidated(PReadOnlyLayout layout) {
			AbstractPLayoutOwner.this.onLayoutInvalidated();
		}
	};
	protected final PComponentObs childObs = new PComponentObs() {
		public void onPreferredSizeChanged(PComponent component) {
			AbstractPLayoutOwner.this.onChildPreferredSizeChanged(component);
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
			oldLayout.clearChildren();
			oldLayout.dispose();
		}
		this.layout = layout;
		if (getLayout() != null) {
			ThrowException.ifNotEqual(this, getLayout().getOwner(), 
					"getLayout().getOwner() != this");
			getLayout().addObs(layoutObs);
		}
	}
	
	public PReadOnlyLayout getLayout() {
		return layout;
	}
	
	/**
	 * Returns true if the {@link PReadOnlyLayout} of this container has at least 
	 * one child.<br>
	 * 
	 * @return true if this container has any children
	 */
	protected boolean hasChildren() {
		return getChildren().isEmpty();
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		child.removeObs(childObs);
		fireReLayOutEvent();
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		child.addObs(childObs);
		fireReLayOutEvent();
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	protected void onChildLaidOut(PComponent child, Object constraint) {
		fireReRenderEvent();
	}
	
	protected void onLayoutInvalidated() {
		fireReLayOutEvent();
	}
	
	protected void onChildPreferredSizeChanged(PComponent child) {
		fireReLayOutEvent();
		checkForPreferredSizeChange();
	}
	
}