package edu.udo.piq;

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
	 * This even is fired when a {@link PComponent} was added to a {@link PReadOnlyLayout}.<br>
	 * 
	 * @param layout the layout that the component was added to
	 * @param child the component that was added
	 * @param constraint the constraint the component was added with
	 */
	public default void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {}
	
	/**
	 * This even is fired when a {@link PComponent} was removed from a 
	 * {@link PReadOnlyLayout}.<br>
	 * 
	 * @param layout		the layout that the component was removed from
	 * @param child			the component that was removed
	 * @param constraint	the constraint used for the removed component
	 */
	public default void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {}
	
	/**
	 * This event is fired when a {@link PComponent} was laid out by this {@link PReadOnlyLayout}.<br>
	 * 
	 * @param layout		the layout that laid out the component
	 * @param child			the component that was laid out
	 * @param constraint	the constraint of the laid out component
	 */
	public default void onChildLaidOut(PReadOnlyLayout layout, PComponent child, Object constraint) {}
	
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