package edu.udo.piq;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

import edu.udo.piq.layouts.AbstractMapPLayout;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

/**
 * A layout defines how components are added to a container in a GUI.<br>
 * The layout manages all children of its owner and places them according
 * to the owners size and associated constraints.<br>
 * A layout does also define the preferred size of its owner.<br>
 * PLayouts can be observed by {@link PLayoutObs}ervers using the observer
 * pattern. All implementations of PLayout should provide a way of adding
 * and removing observers without throwing {@link ConcurrentModificationException}s
 * if an observer is added / removed while an event is fired. The use of the
 * {@link PiqUtil#createDefaultObserverList()} is encouraged but is not enforced.<br>
 * <br>
 * For an easy to use abstract implementation of the PLayout interface
 * have a look at the {@link AbstractMapPLayout} class.
 * 
 * @author Nic Starzi
 * 
 * @see PLayoutObs
 * @see PComponent
 * @see PBounds
 */
public interface PReadOnlyLayout extends PDisposable, PStyleable<PStyleLayout> {
	
	public static final String ATTRIBUTE_KEY_INSETS = "insets";
	public static final String ATTRIBUTE_KEY_GAP = "gap";
	public static final String ATTRIBUTE_KEY_ALIGNMENT_X = "alignmentX";
	public static final String ATTRIBUTE_KEY_ALIGNMENT_Y = "alignmentY";
	
	public void invalidate();
	
	/**
	 * Lays out this {@link PReadOnlyLayout}.<br>
	 * This method should set the bounds for all children according
	 * to the associated constraints.<br>
	 */
	public void layOut();
	
	/**
	 * Returns the preferred {@link PSize} for this layout based on the children
	 * and their constraints.<br>
	 * This size can be used to determine the preferred size of the layouts
	 * owner.<br>
	 * This method never returns null.<br>
	 * <br>
	 * The use of the {@link PSize#ZERO_SIZE} is encouraged when returning a
	 * size of (0, 0).<br>
	 * 
	 * @return the preferred size of this layout
	 * @see PSize
	 * @see PSize#ZERO_SIZE
	 * @see #getChildBounds(PComponent)
	 * @see PComponent#getDefaultPreferredSize()
	 */
	public PSize getPreferredSize();
	
	/**
	 * Sets a custom {@link PStyleLayout} for this layout.<br>
	 * When a custom design is set the layout will take configuration information
	 * from the custom design if possible.<br>
	 * If no custom design is set the default design will be taken from the
	 * {@link PStyleSheet} of the root of the owner.<br>
	 * @param style		the custom design for this layout or null to use the default design
	 */
	@Override
	public void setStyle(PStyleLayout style);
	
	/**
	 * Returns the {@link PStyleLayout} used to configure this layout.<br>
	 * If this layout has a custom design set then the custom design is
	 * returned. Otherwise the returned design will be taken from the
	 * {@link PStyleSheet} of the root of the owner of this layout.<br>
	 * If this layout has no owner, or if the owner is not part of a GUI,
	 * and thus does not have a root, null is returned.<br>
	 *
	 * @return				the design where the configuration for this layout is stored
	 * @see PStyleLayout
	 * @see PStyleSheet
	 */
	@Override
	public PStyleLayout getStyle();
	
	/**
	 * Returns the owner of this layout.<br>
	 * The owner is the {@link PComponent} that uses this layout to
	 * manage its children.<br>
	 * Each layout should only have one owner that is never null and
	 * never changes over the life time of the layout.<br>
	 * 
	 * @return the owner of this layout
	 */
	public PComponent getOwner();
	
	public Iterable<PComponentLayoutData> getAllData();
	
	public int getChildCount();
	
	public default Iterable<PComponent> getChildren() {
		return new ComponentIterable(getAllData());
	}
	
	public default boolean isEmpty() {
		return getChildCount() == 0;
	}
	
