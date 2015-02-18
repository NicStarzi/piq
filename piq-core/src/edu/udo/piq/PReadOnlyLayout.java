package edu.udo.piq;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.util.PCompUtil;

/**
 * A layout defines how components are added to a container in a GUI.<br>
 * The layout manages all children of its owner and places them according 
 * to the owners size and associated constraints.<br>
 * A layout does also define the preferred size of its owner.<br>
 * PLayouts can be observed by {@link PLayoutObs}ervers using the observer 
 * pattern. All implementations of PLayout should provide a way of adding 
 * and removing observers without throwing {@link ConcurrentModificationException}s 
 * if an observer is added / removed while an event is fired. The use of the 
 * {@link CopyOnWriteArrayList} is encouraged but is not enforced.<br>
 * <br>
 * For an easy to use abstract implementation of the PLayout interface 
 * have a look at the {@link AbstractPLayout} class.
 * 
 * @author Nic Starzi
 * 
 * @see PLayoutObs
 * @see PComponent
 * @see PBounds
 */
public interface PReadOnlyLayout {
	
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
	
	/**
	 * Returns true if child is a child of this layout, otherwise returns false.<br>
	 * 
	 * @param child the child component
	 * @return true if child is a child of this layout, otherwise false
	 * @throws NullPointerException if child is null
	 * @see PLayout#addChild(PComponent, Object)
	 * @see #containsChild(Object)
	 */
	public boolean containsChild(PComponent child) 
			throws NullPointerException;
	
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
	public boolean containsChild(Object constraint) 
			throws IllegalArgumentException;
	
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
	public PBounds getChildBounds(PComponent child) 
			throws NullPointerException, IllegalArgumentException;
	
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
	public Object getChildConstraint(PComponent child) 
			throws NullPointerException, IllegalArgumentException;
	
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
		for (PComponent child : getChildren()) {
			if (child.isElusive()) {
				if (child.getLayout() != null) {
					PComponent grandChild = child.getLayout().getChildAt(x, y);
					if (grandChild != null) {
						return grandChild;
					}
				}
			} else if (getChildBounds(child).contains(x, y)) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Returns an unmodifiable collection containing all children that 
	 * this layout currently has.<br>
	 * No assumptions about the order of the children in the returned 
	 * collection should be made.<br>
	 * Changes to the children of this layout will not be reflected by the 
	 * returned collection after this method has been called previously.<br>
	 * 
	 * @return an unmodifiable collection of all children.
	 * @see PLayout#addChild(PComponent, Object)
	 * @see PLayout#removeChild(PComponent)
	 * @see PLayout#removeChild(Object)
	 * @see PLayout#clearChildren()
	 */
	public Collection<PComponent> getChildren();
	
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
	 * The use of the {@link PSize#NULL_SIZE} is encouraged when returning a 
	 * size of (0, 0).<br>
	 * 
	 * @return the preferred size of this layout
	 * @see PSize
	 * @see PSize#NULL_SIZE
	 * @see #getChildBounds(PComponent)
	 * @see PComponent#getDefaultPreferredSize()
	 * @see PDesign#getPreferredSize(PComponent)
	 * @see PCompUtil#getPreferredSizeOf(PComponent)
	 */
	public PSize getPreferredSize();
	
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
	
}