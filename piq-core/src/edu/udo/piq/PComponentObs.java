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
	 * @param component the component that fired the event
	 * @param currentRoot a PRoot or null
	 */
	public default void rootChanged(PComponent component, PRoot currentRoot) {}
	
	/**
	 * This event is fired when a {@link PComponent} has changed its preferred size.<br>
	 * 
	 * @param component the component that changed its preferred size
	 */
	public default void preferredSizeChanged(PComponent component) {}
	
	/**
	 * This event is fired by a {@link PComponent} when the component 
	 * was added to a {@link PReadOnlyLayout}.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public default void wasAdded(PComponent component) {}
	
	/**
	 * This event is fired by a {@link PComponent} when the component 
	 * was removed from a {@link PReadOnlyLayout}.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public default void wasRemoved(PComponent component) {}
	
}