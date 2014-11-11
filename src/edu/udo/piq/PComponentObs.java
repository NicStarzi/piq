package edu.udo.piq;

/**
 * An observer for {@link PComponent}s.<br>
 * These observers can be used to get a notification when 
 * a {@link PComponent} changes its preferred size or is 
 * added to or removed from a {@link PLayout}.<br>
 * 
 * @author Nic Starzi
 */
public interface PComponentObs {
	
	/**
	 * This event is fired when a {@link PComponent} has changed its preferred size.<br>
	 * 
	 * @param component the component that changed its preferred size
	 */
	public void preferredSizeChanged(PComponent component);
	
	/**
	 * This event is fired by a {@link PComponent} when the component 
	 * was added to a {@link PLayout}.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public void wasAdded(PComponent component);
	
	/**
	 * This event is fired by a {@link PComponent} when the component 
	 * was removed from a {@link PLayout}.<br>
	 * 
	 * @param component the component that fired the event
	 */
	public void wasRemoved(PComponent component);
	
}