package edu.udo.piq;

import edu.udo.piq.layouts.PComponentLayoutData;

/**
 * An observer for {@link PReadOnlyLayout}s.<br>
 * These observers can be used to get a notification when 
 * a {@link PComponent} is added, removed or laid out by
 * a {@link PReadOnlyLayout}.<br>
 * 
 * @author Nic Starzi
 */
public interface PLayoutObs {
	
	/**
	 * <p>This method is called after a {@link PComponent} was added to a {@link PReadOnlyLayout layout}.
	 * The first argument passed to this method is the layout which the component was added to. The 
	 * second argument is a {@link PComponentLayoutData data object} which contains the component that was 
	 * added, as well as the constraint with which the component was added.</p>
	 * 
	 * <p>This method should only be called from within layout to notify its observers of a change.</p>
	 * 
	 * @param layout		the layout that the component was added to. This is never null.
	 * @param data			the data of the change. This is never null.
	 * @see PComponentLayoutData#getComponent()
	 * @see PComponentLayoutData#getConstraint()
	 * @see PReadOnlyLayout#getDataFor(PComponent)
	 * @see PComponent#getLayoutData()
	 */
	public default void onChildAdded(PReadOnlyLayout layout, PComponentLayoutData data) {}
	
	/**
	 * <p>This method is called after a {@link PComponent} was removed from a {@link PReadOnlyLayout layout}.
	 * The first argument passed to this method is the layout which the component was removed from. The 
	 * second argument is a {@link PComponentLayoutData data object} which contains the component that was 
	 * removed, as well as the constraint to which the component was previously bound.</p>
	 * 
	 * <p>This method should only be called from within layout to notify its observers of a change.</p>
	 * 
	 * @param layout		the layout that the component was removed from. This is never null.
	 * @param data			the previous data for the component. This is never null.
	 * @see PComponentLayoutData#getComponent()
	 * @see PComponentLayoutData#getConstraint()
	 * @see PReadOnlyLayout#getDataFor(PComponent)
	 * @see PComponent#getLayoutData()
	 */
	public default void onChildRemoved(PReadOnlyLayout layout, PComponentLayoutData data) {}
	
	/**
	 * This event is fired when a {@link PComponent} was laid out by this {@link PReadOnlyLayout}.<br>
	 * 
	 * @param layout		the layout that laid out the component. This is never null.
	 * @param child			the component that was laid out. This is never null.
	 * @param data			the layout data of the laid out component. This is never null.
	 */
	/**
	 * <p>This method is called after a {@link PComponent} was laid out by a {@link PReadOnlyLayout layout}.
	 * The first argument passed to this method is the layout of the parent of the component. The 
	 * second argument is the {@link PComponentLayoutData data object} which contains the component that was 
	 * laid out, as well as the constraint to which the component is currently bound.</p>
	 * 
	 * <p>This method should only be called from within layout to notify its observers of a change.</p>
	 * 
	 * @param layout		the layout which laid out the component. This is never null.
	 * @param data			the current data for the component. This is never null.
	 * @see PComponentLayoutData#getComponent()
	 * @see PComponentLayoutData#getConstraint()
	 * @see PReadOnlyLayout#getDataFor(PComponent)
	 * @see PComponent#getLayoutData()
	 */
	public default void onChildLaidOut(PReadOnlyLayout layout, PComponentLayoutData data) {}
	
	/**
	 * This event is fired when the {@link PReadOnlyLayout} has been invalidated and needs to 
	 * be laid out again.<br>
	 * This might happen, for example, when the layouts internal configuration has 
	 * changed.<br>
	 * 
	 * @param layout the layout
	 */
	public default void onLayoutInvalidated(PReadOnlyLayout layout) {}
	
}