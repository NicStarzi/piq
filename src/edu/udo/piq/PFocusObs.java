package edu.udo.piq;

/**
 * An observer for {@link PComponent}s and {@link PRoot}s.<br>
 * These observers can be used to get a notification when 
 * a {@link PComponent} gains or loses focus within a GUI.<br>
 * When registered at a component the focus observer will 
 * only be notified if the component it is registered at 
 * gains or loses focus.<br>
 * When registered at a root the focus observer will be 
 * notified every time any component gains or loses focus.<br>
 * 
 * @author Nic Starzi
 */
public interface PFocusObs {
	
	/**
	 * This event is fired when a {@link PComponent} gains focus.<br>
	 * The new focus owner is never null.<br>
	 * 
	 * @param oldOwner the component that lost focus or null
	 * @param newOwner the component that gained focus
	 */
	public default void focusGained(PComponent oldOwner, PComponent newOwner) {}
	
	/**
	 * This event is fired when a {@link PComponent} lost its focus.<br>
	 * The old focus owner is never null.<br>
	 * 
	 * @param oldOwner the component that lost focus
	 */
	public default void focusLost(PComponent oldOwner) {}
	
}