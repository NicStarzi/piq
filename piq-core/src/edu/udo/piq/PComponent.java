package edu.udo.piq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.function.Predicate;

import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.components.containers.PGlassPanel;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.dnd.PDnDTransfer;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.layouts.PLayoutPreference;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.scroll2.PScrollComponent;
import edu.udo.piq.style.PStyleComponent;
import edu.udo.piq.style.PStyleable;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.util.AncestorIterator;
import edu.udo.piq.util.BreadthFirstDescendantIterator;
import edu.udo.piq.util.DepthFirstDescendantIterator;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

/**
 * The key part of the piq GUI widget toolkit.<br>
 * PComponents define a tree like architecture where each component has a
 * reference to its parent and every parent has, in turn, a {@link PReadOnlyLayout}
 * that manages the components children.<br>
 * All kinds of user interaction is done by components and everything that
 * is displayed graphically is a component that is being rendered to a
 * {@link PRenderer}.<br>
 * This interface is supposed to provide the most basic key functionalities
 * that are needed to make the piq library work. All implementations of
 * PComponents may opt to add additional useful utility methods.<br>
 * When implementing a PComponent it is of prime importance that all methods
 * of the implementation act exactly as defined by their documentation.
 * Otherwise the newly implemented component might not correctly work together
 * with other components and could potentially derange the entire GUI.<br>
 * PComponents can be observed by {@link PComponentObs}ervers using the observer
 * pattern. All implementations of PComponent should provide a way of adding
 * and removing observers without throwing {@link ConcurrentModificationException}s
 * if an observer is added / removed while an event is fired. The use of
 * {@link PiqUtil#createDefaultObserverList()} is encouraged but is not enforced.<br>
 * <br>
 * For an easy to use abstract implementation of the PComponent interface
 * have a look at the {@link AbstractPComponent} class.
 * 
 * @author Nic Starzi
 * 
 * @see AbstractPComponent
 * @see PRoot
 * @see PRenderer
 * @see PReadOnlyLayout
 * @see PStyleComponent
 * @see PComponentObs
 */
public interface PComponent extends PStyleable<PStyleComponent> {
	
	/**
	 * This method is supposed to be used internally by a {@link PReadOnlyLayout} to set
	 * the newly added or removed child's parent.<br>
	 * This method should not be used by the end user or unpredictable, and most
	 * likely erroneous, behavior will occur.<br>
	 * If parent is null this component will no longer have a parent.<br>
	 * 
	 * @param parent the newly set parent of this component or null
	 * @throws IllegalArgumentException if parent is a descendant of this component
	 * @throws IllegalStateException if this component already has a non-null parent
	 * @see #getParent()
	 * @see PLayout#addChild(PComponent, Object)
	 * @see PLayout#removeChild(Object)
	 * @see PLayout#removeChild(PComponent)
	 * @see PLayout#clearChildren()
	 * @see #isDescendantOf(PComponent)
	 */
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Returns the parent component of this component in the GUI-tree.<br>
	 * If this component is not part of a GUI-tree this method
	 * will return null.<br>
	 * 
	 * @return the parent of this component or null
	 * @see #setParent(PComponent)
	 */
	public PComponent getParent();
	
	/**
	 * This method returns the default preferred size for this component
	 * used by the default rendering mechanism.<br>
	 * This method should return a size as small as possible for rendering
	 * itself with the {@link #defaultRender(PRenderer)} method.<br>
	 * {@link PReadOnlyLayout}s and {@link PDesign}s might use this value or ignore
	 * it completely.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the preferred size for the default render
	 * @see #defaultRender(PRenderer)
	 * @see PSize
	 * @see PDesign
	 * @see PDesign#getPreferredSize(PComponent)
	 * @see PReadOnlyLayout
	 * @see PReadOnlyLayout#getChildBounds(PComponent)
	 */
	public default PSize getDefaultPreferredSize() {
		PReadOnlyLayout layout = getLayout();
		if (layout != null) {
			return layout.getPreferredSize();
		}
		return PSize.ZERO_SIZE;
	}
	
