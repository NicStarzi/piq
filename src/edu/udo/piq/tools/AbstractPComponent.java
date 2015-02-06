package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
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
	 * Holds all {@link PComponentObs}ervers of this component.
	 */
	protected final List<PComponentObs> compObsList = new CopyOnWriteArrayList<>();
	/**
	 * Holds all {@link PFocusObs}ervers of this component.
	 */
	protected final List<PFocusObs> focusObsList = new CopyOnWriteArrayList<>();
	/**
	 * Holds all {@link PMouseObs PMouseObservers} of this component.
	 */
	protected final List<PMouseObs> mouseObsList = new CopyOnWriteArrayList<>();
	/**
	 * Holds all {@link PKeyboardObs PKeyboardObservers} of this component.
	 */
	protected final List<PKeyboardObs> keyboardObsList = new CopyOnWriteArrayList<>();
	/**
	 * Is registered at the {@link PRoot} of this component.<br>
	 * This observer is used to propagate focus events to {@link PFocusObs} 
	 * that are registered at this component.<br>
	 */
	protected final PFocusObs rootFocusObs = new PFocusObs() {
		public void focusLost(PComponent oldOwner) {
			if (oldOwner == AbstractPComponent.this) {
				fireFocusLostEvent();
			}
		}
		public void focusGained(PComponent oldOwner, PComponent newOwner) {
			if (newOwner == AbstractPComponent.this) {
				fireFocusGainedEvent(oldOwner);
			}
		}
	};
	protected final PComponentObs parentObs = new AbstractPComponentObs() {
		public void rootChanged(PComponent component, PRoot currentRoot) {
			setCachedRoot(currentRoot);
		}
	};
	/**
	 * Is registered at the layout of this components parent.<br>
	 * Notices when this component has been laid out to set the 
	 * flag needReLayout to true.
	 */
	protected final PLayoutObs parentLayoutObs = new AbstractPLayoutObs() {
		public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			if (child == AbstractPComponent.this) {
				needReLayout = true;
				fireReRenderEvent();
			}
		}
	};
	/**
	 * Is registered at the mouse of the current {@link PRoot} when available.<br>
	 * This observer is used to pass down events to {@link PMouseObs PMouseObservers} 
	 * that are registered at this {@link PComponent}.<br>
	 */
	protected final PMouseObs delegateMouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
			for (PMouseObs obs : mouseObsList) {
				obs.mouseMoved(mouse);
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			for (PMouseObs obs : mouseObsList) {
				obs.buttonTriggered(mouse, btn);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			for (PMouseObs obs : mouseObsList) {
				obs.buttonReleased(mouse, btn);
			}
		}
		public void buttonPressed(PMouse mouse, MouseButton btn) {
			for (PMouseObs obs : mouseObsList) {
				obs.buttonPressed(mouse, btn);
			}
		}
	};
	/**
	 * Is registered at the keyboard of the current {@link PRoot} when available.<br>
	 * This observer is used to pass down events to {@link PKeyboardObs PKeyboardObservers} 
	 * that are registered at this {@link PComponent}.<br>
	 */
	protected final PKeyboardObs delegateKeyObs = new PKeyboardObs() {
		public void keyPressed(PKeyboard keyboard, Key key) {
			for (PKeyboardObs obs : keyboardObsList) {
				obs.keyPressed(keyboard, key);
			}
		}
		public void keyTriggered(PKeyboard keyboard, Key key) {
			for (PKeyboardObs obs : keyboardObsList) {
				obs.keyTriggered(keyboard, key);
			}
		}
		public void keyReleased(PKeyboard keyboard, Key key) {
			for (PKeyboardObs obs : keyboardObsList) {
				obs.keyReleased(keyboard, key);
			}
		}
	};
	/**
	 * This field is true if the layout of this component needs to be laid out 
	 * with the next update cycle.<br>
	 * After the layout has been laid out this variable should be set to false.<br>
	 */
	protected boolean needReLayout = true;
	/**
	 * The current root of this components GUI.<br>
	 * This value is updated in the {@link #parentObs} of this component when a 
	 * change of the parents root is noticed.<br>
	 */
	private PRoot cachedRoot;
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
	 * Cached for removing observers
	 */
	private PKeyboard currentKeyboard;
	private PMouse currentMouse;
	
	/**
	 * Uses the utility method {@link PCompUtil#getRootOf(PComponent)} to 
	 * obtain the root for this component if it is not cached.<br>
	 */
	public PRoot getRoot() {
		if (cachedRoot != null) {
			return cachedRoot;
		}
		return PCompUtil.getRootOf(this);
	}
	
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException {
		if (parent != null && this.parent != null) {
			throw new IllegalStateException(this+".getParent() != null");
		} if (parent == null) {
			throw new NullPointerException("parent="+parent);
		} if (PCompUtil.isDescendant(this, parent)) {
			throw new IllegalArgumentException(this+" is descendant of "+parent);
		}
		PComponent oldParent = this.parent;
		if (oldParent != null) {
			oldParent.getLayout().removeObs(parentLayoutObs);
			oldParent.removeObs(parentObs);
		}
		this.parent = parent;
		if (this.parent != null) {
			this.parent.getLayout().addObs(parentLayoutObs);
			this.parent.addObs(parentObs);
		}
		if (oldParent == null && this.parent != null) {
			fireAddedEvent();
		} else if (oldParent != null) {
			fireRemovedEvent();
		}
		if (this.parent instanceof PRoot) {
			setCachedRoot((PRoot) this.parent);
		} else {
			setCachedRoot(parent.getRoot());
		}
	}
	
	private void setCachedRoot(PRoot root) {
		if (cachedRoot == root) {
			return;
		}
		if (cachedRoot != null) {
			cachedRoot.removeObs(rootFocusObs);
		}
		cachedRoot = root;
		if (cachedRoot != null) {
			cachedRoot.addObs(rootFocusObs);
		}
		fireRootChangedEvent();
		if (true) {
			if (currentKeyboard != null) {
				currentKeyboard.removeObs(delegateKeyObs);
			}
			currentKeyboard = cachedRoot == null ? null : cachedRoot.getKeyboard();
			if (currentKeyboard != null) {
				currentKeyboard.addObs(delegateKeyObs);
			}
		}
		if (true) {
			if (currentMouse != null) {
				currentMouse.removeObs(delegateMouseObs);
			}
			currentMouse = cachedRoot == null ? null : cachedRoot.getMouse();
			if (currentMouse != null) {
				currentMouse.addObs(delegateMouseObs);
			}
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
	 * If this component has a custom {@link PDesign} that design is returned.<br>
	 * If this component has a {@link PRoot} as returned by the {@link #getRoot()} 
	 * method then the design is retrieved from the {@link PDesignSheet} that 
	 * belongs to the root.<br>
	 * If this component has neither a custom design nor a root null is returned.<br>
	 */
	public PDesign getDesign() {
		if (customDesign != null) {
			return customDesign;
		}
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getDesignSheet().getDesignFor(this);
	}
	
	/**
	 * Returns null.<br>
	 * Subclasses should overwrite this method to return a {@link PLayout}.<br>
	 */
	public PLayout getLayout() {
		return null;
	}
	
	/**
	 * Does nothing.<br>
	 * This method should be overwritten by all subclasses of this class.<br>
	 */
	public void defaultRender(PRenderer renderer) {
	}
	
	/**
	 * The default implementation of this method always returns true.<br>
	 * All components that are partially translucent or have transparent 
	 * parts should override this method to return false.<br>
	 * 
	 * @return true
	 */
	public boolean isDefaultOpaque() {
		return true;
	}
	
	/**
	 * If this component has a layout the preferred size of the layout 
	 * is returned. Otherwise a size of (0, 0) is returned.<br>
	 * The returned size is immutable, it will not update to represent changes.<br>
	 */
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return PSize.NULL_SIZE;
	}
	
	/**
	 * Always returns null by default.<br>
	 */
	public PDnDSupport getDragAndDropSupport() {
		return null;
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
	}
	
	/**
	 * The default implementation always returns false. Components that make use of 
	 * {@link PKeyboard} input should overwrite this method to return true.<br>
	 * 
	 * @return always false
	 */
	public boolean isFocusable() {
		return false;
	}
	
	public void addObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		compObsList.add(obs);
	}
	
	public void removeObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		compObsList.remove(obs);
	}
	
	public void addObs(PFocusObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		focusObsList.add(obs);
	}
	
	public void removeObs(PFocusObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		focusObsList.remove(obs);
	}
	
	public void addObs(PMouseObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		mouseObsList.add(obs);
	}
	
	public void removeObs(PMouseObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		mouseObsList.remove(obs);
	}
	
	public void addObs(PKeyboardObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		keyboardObsList.add(obs);
	}
	
	public void removeObs(PKeyboardObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		keyboardObsList.remove(obs);
	}
	
	protected void fireRootChangedEvent() {
		for (PComponentObs obs : compObsList) {
			obs.rootChanged(this, cachedRoot);
		}
	}
	
	protected void fireAddedEvent() {
		for (PComponentObs obs : compObsList) {
			obs.wasAdded(this);
		}
	}
	
	protected void fireRemovedEvent() {
		for (PComponentObs obs : compObsList) {
			obs.wasRemoved(this);
		}
	}
	
	protected void firePreferredSizeChangedEvent() {
		for (PComponentObs obs : compObsList) {
			obs.preferredSizeChanged(this);
		}
	}
	
	protected void fireReRenderEvent() {
		PRoot root = getRoot();
		if (root != null) {
			root.reRender(this);
		}
	}
	
	protected void fireFocusGainedEvent(PComponent oldFocusOwner) {
		for (PFocusObs obs : focusObsList) {
			obs.focusGained(oldFocusOwner, this);
		}
	}
	
	protected void fireFocusLostEvent() {
		for (PFocusObs obs : focusObsList) {
			obs.focusLost(this);
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