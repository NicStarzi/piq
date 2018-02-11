package edu.udo.piq.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PBorder;
import edu.udo.piq.PBorderObs;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PCursor;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.PComponentActionMap;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.layouts.DefaultPLayoutPreference;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PLayoutObs;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.style.AbstractPStylable;
import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.style.PStyleComponent;
import edu.udo.piq.style.PStyleSheet;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class AbstractPComponent extends AbstractPStylable<PStyleComponent> implements PComponent {
	
	/**
	 * The value returned by {@link #isIgnoredByPicking()} if the user does not
	 * call the {@link #setElusive(boolean)} method is false.<br>
	 */
	public static final boolean DEFAULT_IS_ELUSIVE = false;
	
	/**
	 * Holds the parent of this component in the GUI tree.<br>
	 * This field is null if this component has no parent.<br>
	 */
	private PComponent parent;
	private PBorder border;
	protected final DefaultPLayoutPreference layoutPref = new DefaultPLayoutPreference();
	private PComponentLayoutData layoutData;
	protected PComponentActionMap actionMap;
	/**
	 * Holds all {@link PComponentObs PComponentObservers} of this component.
	 */
	protected final ObserverList<PComponentObs> compObsList
		= PiqUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PFocusObs PFocusObservers} of this component.
	 */
	protected final ObserverList<PFocusObs> focusObsList
		= PiqUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PMouseObs PMouseObservers} of this component.
	 */
	protected final ObserverList<PMouseObs> mouseObsList
		= PiqUtil.createDefaultObserverList();
	/**
	 * Holds all {@link PKeyboardObs PKeyboardObservers} of this component.
	 */
	protected final ObserverList<PKeyboardObs> keyboardObsList
		= PiqUtil.createDefaultObserverList();
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
		public void onRootChanged(PComponent component, PRoot currentRoot, PRoot oldRoot) {
			setCachedRoot(getParent(), currentRoot);
		}
	};
	/**
	 * Is registered at the layout of this components parent.<br>
	 * Notices when this component has been laid out to set the
	 * flag needReLayout to true.
	 */
	protected final PLayoutObs parentLayoutObs = new PLayoutObs() {
		@Override
		public void onChildLaidOut(PReadOnlyLayout layout, PComponentLayoutData data) {
			if (data.getComponent() == AbstractPComponent.this) {
				PComponentLayoutData oldData = layoutData;
				layoutData = data;
				fireLayoutDataChangedEvent(oldData);
				checkForBoundsChange();
				onThisLaidOut(data);
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
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			mouseObsList.fireEvent(obs -> obs.onButtonTriggered(mouse, btn, clickCount));
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			mouseObsList.fireEvent(obs -> obs.onButtonReleased(mouse, btn, clickCount));
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			mouseObsList.fireEvent(obs -> obs.onButtonPressed(mouse, btn, clickCount));
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
		public void onKeyPressed(PKeyboard keyboard, ActualKey key) {
			keyboardObsList.fireEvent(obs -> obs.onKeyPressed(keyboard, key));
		}
		@Override
		public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
			keyboardObsList.fireEvent(obs -> obs.onKeyTriggered(keyboard, key));
		}
		@Override
		public void onKeyReleased(PKeyboard keyboard, ActualKey key) {
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
	protected final MutablePSize prefSize = new MutablePSize();
	private MutablePBounds bndsNoBorder;
	protected PCursor mouseOverCursor = null;
	protected PFocusTraversal focusTrav = null;
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
	/**
	 * Cached for removing observers
	 */
	private PKeyboard currentKeyboard;
	private boolean keyObsRegistered;
	private PMouse currentMouse;
	private boolean mouseObsRegistered;
	/**
	 * Used by the {@link #isIgnoredByPicking()} method
	 */
	private boolean elusive = AbstractPComponent.DEFAULT_IS_ELUSIVE;
	
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
	
	@CallSuper
	@Override
	protected void onActiveStyleChanged(PStyleComponent oldActiveStyle, PStyleComponent newActiveStyle) {
		if (oldActiveStyle != null) {
			oldActiveStyle.removeStyledComponent(this);
		}
		if (newActiveStyle != null) {
			newActiveStyle.addStyledComponent(this);
		}
		refreshBorderAndLayoutStyle();
		if (getRoot() != null) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	}
	
	@Override
	protected void onStyleSizeChangedEvent() {
		firePreferredSizeChangedEvent();
	}
	
	@Override
	protected void onStyleReRenderEvent() {
		fireReRenderEvent();
	}
	
	@Override
	protected void onStyleIdChangedEvent() {
		PRoot root = getRoot();
		if (root == null) {
			return;
		}
		PStyleSheet sheet = root.getStyleSheet();
		if (sheet != null) {
			setCustomStyle(sheet.getStyleFor(this));
		}
	}
	
	protected void refreshBorderAndLayoutStyle() {
		PStyleComponent style = getStyle();
		
		PBorder border = getBorder();
		if (border != null) {
			if (style == null) {
				border.setInheritedStyle(null);
			} else {
				PStyleBorder borderStyle = style.getBorderStyle(this, border);
				border.setInheritedStyle(borderStyle);
			}
		}
//		PReadOnlyLayout layout = getLayout();
//		if (layout != null) {
//			if (style == null) {
//				layout.setInheritedStyle(null);
//			} else {
//				PStyleLayout layoutStyle = style.getLayoutStyle(this, layout);
//				layout.setInheritedStyle(layoutStyle);
//			}
//		}
	}
	
	@CallSuper
	@Override
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException {
		if (parent != null && this.parent != null) {
			throw new IllegalStateException(this+".getParent() != null");
		} if (parent != null && isDescendantOf(parent)) {
			throw new IllegalArgumentException(this+" is descendant of "+parent);
		}
		layoutData = null;
		lastPrefW = lastPrefH = -1;
		lastBndsX = lastBndsY = lastBndsW = lastBndsH = -1;
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
	
	@CallSuper
	private void setCachedRoot(PComponent oldParent, PRoot root) {
		PRoot oldCachedRoot = cachedRoot;
		if (oldCachedRoot == root) {
			return;
		}
		cachedRoot = null;
		if (oldCachedRoot != null) {
			oldCachedRoot.removeObs(rootFocusObs);
			oldCachedRoot.fireComponentRemovedFromGui(oldParent, this);
		}
		cachedRoot = root;
		if (cachedRoot != null) {
			cachedRoot.addObs(rootFocusObs);
			cachedRoot.fireComponentAddedToGui(this);
			if (needReLayout) {
				cachedRoot.scheduleLayout(this);
			}
		}
		fireRootChangedEvent(oldCachedRoot);
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
		if (currentMouse != null && !currentMouse.isCursorSupported(mouseOverCursor)) {
			mouseOverCursor = null;
		}
		mouseObsRegistered = false;
		registerMouseObs();
	}
	
	@CallSuper
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
	
	@CallSuper
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
			refreshBorderAndLayoutStyle();
		}
	}
	
	@Override
	public PBorder getBorder() {
		return border;
	}
	
	@Override
	public PBounds getBounds() {
		PComponentLayoutData data = getLayoutData();
		if (data == null) {// no parent
			return null;
		}
		return data.getComponentBounds();
	}
	
	@CallSuper
	@Override
	public PBounds getBoundsWithoutBorder() {
		PBounds bounds = getBounds();
		if (bounds == null) {
			return null;
		}
		PBorder border = getBorder();
		if (border == null) {
			bndsNoBorder = null;
			return bounds;
		}
		if (bndsNoBorder == null) {
			bndsNoBorder = new MutablePBounds(bounds);
		} else {
			bndsNoBorder.set(bounds);
		}
		bndsNoBorder.subtract(border.getInsets(this));
		return bndsNoBorder;
	}
	
	@Override
	public DefaultPLayoutPreference getLayoutPreference() {
		return layoutPref;
	}
	
	@CallSuper
	@Override
	public PComponentLayoutData getLayoutData() {
		// no parent => no layout data
		PComponent parent = getParent();
		if (parent == null) {
			return null;
		}
		/*
		 * layoutData is null if parent was changed but this component has not been laid out yet.
		 * In this case fetch layout data from the parent layout directly.
		 */
		if (layoutData == null) {
			// cache the fetched copy. This should never be null!
			layoutData = parent.getLayout().getDataFor(this);
			ThrowException.ifNull(layoutData, "getParent().getLayout().getDataFor(this) == null");
		}
		return layoutData;
	}
	
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
		if (layout == null) {
			prefSize.set(getConstantDefaultPreferredSize());
		} else {
			prefSize.set(layout.getPreferredSize());
		}
		PBorder border = getBorder();
		if (border != null) {
			prefSize.add(border.getInsets(this));
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
	@CallSuper
	@Override
	public void redoLayOut() {
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
	
	@Override
	public void addActionMapping(PActionKey actionKey, PComponentAction action) {
		if (actionMap == null) {
			if (action == null) {
				return;
			}
			actionMap = new PComponentActionMap(this);
		}
		actionMap.addAction(actionKey, action);
	}
	
	@Override
	public void removeActionMapping(PActionKey actionKey) {
		if (actionMap == null) {
			return;
		}
		actionMap.removeAction(actionKey);
	}
	
	@Override
	public void clearActionMap() {
		if (actionMap == null) {
			return;
		}
		actionMap.clear();
		actionMap = null;
	}
	
	@Override
	public PComponentAction getAction(PActionKey actionKey) {
		if (actionMap == null) {
			return null;
		}
		return actionMap.getAction(actionKey);
	}
	
	@Override
	public Collection<PComponentAction> getAllActions() {
		return Collections.unmodifiableCollection(actionMap.getActions());
	}
	
	@Override
	public boolean hasAction(PComponentAction action) {
		return actionMap.hasAction(action);
	}
	
	protected void setFocusTraversal(PFocusTraversal focusTraversal) {
		focusTrav = focusTraversal;
	}
	
	@Override
	public PFocusTraversal getFocusTraversal() {
		return focusTrav;
	}
	
	/**
	 * Sets the value that will be returned by the {@link #isIgnoredByPicking()} method
	 * hereafter.<br>
	 * 
	 * @param isElusive whether this component is elusive or not
	 * @see #DEFAULT_IS_ELUSIVE
	 * @see #isIgnoredByPicking()
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
	public boolean isIgnoredByPicking() {
		return elusive;
	}
	
	@Override
	public void setMouseOverCursor(PCursor cursor) {
		if (!Objects.equals(mouseOverCursor, cursor)) {
			mouseOverCursor = cursor;
			fireMouseOverCursorChangedEvent();
		}
	}
	
	@Override
	public PCursor getMouseOverCursor(PMouse mouse) {
		if (mouseOverCursor == null) {
			return mouse.getCursorDefault();
		}
		return mouseOverCursor;
	}
	
//	public void requestScroll(int offsetX, int offsetY) {
//		fireScrollRequestEvent(this, offsetX, offsetY);
//	}
	
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
	
	protected void fireRootChangedEvent(PRoot oldRoot) {
		AbstractPComponent.this.onRootChanged(oldRoot);
		if (oldRoot != null) {
			AbstractPComponent.this.onRemovedFromUi(oldRoot);
		}
		PRoot currentRoot = cachedRoot;
		if (currentRoot != null) {
			AbstractPComponent.this.onAddedToUi(currentRoot);
		}
		compObsList.fireEvent(obs -> obs.onRootChanged(this, currentRoot, oldRoot));
	}
	
	protected void fireLayoutChangedEvent(PReadOnlyLayout oldLayout) {
		PReadOnlyLayout currentLayout = getLayout();
		compObsList.fireEvent(obs -> obs.onLayoutChanged(this, currentLayout, oldLayout));
	}
	
	protected void fireLayoutDataChangedEvent(PComponentLayoutData oldData) {
		PComponentLayoutData curData = getLayoutData();
		compObsList.fireEvent(obs -> obs.onLayoutDataChanged(this, curData));
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
	
//	protected void fireScrollRequestEvent(PComponent component, int offsetX, int offsetY) {
//		compObsList.fireEvent(obs -> obs.onScrollRequest(component, offsetX, offsetY));
//	}
	
	protected void fireReRenderEvent() {
		PRoot root = getRoot();
		if (root != null) {
			root.scheduleReRender(this);
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
			root.scheduleLayout(this);
		}
	}
	
	protected void fireFocusGainedEvent(PComponent oldFocusOwner) {
		focusObsList.fireEvent(obs -> obs.onFocusGained(oldFocusOwner, this));
	}
	
	protected void fireFocusLostEvent() {
		focusObsList.fireEvent(obs -> obs.onFocusLost(this));
	}
	
	@CallSuper
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
	
	@CallSuper
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
	
	@TemplateMethod
	protected void onRootChanged(PRoot oldRoot) {}
	
	@TemplateMethod
	protected void onAddedToUi(PRoot newRoot) {}
	
	@TemplateMethod
	protected void onRemovedFromUi(PRoot oldRoot) {}
	
	@TemplateMethod
	protected void onParentChanged(PComponent oldParent) {}
	
	@TemplateMethod
	protected void onThisLaidOut(PComponentLayoutData data) {}
	
	@Override
	public void setID(String value) {
		id = value;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String toString() {
		if (id == null) {
			return getClass().getSimpleName();
		}
		return id;
	}
}