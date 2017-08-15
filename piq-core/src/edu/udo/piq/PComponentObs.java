package edu.udo.piq;

/**
 * An observer for {@link PComponent}s.<br>
 * These observers can be used to get a notification when
 * a {@link PComponent} changes its preferred size or is
 * added to or removed from a {@link PReadOnlyLayout}.<br>
 * 
 * @author Nic Starzi
 */
public interface PComponentObs {
	
	/**
	 * This event is fired when a {@link PComponent} has changed its {@link PRoot}.<br>
	 * If the currentRoot is null the component is no longer a part of a GUI.<br>
	 * 
	 * @param component			the component that fired the event. This is never null.
	 * @param currentRoot		the current root of this component. Might be null.
	 * @param oldRoot			the former root of this component. Might be null.
	 */
	public default void onRootChanged(PComponent component, PRoot currentRoot, PRoot oldRoot) {}
	
	/**
	 * This event is fired when a {@link PComponent} has changed its preferred size.<br>
	 * 
	 * @param component the component that changed its preferred size
	 */
	public default void onPreferredSizeChanged(PComponent component) {}
	
	/**
	 * This event is fired when the {@link PBounds} of a {@link PComponent} have changed.<br>
	 * The bounds of a component change when the component is laid out by its parents
	 * {@link PLayout} or when the component is added to or removed from a {@link PLayout}.<br>
	 * The bounds of a component may be null.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public default void onBoundsChanged(PComponent component) {}
	
	/**
	 * <p>This event is fired when a component requests itself to become visible at the
	 * given offset coordinates. This event should be processed by any scroll component
	 * in the ancestor hierarchy of the event component.</p>
	 * @param component		the component which requested the scrolling
	 * @param offsetX		an offset (in pixels) to the coordinates which should be made visible
	 * @param offsetY		an offset (in pixels) to the coordinates which should be made visible
	 */
	public default void onScrollRequest(PComponent component, int offsetX, int offsetY) {}
	
	/**
	 * This event is fired by a {@link PComponent} when the component
	 * was added to a {@link PReadOnlyLayout layout}.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public default void onAdd(PComponent component) {}
	
	/**
	 * This event is fired by a {@link PComponent} when the component
	 * was removed from a {@link PReadOnlyLayout layout}.<br>
	 * @param oldParent the component from which a component was removed
	 * @param component the component that fired the event
	 */
	public default void onRemove(PComponent oldParent, PComponent component) {}
	
}