	public default PSize getPreferredSize() {
		PStyleComponent style = getStyleFromSheet();
		if (style == null) {
			return getDefaultPreferredSize();
		}
		return style.getPreferredSize(this);
	}
	
	public PComponentLayoutData getLayoutData();
	
	/**
	 * Returns true if this {@link PComponent} may become the focus owner of a GUI.<br>
	 * If this method returns false this method will be ignored when the user is
	 * traversing the focus through the GUI.<br>
	 * A component that is not focusable may still get the focus programmatically
	 * through the use of the {@link PRoot#setFocusOwner(PComponent)} or
	 * {@link #takeFocus()} method.<br>
	 * 
	 * @return true if this component should be included in focus traversal
	 * @see PRoot#setFocusOwner(PComponent)
	 * @see PRoot#getFocusOwner()
	 * @see #takeFocus()
	 * @see #hasFocus()
	 */
	public boolean isFocusable();
	
	public default boolean isStrongFocusOwner() {
		return false;
	}
	
	public void setCustomStyle(PStyleComponent style);
	
	public PStyleComponent getCustomStyle();
	
	@Override
	public void setStyleFromSheet(PStyleComponent style);
	
	@Override
	public PStyleComponent getStyleFromSheet();
	
	public PBorder getBorder();
	
	/**
	 * Returns the {@link PReadOnlyLayout} of this component.<br>
	 * If this component does not have a layout or does not
	 * support layouts this method will return null.<br>
	 * 
	 * @return the layout of this component or null.
	 * @see PReadOnlyLayout
	 */
	public PReadOnlyLayout getLayout();
	
	/**
	 * Renders the default rendering of this component to the given
	 * {@link PRenderer}.<br>
	 * This method is called if the {@link PDesignSheet} of the
	 * {@link PRoot} of this component does not associate a
	 * {@link PDesign} with this component.<br>
	 * 
	 * @param renderer the renderer to be used for rendering
	 * @see PRenderer
	 * @see PDesignSheet
	 * @see PDesign
	 */
	public default void defaultRender(PRenderer renderer) {}
	
	public default void render(PRenderer renderer) {
		PStyleComponent style = getStyleFromSheet();
		if (style == null) {
			defaultRender(renderer);
		} else {
			style.render(renderer, this);
		}
	}
	
	/**
	 * Returns true if this component fills all pixels within its
	 * {@link PBounds} when the {@link #defaultRender(PRenderer)}
	 * method is invoked.<br>
	 * This method is important for the {@link PRenderer} to
	 * determine whether the parent of this component needs to be
	 * re-rendered when this component is re-rendered.<br>
	 * For a component which is translucent or has transparent parts
	 * this method should always return false.<br>
	 * 
	 * @return true if the component is completely opaque
	 * @see #defaultRender(PRenderer)
	 * @see PRenderer
	 * @see PDesignSheet
	 * @see PDesign
	 */
	public default boolean defaultFillsAllPixels() {
		return false;
	}
	
	public default boolean fillsAllPixels() {
		PBorder border = getBorder();
		if (border != null && !border.fillsAllPixels(this)) {
			return false;
		}
		PStyleComponent style = getStyleFromSheet();
		if (style == null) {
			return defaultFillsAllPixels();
		}
		return style.fillsAllPixels(this);
	}
	
	/**
	 * Returns the mouse cursor to be used when the mouse is over
	 * this component.<br>
	 * @param mouse		a non-null mouse that belongs to the root of this components GUI tree
	 * @return			a non-null instance of PCursor compatible with the given mouse
	 * @see PMouse#getCursorDefault()
	 * @see PMouse#getCursorText()
	 * @see PMouse#getCursorBusy()
	 * @see PMouse#getCursorHand()
	 * @see PMouse#getCursorScroll()
	 * @see PRoot#createCustomCursor(PImageResource, int, int)
	 */
	public default PCursor getMouseOverCursor(PMouse mouse) {
		return mouse.getCursorDefault();
	}
	
