package edu.udo.piq.tools;

import java.util.Collection;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPLayoutOwner extends AbstractPComponent {
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		public void childRemoved(PLayout layout, PComponent child, Object constraint) {
			child.removeObs(childObs);
			needReLayout = true;
			checkForPreferredSizeChange();
		}
		public void childAdded(PLayout layout, PComponent child, Object constraint) {
			child.addObs(childObs);
			needReLayout = true;
			checkForPreferredSizeChange();
		}
		public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
		}
		public void layoutInvalidated(PLayout layout) {
			needReLayout = true;
		}
	};
	protected final PComponentObs childObs = new AbstractPComponentObs() {
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
			checkForPreferredSizeChange();
		}
	};
	protected PLayout layout;
	
	/**
	 * Saves the given {@link PLayout} as the layout for this component.<br>
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
	
	public PLayout getLayout() {
		return layout;
	}
	
	/**
	 * Returns a {@link Collection} containing all children of this 
	 * containers {@link PLayout} as returned by {@link PLayout#getChildren()}.<br>
	 * 
	 * @return the children of this container
	 */
	protected Collection<PComponent> getChildren() {
		return PCompUtil.getChildrenOf(this);
	}
	
	/**
	 * Returns true if the {@link PLayout} of this container has at least 
	 * one child.<br>
	 * 
	 * @return true if this container has any children
	 */
	protected boolean hasChildren() {
		return getChildren().isEmpty();
	}
	
}