	public default PComponentLayoutData getDataFor(PComponent child) {
		for (PComponentLayoutData data : getAllData()) {
			if (data.getComponent() == child) {
				return data;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if child is a child of this layout, otherwise returns false.<br>
	 * 
	 * @param child the child component
	 * @return true if child is a child of this layout, otherwise false
	 * @throws NullPointerException if child is null
	 * @see PLayout#addChild(PComponent, Object)
	 * @see #containsChild(Object)
	 */
	public default boolean containsChild(PComponent child) {
		return getOwner() == child.getParent();
	}
	
	/**
	 * Returns true if this layout contains a child component that is associated
	 * with the given Constraints or false if no such child was added to this layout.<br>
	 * 
	 * @param constraint the constraints for the child
	 * @return true if a child with the given constraint exists, otherwise false
	 * @throws IllegalArgumentException if constraint is not a valid constraint for this layout
	 * @see #getChildConstraint(PComponent)
	 * @see PLayout#addChild(PComponent, Object)
	 * @see PLayout#removeChild(Object)
	 */
	public default boolean containsChild(Object constraint) {
		return getChildForConstraint(constraint) != null;
	}
	
	/**
	 * Returns the {@link PBounds} within this layout for the child with the given
	 * constraint.<br>
	 * The returned bounds are never null.<br>
	 * 
	 * @param constraint					a valid constraint for this layout. Can be null.
	 * @return 								the {@link PBounds} for the child
	 * @throws IllegalStateException		if there is no child for the given constraint
	 * @throws IllegalArgumentException		if the constraint is not valid for this layout
	 */
	public default PBounds getChildBounds(Object constraint) {
		PComponent child = getChildForConstraint(constraint);
		ThrowException.ifNull(child, "getChildForConstraint(constraint) == null");
		return getChildBounds(child);
	}
	
	/**
	 * Returns the {@link PBounds} within this layout for the given child.<br>
	 * The returned bounds are determined by this layout based on the Constraint
	 * and other PComponents.<br>
	 * The returned bounds are never null.<br>
	 * 
	 * @param child a child component of this layout
	 * @return the {@link PBounds} for the child
	 * @throws NullPointerException if child is null
	 * @throws IllegalArgumentException if child is not a child of this layout
	 */
	public default PBounds getChildBounds(PComponent child) {
		ThrowException.ifNull(child, "child == null");
		PComponentLayoutData data = getDataFor(child);
		ThrowException.ifNull(data, "containsChild(child) == false");
		return data.getComponentBounds();
	}
	
	/**
	 * Returns the Constraint which was used when the child component has
	 * been added to this layout.<br>
	 * If the given component is not a child of this layout an
	 * {@link IllegalArgumentException} will be thrown.<br>
	 * 
	 * @param child the {@link PComponent} for which the Constraint is queried.
	 * @return the Constraint of the child or null if the argument is not a child of this layout
	 * @throws NullPointerException if child is null
	 * @throws IllegalArgumentException if child is not a child of this layout
	 * @see PLayout#addChild(PComponent, Object)
	 * @see PLayout#removeChild(Object)
	 * @see #containsChild(Object)
	 */
	public default Object getChildConstraint(PComponent child) {
		ThrowException.ifNull(child, "child == null");
		PComponentLayoutData data = getDataFor(child);
		ThrowException.ifNull(data, "containsChild(child) == false");
		return data.getConstraint();
	}
	
	/**
	 * Returns the child of this {@link PReadOnlyLayout} registered with the given constraint.<br>
	 * If no such child exists null is returned. If the constraint is not a valid constraint
	 * for this layout an exception is thrown.<br>
	 * If more then one such child exists the layout is allowed to decide which one should be
	 * returned.<br>
	 * 
	 * @param constraint					a valid constraint for this layout. This can be null.
	 * @return								a {@link PComponent} registered with the constraint, or null if no such component exists.
	 * @throws IllegalArgumentException		if the given constraint is not valid for this layout.
	 */
	public default PComponent getChildForConstraint(Object constraint) {
		for (PComponentLayoutData data : getAllData()) {
			if (Objects.equals(data.getConstraint(), constraint)) {
				return data.getComponent();
			}
		}
		return null;
	}
	
	/**
	 * Returns the child of this {@link PReadOnlyLayout} that contains the given coordinates.<br>
	 * If no such child exists null is returned.<br>
	 * If more then one such child exists the layout is allowed to decide which one should be
	 * returned.<br>
	 * 
	 * @param x coordinate on the X-axis in window space
	 * @param y coordinate on the Y-axis in window space
	 * @return a child of this layout that contains (x, y) or null if no such child exists
	 */
	public default PComponent getChildAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		}
		if (isEmpty()) {
			return null;
		}
		for (PComponentLayoutData data : getAllData()) {
			PComponent child = data.getComponent();
			if (child.isIgnoredByPicking()) {
				if (child.getLayout() != null) {
					PComponent grandChild = child.getLayout().getChildAt(x, y);
					if (grandChild != null) {
						return grandChild;
					}
				}
			} else if (data.getComponentBounds().contains(x, y)) {
				return child;
			}
		}
		return null;
	}
	
	public default <E> E getStyleAttribute(Object attrKey, E defaultValue) {
		PStyleLayout style = getStyle();
		if (style == null) {
			return defaultValue;
		}
		return style.getAttribute(this, attrKey, defaultValue);
	}
	
	/**
	 * Adds the layout observer to this layout.<br>
	 * A layout can have any number of observers and observers may be added
	 * more then once to a layout.<br>
	 * 
	 * @param obs the observer
	 * @throws NullPointerException if obs is null
	 */
	public void addObs(PLayoutObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the layout observer from this layout.<br>
	 * If the observer was not added to this layout previously nothing will happen.<br>
	 * 
	 * @param obs the observer
	 * @throws NullPointerException if obs is null
	 */
	public void removeObs(PLayoutObs obs) throws NullPointerException;
	
	/**
	 * Frees all resources that the layout may be holding.<br>
	 * This method is being called by the owner of the {@link PLayout} when it is
	 * no longer in use. After a layout has been disposed it may no longer be used.<br>
	 */
	@Override
	public default void dispose() {}
	
	public static class ComponentIterable implements Iterable<PComponent> {
		
		private final Iterable<PComponentLayoutData> allData;
		
		public ComponentIterable(Iterable<PComponentLayoutData> data) {
			allData = data;
		}
		
		@Override
		public Iterator<PComponent> iterator() {
			Iterator<PComponentLayoutData> iter = allData.iterator();
			return new Iterator<PComponent>() {
				@Override
				public PComponent next() {
					return iter.next().getComponent();
				}
				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}
			};
		}
		
	}
	
}