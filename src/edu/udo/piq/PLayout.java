package edu.udo.piq;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

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
public interface PLayout {
	
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
	 * Adds the given {@link PComponent} to this layout with the given constraint.<br>
	 * If the component is already a child of this layout an {@link IllegalStateException} 
	 * will be thrown.<br>
	 * Furthermore if the constraint is already used by another component and no two 
	 * components may use the same constraint in this layout an {@link IllegalArgumentException} 
	 * will be thrown.<br>
	 * This method must set the parent of the newly added component to the owner of this 
	 * layout.<br>
	 * 
	 * @param component the component that is to be added
	 * @param constraint the constraint to be associated with this child
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if constraint is not a valid value
	 * @throws IllegalStateException if component is already a child of this layout
	 * @see #removeChild(Object)
	 * @see #removeChild(PComponent)
	 * @see #clearChildren()
	 * @see #getChildren()
	 */
	public void addChild(PComponent component, Object constraint) throws NullPointerException, IllegalArgumentException, IllegalStateException;
	
	/**
	 * Removes the given child from this layout.<br>
	 * If the given component is not a child of this layout an 
	 * {@link IllegalArgumentException} will be thrown.<br>
	 * This method must set the parent of the removed component to null.<br>
	 * 
	 * @param child the child that is to be removed
	 * @throws NullPointerException if child is null
	 * @throws IllegalArgumentException if child is not a child of this layout
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(Object)
	 * @see #clearChildren()
	 * @see #containsChild(PComponent)
	 */
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException;
	
	/**
	 * Removes the child that is registered with the associated constraint.<br>
	 * If no child was added with the given constraint an 
	 * {@link IllegalStateException} will be thrown.<br>
	 * This method must set the parent of the removed component to null.<br>
	 * 
	 * @param constraint the constraint for the child that is to be removed
	 * @throws IllegalArgumentException if constraint is not a valid constraint
	 * @throws IllegalStateException if no child was added with the given constraint
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(PComponent)
	 * @see #getChildConstraint(PComponent)
	 * @see #containsChild(Object)
	 */
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Removes all children of this layout if this layout contains any children.<br>
	 * For an empty layout this method does nothing.<br>
	 * This method must set the parent of all removed components to null.<br>
	 * 
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(PComponent)
	 * @see #removeChild(Object)
	 * @see #clearChildren()
	 */
	public void clearChildren();
	
	/**
	 * Returns true if child is a child of this layout, otherwise returns false.<br>
	 * 
	 * @param child the child component
	 * @return true if child is a child of this layout, otherwise false
	 * @throws NullPointerException if child is null
	 * @see #addChild(PComponent, Object)
	 * @see #containsChild(Object)
	 */
	public boolean containsChild(PComponent child) throws NullPointerException;
	
	/**
	 * Returns true if this layout contains a child component that is associated 
	 * with the given Constraints or false if no such child was added to this layout.<br>
	 * 
	 * @param constraint the constraints for the child
	 * @return true if a child with the given constraint exists, otherwise false
	 * @throws IllegalArgumentException if constraint is not a valid constraint for this layout
	 * @see #getChildConstraint(PComponent)
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(Object)
	 */
	public boolean containsChild(Object constraint) throws IllegalArgumentException;
	
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
	public PBounds getChildBounds(PComponent child) throws NullPointerException, IllegalArgumentException;
	
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
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(Object)
	 * @see #containsChild(Object)
	 */
	public Object getChildConstraint(PComponent child) throws NullPointerException, IllegalArgumentException;
	
	/**
	 * Returns an unmodifiable collection containing all children that 
	 * this layout currently has.<br>
	 * No assumptions about the order of the children in the returned 
	 * collection should be made.<br>
	 * Changes to the children of this layout will not be reflected by the 
	 * returned collection after this method has been called previously.<br>
	 * 
	 * @return an unmodifiable collection of all children.
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(PComponent)
	 * @see #removeChild(Object)
	 * @see #clearChildren()
	 */
	public Collection<PComponent> getChildren();
	
	/**
	 * Lays out this {@link PLayout}.<br>
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