	/**
	 * @param cursor
	 * @see PRoot#onMouseOverCursorChanged(PComponent)
	 */
	public void setMouseOverCursor(PCursor cursor);
	
	public void addActionMapping(PActionKey actionKey, PComponentAction action);
	
	public default void removeActionMapping(PActionKey actionKey) {
		addActionMapping(actionKey, null);
	}
	
	public void clearActionMap();
	
	public PComponentAction getAction(PActionKey actionKey);
	
	public Collection<PComponentAction> getAllActions();
	
	public default boolean hasActionForKey(PActionKey actionKey) {
		return getAction(actionKey) != null;
	}
	
	public boolean hasAction(PComponentAction action);
	
	/**
	 * Returns the {@link PFocusTraversal} for this component or null if this
	 * component is not part of a GUI tree.<br>
	 * <br>
	 * If this component does not have its own focus traversal the focus traversal
	 * of the parent component will be returned. A PRoot does always have a focus
	 * traversal.<br>
	 * 
	 * @return an instance of {@link PFocusTraversal} or null
	 */
	public default PFocusTraversal getFocusTraversal() {
		return null;
	}
	
	/**
	 * Returns true if this {@link PComponent} should not be returned by the
	 * {@link PReadOnlyLayout#getChildAt(int, int)} method.<br>
	 * This property can be used to construct components that can be placed on top
	 * of other components without obstructing the components below. For example to
	 * keep their original behavior unchanged.<br>
	 * One component that makes use of this attribute is the {@link PGlassPanel}.<br>
	 * Most components return false for this attribute.<br>
	 * 
	 * @return true if this component can not be returned by {@link PReadOnlyLayout#getChildAt(int, int)}
	 */
	public boolean isIgnoredByPicking();
	
	/**
	 * Returns either an instance of {@link PDnDSupport} if this component supports
	 * drag and drop or returns null if this component does not support drag and drop.<br>
	 * The {@link PDnDSupport} of a component may change within the components life-
	 * cycle but this must never happen while a drag is taking place on the support.<br>
	 * 
	 * @return an instance of {@link PDnDSupport} if drag and drop is supported, or null if drag and drop is not supported
	 * @see PDnDManager
	 * @see PDnDSupport
	 * @see PDnDTransfer
	 */
	public PDnDSupport getDragAndDropSupport();
	
	/**
	 * Calls the {@link PLayout#layOut()} method on the {@link PLayout} of this
	 * {@link PComponent} if it exists. Otherwise nothing happens.<br>
	 * If this {@link PComponent PComponents} preferred size, as returned by
	 * {@link PiqUtil#getPreferredSizeOf(PComponent)} changes as a result of
	 * the layouting this component will call the {@link PRoot#scheduleLayout(PComponent)}
	 * method of its {@link PRoot}.<br>
	 * 
	 * @see #getRoot()
	 * @see PRoot#scheduleLayout(PComponent)
	 */
	public void redoLayOut();
	
