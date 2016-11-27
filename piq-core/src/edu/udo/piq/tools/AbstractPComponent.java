package edu.udo.piq.tools;

import java.util.Objects;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBorderObs;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PStyleBorder;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class AbstractPComponent implements PComponent {
	
	/**
	 * The value returned by {@link #isElusive()} if the user does not
	 * call the {@link #setElusive(boolean)} method is false.<br>
	 */
	public static final boolean DEFAULT_IS_ELUSIVE = false;
	
	/**
	 * Holds the parent of this component in the GUI tree.<br>
	 * This field is null if this component has no parent.<br>
	 */
	private PComponent parent;
	private PBorder border;
	private PStyleComponent style;
	/**
	 * Holds all {@link PComponentObs PComponentObservers} of this component.
	 */
	protected final ObserverList<PComponentObs> compObsList
		= PCompUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PFocusObs PFocusObservers} of this component.
	 */
	protected final ObserverList<PFocusObs> focusObsList
		= PCompUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PMouseObs PMouseObservers} of this component.
	 */
	protected final ObserverList<PMouseObs> mouseObsList
		= PCompUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PKeyboardObs PKeyboardObservers} of this component.
	 */
	protected final ObserverList<PKeyboardObs> keyboardObsList
		= PCompUtil.createDefaultObserverList();
	/**
	 * Is registered at the {@link PRoot} of this component.<br>
	 * This observer is used to propagate focus events to {@link PFocusObs}
	 * that are registered at this component.<br>
	 */
	protected final PFocusObs rootFocusObs = new PFocusObs() {
		@Override
		public void onFocusLost(PComponent oldOwner) {
			if (oldOwner == AbstractPComponent.this) {
				fireFocusLostEvent();
				fireReRenderEvent();
			}
		}
		@Override
		public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
			if (newOwner == AbstractPComponent.this) {
				fireFocusGainedEvent(oldOwner);
				fireReRenderEvent();
			}
		}
	};
	protected final PComponentObs parentObs = new PComponentObs() {
		@Override
		public void onRootChanged(PComponent component, PRoot currentRoot) {
			PRoot oldRoot = cachedRoot;
			setCachedRoot(getParent(), currentRoot);
			AbstractPComponent.this.onRootChanged(oldRoot);
		}
	};
	/**
	 * Is registered at the layout of this components parent.<br>
	 * Notices when this component has been laid out to set the
	 * flag needReLayout to true.
	 */
	protected final PLayoutObs parentLayoutObs = new PLayoutObs() {
		@Override
		public void onChildLaidOut(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (child == AbstractPComponent.this) {
				cachedBoundsInvalid = true;
				checkForBoundsChange();
				AbstractPComponent.this.onThisLaidOut(constraint);
			}
		}
	};
	protected final PBorderObs borderObs = new PBorderObs() {
		@Override
		public void onReRender(PBorder border) {
			fireReRenderEvent();
		}
		@Override
		public void onInsetsChanged(PBorder border) {
			firePreferredSizeChangedEvent();
		}
	};
	/**
	 * Is registered at the mouse of the current {@link PRoot} when available.<br>
	 * This observer is used to pass down events to {@link PMouseObs PMouseObservers}
	 * that are registered at this {@link PComponent}.<br>
	 */
	protected final PMouseObs delegateMouseObs = new PMouseObs() {
		@Override
		public void onMouseMoved(PMouse mouse) {
			mouseObsList.fireEvent(obs -> obs.onMouseMoved(mouse));
		}
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			mouseObsList.fireEvent(obs -> obs.onButtonTriggered(mouse, btn));
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			mouseObsList.fireEvent(obs -> obs.onButtonReleased(mouse, btn));
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn) {
			mouseObsList.fireEvent(obs -> obs.onButtonPressed(mouse, btn));
		}
	};
	/**
	 * Is registered at the keyboard of the current {@link PRoot} when available.<br>
	 * This observer is used to pass down events to {@link PKeyboardObs PKeyboardObservers}
	 * that are registered at this {@link PComponent}.<br>
	 */
	protected final PKeyboardObs delegateKeyObs = new PKeyboardObs() {
		@Override
		public void onStringTyped(PKeyboard keyboard, String string) {
			keyboardObsList.fireEvent(obs -> obs.onStringTyped(keyboard, string));
		}
		@Override
		public void onModifierToggled(PKeyboard keyboard, Modifier modifier) {
			keyboardObsList.fireEvent(obs -> obs.onModifierToggled(keyboard, modifier));
		}
		@Override
		public void onKeyPressed(PKeyboard keyboard, Key key) {
			keyboardObsList.fireEvent(obs -> obs.onKeyPressed(keyboard, key));
		}
		@Override
		public void onKeyTriggered(PKeyboard keyboard, Key key) {
			keyboardObsList.fireEvent(obs -> obs.onKeyTriggered(keyboard, key));
		}
		@Override
		public void onKeyReleased(PKeyboard keyboard, Key key) {
			keyboardObsList.fireEvent(obs -> obs.onKeyReleased(keyboard, key));
		}
	};
	/**
	 * This field is true if the layout of this component needs to be laid out again.<br>
	 * After the layout has been laid out this variable should be set to false.<br>
	 */
	protected boolean needReLayout = true;
	/**
	 * The current root of this components GUI.<br>
	 * This value is updated in the {@link #parentObs} of this component when a
	 * change of the parents root is noticed.<br>
	 */
	private PRoot cachedRoot;
	private PBounds cachedBounds;
	private boolean cachedBoundsInvalid = true;
	protected final MutablePSize prefSize = new MutablePSize();
	private MutablePBounds bndsNoBorder;
	private PCursor mouseOverCursor = null;
	/**
	 * These fields are used to store the previous preferred size of this component.<br>
	 * After the layout has been laid out these values are checked against the
	 * new preferred size of this component. If the size has changed the
	 * preferredSizeChanged event is fired and these values are updated.<br>
	 */
	private int lastPrefW = -1;
	private int lastPrefH = -1;
	/**
	 * These fields are used to store the previous bounds of this component.<br>
	 * After this component has been laid out by its parents layout as detected
	 * via the {@link #parentLayoutObs}, the new bounds are checked against the
	 * old bounds and the {@link #fireBoundsChangedEvent()} method is called if
	 * the bounds have changed.<br>
	 */
	private int lastBndsX = -1;
	private int lastBndsY = -1;
	private int lastBndsW = -1;
	private int lastBndsH = -1;
	/**
	 * The components id will be displayed by the toString() method unless the id
	 * is null.<br> If the id is null the toString() method will show the components
	 * classes simple name.
	 */
	private String id = null;
	private Object styleID = getClass();
	/**
	 * Cached for removing observers
	 */
	private PKeyboard currentKeyboard;
	private boolean keyObsRegistered;
	private PMouse currentMouse;
	private boolean mouseObsRegistered;
	/**
	 * Used by the {@link #isElusive()} method
	 */
	private boolean elusive = DEFAULT_IS_ELUSIVE;
	
	/**
	 * The root is being cached by the {@link AbstractPComponent}.<br>
	 */
	@Override
	public PRoot getRoot() {
		if (cachedRoot != null) {
			return cachedRoot;
		}
		return null;
	}
	
	@Override
	public void setStyle(PStyleComponent style) {
		if (!Objects.equals(getStyle(), style)) {
			this.style = style;
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
			refreshBorderStyle();
		}
	}
	
	@Override
	public PStyleComponent getStyle() {
		return style;
	}
	
	protected void refreshBorderStyle() {
		PBorder border = getBorder();
		if (border == null) {
			return;
		}
		PStyleComponent style = getStyle();
		if (style == null) {
			border.setStyle(null);
		} else {
			PStyleBorder borderStyle = style.getBorderStyle(this, border);
			border.setStyle(borderStyle);
		}
	}
	
	@Override
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException {
		if (parent != null && this.parent != null) {
			throw new IllegalStateException(this+".getParent() != null");
		} if (parent != null && isDescendantOf(parent)) {
			throw new IllegalArgumentException(this+" is descendant of "+parent);
		}
		cachedBoundsInvalid = true;
		cachedBounds = null;
		lastPrefW = -1;
		lastPrefH = -1;
		lastBndsX = -1;
		lastBndsY = -1;
		lastBndsW = -1;
		lastBndsH = -1;
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
		if (this.parent instanceof PRoot) {
			setCachedRoot(oldParent, (PRoot) this.parent);
		} else {
			PRoot root = parent == null ? null : parent.getRoot();
			setCachedRoot(oldParent, root);
		}
		onParentChanged(oldParent);
		if (oldParent == null && this.parent != null) {
			fireAddedEvent();
		} else if (oldParent != null) {
			fireRemovedEvent(oldParent);
		}
	}
	
	private void setCachedRoot(PComponent oldParent, PRoot root) {
		if (cachedRoot == root) {
			return;
		}
		if (cachedRoot != null) {
			cachedRoot.removeObs(rootFocusObs);
			cachedRoot.fireComponentRemovedFromGui(oldParent, this);
		}
		cachedRoot = root;
		if (cachedRoot != null) {
			cachedRoot.addObs(rootFocusObs);
			cachedRoot.fireComponentAddedToGui(this);
		}
		fireRootChangedEvent();
		if (keyObsRegistered) {
			currentKeyboard.removeObs(delegateKeyObs);
		}
		currentKeyboard = cachedRoot == null ? null : cachedRoot.getKeyboard();
		keyObsRegistered = false;
		registerKeyBoardObs();
		if (mouseObsRegistered) {
			currentMouse.removeObs(delegateMouseObs);
		}
		currentMouse = cachedRoot == null ? null : cachedRoot.getMouse();
		if (currentMouse == null) {
			mouseOverCursor = null;
		} else {
			mouseOverCursor = cachedRoot.getMouse().getCursorDefault();
		}
		mouseObsRegistered = false;
		registerMouseObs();
	}
	
	private void registerKeyBoardObs() {
		if (keyObsRegistered && keyboardObsList.isEmpty()) {
			currentKeyboard.removeObs(delegateKeyObs);
			keyObsRegistered = false;
		} else if (!keyObsRegistered && currentKeyboard != null
				&& !keyboardObsList.isEmpty())
		{
			currentKeyboard.addObs(delegateKeyObs);
			keyObsRegistered = true;
		}
	}
	
	private void registerMouseObs() {
		if (mouseObsRegistered && mouseObsList.isEmpty()) {
			currentMouse.removeObs(delegateMouseObs);
			mouseObsRegistered = false;
		} else if (!mouseObsRegistered && currentMouse != null
				&& !mouseObsList.isEmpty())
		{
			currentMouse.addObs(delegateMouseObs);
			mouseObsRegistered = true;
		}
	}
	
	@Override
	public PComponent getParent() {
		return parent;
	}
	
	protected void setBorder(PBorder border) {
		if (getBorder() != null) {
			getBorder().removeObs(borderObs);
		}
		this.border = border;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
		if (getBorder() != null) {
			getBorder().addObs(borderObs);
			refreshBorderStyle();
		}
	}
	
	@Override
	public PBorder getBorder() {
		return border;
	}
	
	@Override
	public PBounds getBounds() {
		if (cachedBoundsInvalid) {
			cachedBounds = PComponent.super.getBounds();
			cachedBoundsInvalid = false;
		}
		return cachedBounds;
	}
	
	@Override
	public PBounds getBoundsWithoutBorder() {
		PBorder border = getBorder();
		if (border == null) {
			bndsNoBorder = null;
			return getBounds();
		}
		if (bndsNoBorder == null) {
			bndsNoBorder = new MutablePBounds(getBounds());
		} else {
			bndsNoBorder.set(getBounds());
		}
		bndsNoBorder.subtract(border.getDefaultInsets(this));
		return bndsNoBorder;
	}
	
