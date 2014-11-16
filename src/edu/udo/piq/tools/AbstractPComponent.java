package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.util.PCompUtil;

public class AbstractPComponent implements PComponent {
	
	/**
	 * Holds the parent of this component in the GUI tree.<br>
	 * This field is null if this component has no parent.<br>
	 */
	private PComponent parent;
	/**
	 * Custom design used by this component.
	 */
	private PDesign customDesign;
	/**
	 * Holds all observers of this component.
	 */
	protected final List<PComponentObs> obsList = new CopyOnWriteArrayList<>();
	/**
	 * Is registered at the layout of this components parent.<br>
	 * Notices when this component has been laid out to set the 
	 * flag needReLayout to true.
	 */
	protected final PLayoutObs parentLayoutObs = new PLayoutObs() {
		public void childRemoved(PLayout layout, PComponent child, Object constraint) {
		}
		public void childAdded(PLayout layout, PComponent child, Object constraint) {
		}
		public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			if (child == AbstractPComponent.this) {
				needReLayout = true;
				fireReRenderEvent();
			}
		}
		public void layoutInvalidated(PLayout layout) {
		}
	};
	/**
	 * This field is true if the layout of this component needs to be laid out 
	 * with the next update cycle.<br>
	 * After the layout has been laid out this variable should be set to false.<br>
	 */
	protected boolean needReLayout = true;
	/**
	 * These fields are used to store the previous preferred size of this component.<br>
	 * After the layout has been laid out these values are checked against the 
	 * new preferred size of this component. If the size has changed the 
	 * preferredSizeChanged event is fired and these values are updated.<br>
	 */
	private int lastPrefW = -1;
	private int lastPrefH = -1;
	/**
	 * The components id will be displayed by the toString() method unless the id 
	 * is null.<br> If the id is null the toString() method will show the components 
	 * classes simple name.
	 */
	private String id = null;
	
	/**
	 * Uses the utility method {@link PCompUtil#getRootOf(PComponent)} to 
	 * obtain the root for this component.<br>
	 */
	public PRoot getRoot() {
		return PCompUtil.getRootOf(this);
	}
	
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException {
		if (parent != null && this.parent != null) {
			throw new IllegalStateException();
		}
		if (PCompUtil.isDescendant(this, parent)) {
			throw new IllegalArgumentException();
		}
		PComponent oldParent = this.parent;
		if (oldParent != null) {
			oldParent.getLayout().removeObs(parentLayoutObs);
		}
		this.parent = parent;
		if (this.parent != null) {
			this.parent.getLayout().addObs(parentLayoutObs);
		}
		if (oldParent == null && this.parent != null) {
			fireAddedEvent();
		} else if (oldParent != null) {
			fireRemovedEvent();
		}
	}
	
	public PComponent getParent() {
		return parent;
	}
	
	/**
	 * Uses the utility method {@link PCompUtil#getBoundsOf(PComponent)} to 
	 * obtain the bounds for this component.<br>
	 */
	public PBounds getBounds() {
		return PCompUtil.getBoundsOf(this);
	}
	
	public void setDesign(PDesign design) {
		customDesign = design;
		fireReRenderEvent();
	}
	
	/**
	 * Uses the utility method {@link PCompUtil#getDesignOf(PComponent)} to 
	 * obtain the design for this component.<br>
	 */
	public PDesign getDesign() {
		if (customDesign != null) {
			return customDesign;
		}
		return getRoot().getDesignSheet().getDesignFor(this);
	}
	
	/**
	 * Returns null.
	 */
	public PLayout getLayout() {
		return null;
	}
	
	/**
	 * Does nothing.<br>
	 * This method should be overwritten by all subclasses of this class.
	 */
	public void defaultRender(PRenderer renderer) {
	}
	
	/**
	 * If this component has a layout the preferred size of the layout 
	 * is returned. Otherwise a size of (1, 1) is returned.<br>
	 * The returned size is immutable and not synchronized with this 
	 * component.<br>
	 */
	public PSize getDefaultPreferredSize() {
		int w = 1;
		int h = 1;
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return new ImmutablePSize(w, h);
	}
	
	/**
	 * Refreshes the layout as needed.<br>
	 * This method calls the onUpdate() method to delegate updates to subclasses.<br>
	 */
	public final void update() {
		if (needReLayout && getLayout() != null) {
			getLayout().layOut();
			needReLayout = false;
			
			checkForPreferredSizeChange();
		}
		onUpdate();
	}
	
	/**
	 * Does nothing.<br>
	 * This method should be overwritten by all subclasses of this class.<br>
	 * This method is called by the update() method to delegate subclass code.<br>
	 */
	protected void onUpdate() {
	}
	
	public void addObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.add(obs);
	}
	
	public void removeObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.remove(obs);
	}
	
	protected void fireAddedEvent() {
		for (PComponentObs obs : obsList) {
			obs.wasAdded(this);
		}
	}
	
	protected void fireRemovedEvent() {
		for (PComponentObs obs : obsList) {
			obs.wasRemoved(this);
		}
	}
	
	protected void firePreferredSizeChangedEvent() {
		for (PComponentObs obs : obsList) {
			obs.preferredSizeChanged(this);
		}
	}
	
	protected void fireReRenderEvent() {
		PRoot root = getRoot();
		if (root != null) {
			root.reRender(this);
		}
	}
	
	protected void checkForPreferredSizeChange() {
		PSize currentPrefSize = PCompUtil.getPreferredSizeOf(this);
		
		if (lastPrefW != currentPrefSize.getWidth() 
				|| lastPrefH != currentPrefSize.getHeight()) {
			
			lastPrefW = currentPrefSize.getWidth();
			lastPrefH = currentPrefSize.getHeight();
			firePreferredSizeChangedEvent();
		}
	}
	
	public void setID(String value) {
		id = value;
	}
	
	public String getID() {
		return id;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (id == null) {
			builder.append(getClass().getSimpleName());
		} else {
			builder.append(getID());
		}
		builder.append(" [bounds=");
		builder.append(PCompUtil.getBoundsOf(this));
		builder.append("]");
		return builder.toString();
	}
}