	/**
	 * Adds an observer to this {@link PComponent}.<br>
	 * A component can have any number of observers and observers may be added
	 * more then once to a component.<br>
	 * 
	 * @param obs the obs to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PComponentObs)
	 */
	public void addObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the observer from this component.<br>
	 * If the observer was not added to this component previously nothing will happen.<br>
	 * 
	 * @param obs the obs to be unregistered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PComponentObs)
	 */
	public void removeObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Adds an observer to this {@link PComponent}.<br>
	 * A component can have any number of observers and observers may be added
	 * more then once to a component.<br>
	 * 
	 * @param obs the obs to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PFocusObs)
	 * @see PRoot#addObs(PFocusObs)
	 */
	public void addObs(PFocusObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the observer from this component.<br>
	 * If the observer was not added to this component previously nothing will happen.<br>
	 * 
	 * @param obs the obs to be unregistered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PFocusObs)
	 * @see PRoot#addObs(PFocusObs)
	 */
	public void removeObs(PFocusObs obs) throws NullPointerException;
	
	public void addObs(PMouseObs obs) throws NullPointerException;
	
	public void removeObs(PMouseObs obs) throws NullPointerException;
	
	public void addObs(PKeyboardObs obs) throws NullPointerException;
	
	public void removeObs(PKeyboardObs obs) throws NullPointerException;
	
	/**
	 * Sets the id of this {@link PComponent}.<br>
	 * The id should only be used for debugging purposes and has no meaning to the end
	 * user whatsoever.<br>
	 * The id might be null.<br>
	 * 
	 * @param value the new id for this component
	 * @see #getID()
	 * @see PiqUtil#getDescendantByID(PComponent, String)
	 * @see PGuiUtil#componentToString(PComponent)
	 * @see PGuiUtil#guiTreeToString(PComponent)
	 */
	public void setID(String value);
	
	/**
	 * The id of a {@link PComponent} should only be used for debugging purposes and
	 * has no meaning to the end user whatsoever.<br>
	 * The id might be null in which case it should be treated as if it was equal to
	 * the simple name of the components class.<br>
	 * 
	 * @return the id use for debugging purposes
	 * @see #setID(String)
	 * @see PiqUtil#getDescendantByID(PComponent, String)
	 * @see PGuiUtil#componentToString(PComponent)
	 * @see PGuiUtil#guiTreeToString(PComponent)
	 */
	public String getID();
	
	/**
	 * Returns a detailed representation of this {@link PComponent PComponents} inner
	 * state. The returned String should at least contain the components class, id,
	 * the bounds and the layout.<br>
	 * A component implementation is free to provide any other useful information.<br>
	 * 
	 * @return a String representing all important information for this component
	 */
	public default String getDebugInfo() {
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
	
	@Override
	public default Object getStyleID() {
		return getClass();
	}
	
	/*
	 * Default convenience methods
	 */
	
	/**
	 * Returns the {@link PRoot} of the GUI that this component is a part of.<br>
	 * If this component is not part of a GUI this method will return null.<br>
	 * 
	 * @return the root of this GUI or null
	 * @see PRoot
	 * @see #getParent()
	 */
	public default PRoot getRoot() {
		PComponent comp = this;
		while (comp.getParent() != null) {
			comp = comp.getParent();
		}
		if (comp instanceof PRoot) {
			return (PRoot) comp;
		}
		return null;
	}
	
	/**
	 * Returns the number of times one has to call the {@link #getParent()} method
	 * recursively until one reaches the {@link PRoot} of this {@link PComponent}.<br>
	 * If this {@link PComponent} is a {@link PRoot} this method returns 0.<br>
	 * If this {@link PComponent} is not part of a GUI and thus does not have a
	 * {@link PRoot} this method returns -1;
	 * 
	 * @return the number of steps to the root or -1
	 * @see #getRoot()
	 * @see #getParent()
	 */
	public default int getDepth() {
		int depth = 0;
		PRoot root = getRoot();
		PComponent current = this;
		while (current.getParent() != null) {
			current = current.getParent();
			depth++;
		}
		if (current == root) {
			return depth;
		}
		return -1;
	}
	
	/**
	 * If this component has a parent then the {@link PBounds} of this component
	 * will be defined by the parents {@link PReadOnlyLayout}.<br>
	 * If this component is a {@link PRoot} then the {@link PBounds} will be
	 * returned directly.<br>
	 * If this component does neither have a parent nor is a {@link PRoot} null
	 * is returned.<br>
	 * 
	 * @return the {@link PBounds} of this component or null
	 * @see PReadOnlyLayout#getChildBounds(PComponent)
	 * @see PRoot#getBounds()
	 */
	public default PBounds getBounds() {
		PComponent parent = getParent();
		if (parent != null) {
			PBounds bnds = parent.getLayout().getChildBounds(this);
			ThrowException.ifNull(bnds, "bounds == null");
			return bnds;
		}
		if (this instanceof PRoot) {
			return ((PRoot) this).getBounds();
		}
		return null;
	}
	
	public default PInsets getBorderInsets() {
		PBorder border = getBorder();
		if (border == null) {
			return PInsets.ZERO_INSETS;
		}
		return border.getInsets(this);
	}
	
	public default PBounds getBoundsWithoutBorder() {
		PBorder border = getBorder();
		if (border == null) {
			return getBounds();
		}
		return getBounds().createCopyAndSubtract(border.getInsets(this));
	}
	
	public default PLayoutPreference getLayoutPreference() {
		return PLayoutPreference.DEFAULT_LAYOUT_PREFERENCE;
	}
	
	public default Object getConstraintAtParent() {
		ThrowException.ifNull(getParent(), "getParent() == null");
		return getParent().getLayout().getChildConstraint(this);
	}
	
	/**
	 * Returns a {@link Collection} of all child components of this component.<br>
	 * If this component has a {@link PReadOnlyLayout} then the method {@link PReadOnlyLayout#getChildren()}
	 * is used on the layout to retrieve the children.<br>
	 * If this component does not have a {@link PReadOnlyLayout} an unmodifiable empty list is
	 * returned.<br>
	 * This method does never return null.<br>
	 * <br>
	 * The returned Collection is unmodifiable and is not synchronized with the
	 * layout.<br>
	 * 
	 * @return a Collection of {@link PComponent PComponents} that are children of this component
	 * @see PReadOnlyLayout#getChildren()
	 */
	public default Iterable<PComponent> getChildren() {
		if (getLayout() != null) {
			return getLayout().getChildren();
		}
		return Collections.emptyList();
	}
	
	public default Iterable<PComponent> getAncestors() {
		return new AncestorIterator(this, false);
	}
	
	public default Iterable<PComponent> getAncestorsAndSelf() {
		return new AncestorIterator(this, true);
	}
	
	public default Iterable<PComponent> getDescendants() {
		return new BreadthFirstDescendantIterator(this);
	}
	
	/**
	 * Returns the first descendant of root with the given ID if there is any.<br>
	 * If no such {@link PComponent} exists in the GUI null will be returned.<br>
	 * If the root itself has the given ID the root will be returned.<br>
	 * 
	 * @param root the PComponent from where we start to search
	 * @param id the ID for which we are searching for
	 * @return a PComponent with the given ID or null if no such component exists
	 * @throws IllegalArgumentException if either root or id are null
	 * @see PComponent#setID(String)
	 * @see PComponent#getID()
	 */
	public default PComponent getFirstDescendantWithID(String id) throws IllegalArgumentException {
		ThrowException.ifNull(id, "id == null");
		for (PComponent current : new DepthFirstDescendantIterator(this)) {
			if (id.equals(current.getID())) {
				return current;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public default <T> T getFirstDescendantOfType(Class<T> descendantType) {
		ThrowException.ifNull(descendantType, "descendantType == null");
		for (PComponent current : new DepthFirstDescendantIterator(this)) {
			if (descendantType.isInstance(current)) {
				return (T) current;
			}
		}
		return null;
	}
	
	public default PComponent getFirstDescendantMatchingCondition(Predicate<PComponent> condition) {
		ThrowException.ifNull(condition, "condition == null");
		for (PComponent current : new DepthFirstDescendantIterator(this)) {
			if (condition.test(current)) {
				return current;
			}
		}
		return null;
	}
	
	public default Collection<PComponent> getAllDescendantsMatchingCondition(Predicate<PComponent> condition) {
		ThrowException.ifNull(condition, "condition == null");
		Collection<PComponent> result = null;
		PComponent first = null;
		for (PComponent current : new DepthFirstDescendantIterator(this)) {
			if (condition.test(current)) {
				if (first == null) {
					first = current;
				} else if (result == null) {
					result = new ArrayList<>();
					result.add(current);
				} else {
					result.add(current);
				}
			}
		}
		if (result == null) {
			if (first == null) {
				return Collections.emptyList();
			}
			return Collections.singleton(first);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public default <T> Collection<T> getAllDescendantsOfType(Class<T> descendantType) {
		ThrowException.ifNull(descendantType, "descendantType == null");
		Collection<T> result = null;
		T first = null;
		for (PComponent current : new DepthFirstDescendantIterator(this)) {
			if (descendantType.isInstance(current)) {
				if (first == null) {
					first = (T) current;
				} else if (result == null) {
					result = new ArrayList<>();
					result.add((T) current);
				} else {
					result.add((T) current);
				}
			}
		}
		if (result == null) {
			if (first == null) {
				return Collections.emptyList();
			}
			return Collections.singleton(first);
		}
		return result;
	}
	
	public default PComponent getFirstAncestorWithID(String id) {
		ThrowException.ifNull(id, "id == null");
		for (PComponent current : new AncestorIterator(this, true)) {
			if (id.equals(current.getID())) {
				return current;
			}
		}
		return null;
	}
	
	public default PComponent getFirstAncestorMatchingCondition(Predicate<PComponent> condition) {
		ThrowException.ifNull(condition, "condition == null");
		for (PComponent current : new AncestorIterator(this, true)) {
			if (condition.test(current)) {
				return current;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public default <T> T getFirstAncestorOfType(Class<T> ancestorType) {
		ThrowException.ifNull(ancestorType, "ancestorType == null");
		for (PComponent current : new AncestorIterator(this, true)) {
			if (ancestorType.isInstance(current)) {
				return (T) current;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if any positive number of steps up in the GUI hierarchy,
	 * starting from maybeDescendant, will get to this component.<br>
	 * A component is not an ancestor of itself.<br>
	 * 
	 * @param maybeDescendant the component for which the hierarchy is tested
	 * @return true if this is an ancestor of maybeDescendant, false otherwise
	 * @throws NullPointerException if maybeDescendant is null
	 * @see #isDescendantOf(PComponent)
	 * @see #getParent()
	 */
	public default boolean isAncestorOf(PComponent maybeDescendant) {
		if (maybeDescendant == null) {
			return false;
		}
		PComponent ancestor = maybeDescendant.getParent();
		while (ancestor != null) {
			if (ancestor == this) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}
	
	/**
	 * Returns true if any positive number of steps up in the GUI hierarchy,
	 * starting from this component, will get to maybeAncestor.<br>
	 * A component is not a descendant of itself.<br>
	 * 
	 * @param maybeAncestor the component for which the hierarchy is tested
	 * @return true if this is a descendant of maybeAncestor, false otherwise
	 * @throws NullPointerException if maybeAncestor is null
	 * @see #isAncestorOf(PComponent)
	 * @see #getParent()
	 */
	public default boolean isDescendantOf(PComponent maybeAncestor) {
		if (maybeAncestor == null) {
			return false;
		}
		return maybeAncestor.isAncestorOf(this);
	}
	
	/**
	 * Returns the {@link PMouse} installed in the {@link PRoot} of this
	 * component.<br>
	 * If this component is not part of a GUI or the GUI does not support a
	 * {@link PMouse} null is returned.<br>
	 * 
	 * @return the {@link PMouse} for this GUI or null
	 * @see #getRoot()
	 * @see PRoot#getMouse()
	 */
	public default PMouse getMouse() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getMouse();
	}
	
	/**
	 * Returns the {@link PKeyboard} installed in the {@link PRoot} of this
	 * component.<br>
	 * If this component is not part of a GUI or the GUI does not support a
	 * {@link PKeyboard} null is returned.<br>
	 * 
	 * @return the {@link PKeyboard} for this GUI or null
	 * @see #getRoot()
	 * @see PRoot#getKeyboard()
	 */
	public default PKeyboard getKeyboard() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getKeyboard();
	}
	
	/**
	 * Returns the drag and drop manager installed in the {@link PRoot} of this
	 * component.<br>
	 * If this component is not part of a GUI or the GUI does not support a
	 * {@link PDnDManager} null is returned.<br>
	 * 
	 * @return the {@link PDnDManager} for this GUI or null
	 * @see #getRoot()
	 * @see PRoot#getDragAndDropManager()
	 */
	public default PDnDManager getDragAndDropManager() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getDragAndDropManager();
	}
	
	/**
	 * Returns the portion of this components {@link PBounds} that is not
	 * clipped by the bounds of an ancestor of this component.<br>
	 * More specifically the returned {@link PBounds} are those bounds that
	 * are created by recursively intersecting this components bounds with
	 * its parents bounds.<br>
	 * The returned {@link PBounds} are not synchronized with this components
	 * actual bounds and may or may not be immutable.<br>
	 * <br>
	 * If the clipped bounds of this component would have negative size, or
	 * if this component has no parent, null is returned instead.<br>
	 * 
	 * @return the clipped {@link PBounds} of this component or null
	 * @see #getBounds()
	 * @see PBounds#createIntersection(PBounds)
	 * @see PiqUtil#fillClippedBounds(MutablePBounds, PComponent)
	 */
	public default PBounds getClippedBounds() {
		if (getParent() == null) {
			return null;
		}
		return PiqUtil.fillClippedBounds(null, this);
	}
	
	/**
	 * Returns true if this component is part of a GUI with a mouse and the mouse
	 * is within the clipped bounds of this component.<br>
	 * If any of the above conditions are not met false is returned.<br>
	 * Please note the difference between this method and {@link #isMouseOver()}
	 * is, that this method will return true even if other components are displayed
	 * on top of this component.<br>
	 * 
	 * @return true if this component is part of a GUI with a mouse and the mouse position is within the clipped bounds of this component
	 * @see #getClippedBounds()
	 * @see #getMouse()
	 * @see PBounds#contains(int, int)
	 */
	public default boolean isMouseWithinClippedBounds() {
		PMouse mouse = getMouse();
		if (mouse == null) {
			return false;
		}
		PBounds bounds = getClippedBounds();
		if (bounds == null) {
			return false;
		}
		return bounds.contains(mouse.getX(), mouse.getY());
	}
	
	/**
	 * Returns true if the {@link PMouse} of this {@link PComponent PComponents} {@link PRoot}
	 * is currently sitting on top of this component.<br>
	 * If this component is not part of a GUI or the GUI does not have a {@link PMouse} false
	 * is returned.<br>
	 * 
	 * @return true if the mouse is currently on top of this component
	 * @see #isMouseOverThisOrChild()
	 * @see #getMouse()
	 * @see PiqUtil#getComponentAt(PComponent, int, int)
	 */
	public default boolean isMouseOver() {
		PMouse mouse = getMouse();
		if (mouse == null) {
			return false;
		}
		return isMouseOver(mouse);
	}
	
	public default boolean isMouseOver(PMouse mouse) {
		PComponent compAtMouse = mouse.getComponentAtMouse();
		return compAtMouse == this;
	}
	
	/**
	 * Returns true if the {@link PMouse} of this {@link PComponent PComponents} {@link PRoot}
	 * is currently sitting on top of this component.<br>
	 * If this component is not part of a GUI or the GUI does not have a {@link PMouse} false
	 * is returned.<br>
	 * 
	 * @return true if the mouse is currently on top of this component
	 * @see #isMouseOver()
	 * @see #getMouse()
	 * @see PiqUtil#getComponentAt(PComponent, int, int)
	 */
	public default boolean isMouseOverThisOrChild() {
		PMouse mouse = getMouse();
		if (mouse == null) {
			return false;
		}
		return isMouseOverThisOrChild(mouse);
	}
	
	public default boolean isMouseOverThisOrChild(PMouse mouse) {
		PComponent compAtMouse = mouse.getComponentAtMouse();
		return compAtMouse != null && (compAtMouse == this
				|| isAncestorOf(compAtMouse));
	}
	
	/**
	 * Returns true if this component is currently holding the focus within its
	 * GUI.<br>
	 * If this component is not part of a GUI this method will return false.<br>
	 * 
	 * @return true if this component is part of a GUI and holds the focus within it
	 * @see PRoot#getFocusOwner()
	 * @see PRoot#setFocusOwner(PComponent)
	 * @see #takeFocus()
	 * @see #releaseFocus()
	 */
	public default boolean hasFocus() {
		PRoot root = getRoot();
		if (root == null) {
			return false;
		}
		return root.getFocusOwner() == this;
	}
	
	public default boolean thisOrChildHasFocus() {
		PRoot root = getRoot();
		if (root == null) {
			return false;
		}
		PComponent focusOwner = root.getFocusOwner();
		return focusOwner == this || isAncestorOf(focusOwner);
	}
	
	public default PComponent getHighestFocusableAncestor() {
		PComponent last = isFocusable() ? this : null;
		for (PComponent anc : getAncestors()) {
			if (anc.isFocusable()) {
				last = anc;
			}
		}
		return last;
	}
	
	/**
	 * Makes this component the focus owner of its GUI.<br>
	 * 
	 * @throws IllegalStateException if component is not part of a GUI
	 * @see PRoot#getFocusOwner()
	 * @see PRoot#setFocusOwner(PComponent)
	 * @see #hasFocus()
	 * @see #releaseFocus()
	 */
	public default void takeFocus() {
		PRoot root = getRoot();
		ThrowException.ifNull(root, "getRoot() == null");
		root.setFocusOwner(this);
	}
	
	public default void tryToTakeFocus() {
		PRoot root = getRoot();
		if (root != null) {
			root.setFocusOwner(this);
		}
	}
	
	public default PComponent getFirstFocusableAncestor() {
		PComponent current = this;
		while (current != null && !current.isFocusable()) {
			current = current.getParent();
		}
		return current;
	}
	
	public default void takeFocusNotFromDescendants() {
		PRoot root = getRoot();
		ThrowException.ifNull(root, "getRoot() == null");
		PComponent curFocusOwner = root.getFocusOwner();
		if (curFocusOwner != null && curFocusOwner.isDescendantOf(this)) {
			return;
		}
		root.setFocusOwner(this);
	}
	
	/**
	 * If this component currently holds the focus within its GUI the focus will
	 * be released and the new focus owner will be null.<br>
	 * 
	 * @throws IllegalStateException if this component is not part of a GUI or if this component does not have the focus
	 * @see PRoot#getFocusOwner()
	 * @see PRoot#setFocusOwner(PComponent)
	 * @see #hasFocus()
	 * @see #takeFocus()
	 */
	public default void releaseFocus() {
		PRoot root = getRoot();
		ThrowException.ifNull(root, "getRoot() == null");
		ThrowException.ifFalse(root.getFocusOwner() == this, "hasFocus() == false");
		root.setFocusOwner(null);
	}
	
	public default void requestScrollToRect(int x, int y, int fx, int fy) {
		for (PComponent ancestor : getAncestors()) {
			if (ancestor instanceof PScrollComponent) {
				PScrollComponent scrollArea = (PScrollComponent) ancestor;
				scrollArea.onScrollRequest(x, y, fx, fy);
			}
		}
	}
	
}