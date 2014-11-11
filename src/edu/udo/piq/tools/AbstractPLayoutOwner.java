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
			firePreferredSizeChangedEvent();
		}
		public void childAdded(PLayout layout, PComponent child, Object constraint) {
			child.addObs(childObs);
			needReLayout = true;
			firePreferredSizeChangedEvent();
		}
		public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
		}
		public void layoutInvalidated(PLayout layout) {
			needReLayout = true;
		}
	};
	protected final PComponentObs childObs = new PComponentObs() {
		public void wasRemoved(PComponent component) {
		}
		public void wasAdded(PComponent component) {
		}
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
			firePreferredSizeChangedEvent();
		}
	};
	protected PLayout layout;
	
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
	
	protected Collection<PComponent> getChildren() {
		return PCompUtil.getChildrenOf(this);
	}
	
	protected boolean hasChildren() {
		return getChildren().isEmpty();
	}
	
}