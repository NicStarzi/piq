package edu.udo.piq;

/**
 * An observer for {@link PLayout}s.<br>
 * These observers can be used to get a notification when 
 * a {@link PComponent} is added, removed or laid out by
 * a {@link PLayout}.<br>
 * 
 * @author Nic Starzi
 */
public interface PLayoutObs {
	
	/**
	 * This even is fired when a {@link PComponent} was added to a {@link PLayout}.<br>
	 * 
	 * @param layout the layout that the component was added to
	 * @param child the component that was added
	 * @param constraint the constraint the component was added with
	 */
	public default void childAdded(PLayout layout, PComponent child, Object constraint) {}
	
	/**
	 * This even is fired when a {@link PComponent} was removed from a 
	 * {@link PLayout}.<br>
	 * 
	 * @param layout the layout that the component was removed from
	 * @param child the component that was removed
	 */
	public default void childRemoved(PLayout layout, PComponent child, Object constraint) {}
	
	/**
	 * This event is fired when a {@link PComponent} was laid out by this {@link PLayout}.<br>
	 * 
	 * @param layout the layout that laid out the component
	 * @param child the component that was laid out
	 */
	public default void childLaidOut(PLayout layout, PComponent child, Object constraint) {}
	
	/**
	 * This event is fired when the {@link PLayout} has been invalidated and needs to 
	 * be laid out again.<br>
	 * This might happen, for example, when the layouts internal configuration has 
	 * changed.<br>
	 * 
	 * @param layout the layout
	 */
	public default void layoutInvalidated(PLayout layout) {}
	
}