//	@Override
//	public void setDesign(PDesign design) {
//		customDesign = design;
//		fireReRenderEvent();
//	}
//
//	/**
//	 * If this component has a custom {@link PDesign} that design is returned.<br>
//	 * If this component has a {@link PRoot} as returned by the {@link #getRoot()}
//	 * method then the design is retrieved from the {@link PDesignSheet} that
//	 * belongs to the root.<br>
//	 * If this component has neither a custom design nor a root null is returned.<br>
//	 */
//	@Override
//	public PDesign getDesign() {
//		if (customDesign != null) {
//			return customDesign;
//		}
//		PRoot root = getRoot();
//		if (root == null) {
//			return null;
//		}
//		return root.getDesignSheet().getDesignFor(this);
//	}
	
	/**
	 * Returns null.<br>
	 * Subclasses should overwrite this method to return a {@link PReadOnlyLayout}.<br>
	 */
	@Override
	public PReadOnlyLayout getLayout() {
		return null;
	}
	
	/**
	 * Does nothing.<br>
	 * This method should be overwritten by all subclasses of this class.<br>
	 */
	@Override
	public void defaultRender(PRenderer renderer) {
	}
	
	/**
	 * The default implementation of this method always returns true.<br>
	 * All components that are partially translucent or have transparent
	 * parts should override this method to return false.<br>
	 * 
	 * @return true
	 */
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	/**
	 * If this component has a layout the preferred size of the layout
	 * is returned. Otherwise a size of (0, 0) is returned.<br>
	 * The returned size is immutable, it will not update to represent changes.<br>
	 */
	@Override
	public PSize getDefaultPreferredSize() {
		PReadOnlyLayout layout = getLayout();
		if (layout != null) {
			prefSize.set(layout.getPreferredSize());
//			return layout.getPreferredSize();
		} else {
			prefSize.set(getConstantDefaultPreferredSize());
		}
		PBorder border = getBorder();
		if (border != null) {
			prefSize.add(border.getDefaultInsets(this));
		}
		return prefSize;
	}
	
	protected PSize getConstantDefaultPreferredSize() {
		return PSize.ZERO_SIZE;
	}
	
	/**
	 * Always returns null by default.<br>
	 */
	@Override
	public PDnDSupport getDragAndDropSupport() {
		return null;
	}
	
	/**
	 * Refreshes the layout as needed.<br>
	 * May fire a {@link #firePreferredSizeChangedEvent()} as necessary.<br>
	 */
	@Override
	public void reLayOut() {
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
	@Override
	public boolean isFocusable() {
		return false;
	}
	
	/**
	 * Sets the value that will be returned by the {@link #isElusive()} method
	 * hereafter.<br>
	 * 
	 * @param isElusive whether this component is elusive or not
	 * @see #DEFAULT_IS_ELUSIVE
	 * @see #isElusive()
	 */
	public void setElusive(boolean isElusive) {
		elusive = isElusive;
	}
	
	/**
	 * The behavior of this method can be changed with the {@link #setElusive(boolean)}
	 * method.<br>
	 * 
	 * @return whether this component is currently elusive or not
	 * @see #DEFAULT_IS_ELUSIVE
	 * @see #setElusive(boolean)
	 */
	@Override
	public boolean isElusive() {
		return elusive;
	}
	
	@Override
	public void setMouseOverCursor(PCursor cursor) {
		mouseOverCursor = cursor;
		fireMouseOverCursorChangedEvent();
	}
	
	@Override
	public PCursor getMouseOverCursor(PMouse mouse) {
		if (mouseOverCursor == null) {
			return mouse.getCursorDefault();
		}
		return mouseOverCursor;
	}
	
	@Override
	public void addObs(PComponentObs obs) throws NullPointerException {
		compObsList.add(obs);
	}
	
	@Override
	public void removeObs(PComponentObs obs) throws NullPointerException {
		compObsList.remove(obs);
	}
	
	@Override
	public void addObs(PFocusObs obs) throws NullPointerException {
		focusObsList.add(obs);
	}
	
	@Override
	public void removeObs(PFocusObs obs) throws NullPointerException {
		focusObsList.remove(obs);
	}
	
	@Override
	public void addObs(PMouseObs obs) throws NullPointerException {
		mouseObsList.add(obs);
		registerMouseObs();
	}
	
	@Override
	public void removeObs(PMouseObs obs) throws NullPointerException {
		mouseObsList.remove(obs);
	}
	
	@Override
	public void addObs(PKeyboardObs obs) throws NullPointerException {
		keyboardObsList.add(obs);
		registerKeyBoardObs();
	}
	
	@Override
	public void removeObs(PKeyboardObs obs) throws NullPointerException {
		keyboardObsList.remove(obs);
	}
	
	protected void fireRootChangedEvent() {
		compObsList.fireEvent(obs -> obs.onRootChanged(this, cachedRoot));
	}
	
	protected void fireAddedEvent() {
		compObsList.fireEvent(obs -> obs.onAdd(this));
	}
	
	protected void fireRemovedEvent(PComponent parent) {
		compObsList.fireEvent(obs -> obs.onRemove(parent, this));
	}
	
	protected void firePreferredSizeChangedEvent() {
//		System.out.println(this+".firePreferredSizeChangedEvent()");
		compObsList.fireEvent(obs -> obs.onPreferredSizeChanged(this));
	}
	
	protected void fireBoundsChangedEvent() {
		compObsList.fireEvent(obs -> obs.onBoundsChanged(this));
	}
	
	protected void fireReRenderEvent() {
		PRoot root = getRoot();
		if (root != null) {
			root.reRender(this);
		}
	}
	
	protected void fireMouseOverCursorChangedEvent() {
		PRoot root = getRoot();
		if (root != null) {
			root.onMouseOverCursorChanged(this);
		}
	}
	
	protected void fireReLayOutEvent() {
		needReLayout = true;
		PRoot root = getRoot();
		if (root != null) {
			root.reLayOut(this);
		}
	}
	
	protected void fireFocusGainedEvent(PComponent oldFocusOwner) {
		focusObsList.fireEvent(obs -> obs.onFocusGained(oldFocusOwner, this));
	}
	
	protected void fireFocusLostEvent() {
		focusObsList.fireEvent(obs -> obs.onFocusLost(this));
	}
	
	protected void checkForBoundsChange() {
		final PBounds currentBounds = getBounds();
		final int currentX;
		final int currentY;
		final int currentW;
		final int currentH;
		if (currentBounds == null) {
			currentX = -1;
			currentY = -1;
			currentW = 0;
			currentH = 0;
		} else {
			currentX = currentBounds.getX();
			currentY = currentBounds.getY();
			currentW = currentBounds.getWidth();
			currentH = currentBounds.getHeight();
		}
		if (lastBndsX != currentX
				|| lastBndsY != currentY
				|| lastBndsW != currentW
				|| lastBndsH != currentH)
		{
//			System.out.println("checkForBoundsChange this="+this
//					+"\n\told="+lastBndsX+", "+lastBndsY+", ="+lastBndsW+", ="+lastBndsH
//					+"\n\tnew="+currentX+", "+currentY+", ="+currentW+", ="+currentH);
			lastBndsX = currentX;
			lastBndsY = currentY;
			lastBndsW = currentW;
			lastBndsH = currentH;
			fireBoundsChangedEvent();
			fireReRenderEvent();
		}
	}
	
	protected void checkForPreferredSizeChange() {
		PSize currentPrefSize = getPreferredSize();
		if (lastPrefW != currentPrefSize.getWidth()
				|| lastPrefH != currentPrefSize.getHeight())
		{
//			System.out.println(this+".checkForPreferredSizeChange lastPrefW="
//					+lastPrefW+", lastPrefH="+lastPrefH+", size="+currentPrefSize);
			lastPrefW = currentPrefSize.getWidth();
			lastPrefH = currentPrefSize.getHeight();
			firePreferredSizeChangedEvent();
		}
	}
	
	protected void onRootChanged(PRoot oldRoot) {}
	
	protected void onParentChanged(PComponent oldParent) {}
	
	protected void onThisLaidOut(Object constraint) {}
	
	@Override
	public void setID(String value) {
		id = value;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public Object getStyleID() {
		return styleID;
	}
	
	@Override
	public String toString() {
		if (id == null) {
			return getClass().getSimpleName();
		}
		return id;
	}
	
	@Override
	public String getDebugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("[class=");
		sb.append(getClass().getSimpleName());
		sb.append(", id=");
		sb.append(getID());
		sb.append(", styleID=");
		sb.append(getStyleID());
		sb.append(", bounds=");
		sb.append(getBounds());
		sb.append(", prefSize=");
		sb.append(getPreferredSize());
		sb.append(", layout=");
		sb.append(getLayout());
		sb.append(", border=");
		sb.append(getBorder());
		sb.append("]");
		return sb.toString();
	}
}