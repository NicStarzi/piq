package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;

public abstract class AbstractPLayoutOwner extends AbstractPComponent {
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			child.removeObs(childObs);
			needReLayout = true;
			checkForPreferredSizeChange();
		}
		public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			child.addObs(childObs);
			needReLayout = true;
			checkForPreferredSizeChange();
		}
		public void childLaidOut(PReadOnlyLayout layout, PComponent child, Object constraint) {
		}
		public void layoutInvalidated(PReadOnlyLayout layout) {
			needReLayout = true;
		}
	};
	protected final PComponentObs childObs = new PComponentObs() {
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
			checkForPreferredSizeChange();
		}
	};
	protected PLayout layout;
	
	/**
	 * Saves the given {@link PReadOnlyLayout} as the layout for this component.<br>
	 * If this component had a layout before it is cleared.<br>
	 * This method registers an observer at the given layout to react to
	 * changes with a re-render.<br>
	 * 
	 * @param layout
	 */
	protected void setLayout(PLayout layout) {
		if (this.layout != null) {
			this.layout.removeObs(layoutObs);
			this.layout.clearChildren();
		}
		this.layout = layout;
		if (this.layout != null) {
			this.layout.addObs(layoutObs);
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
	
}