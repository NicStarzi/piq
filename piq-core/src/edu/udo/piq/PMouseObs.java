package edu.udo.piq;

import edu.udo.piq.PMouse.MouseButton;

/**
 * A mouse observer can be used to get mouse input like button
 * presses or movement. Mouse observers can either be added to
 * a {@link PMouse} directly or to a {@link PComponent}.<br>
 * If the mouse observer is registered at a component then it
 * will always be registered for the {@link PMouse} that belongs
 * to the root of the component. If the component has no root
 * (because it is not part of a GUI) then there will be no
 * mouse input to observe.<br>
 * 
 * @author NicStarzi
 */
public interface PMouseObs {
	
	/**
	 * This method is being called every time the mouse has moved.<br>
	 * The implementation decides whether this event is only fired while
	 * the application has focus and/or the mouse is over the
	 * applications window.<br>
	 * @param mouse		the mouse that has moved, this is never null
	 */
	public default void onMouseMoved(PMouse mouse) {}
	
	/**
	 * This method is being called every time a mouse button has been
	 * pressed.<br>
	 * The implementation decides whether this event is only fired while
	 * the application has focus and/or the mouse is over the
	 * applications window.<br>
	 * @param mouse			the mouse which had its button pressed, this is never null
	 * @param btn			the button that was pressed, this is never null
	 * @param clickCount	the number of times the button was clicked
	 */
	public default void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {}
	
	/**
	 * This method is being called every time a mouse button has been
	 * triggered. A mouse button is triggered when it is first pressed
	 * and then released without the mouse having been moved too far
	 * away.<br>
	 * The implementation decides whether this event is only fired while
	 * the application has focus and/or the mouse is over the
	 * applications window.<br>
	 * @param mouse			the mouse which had its button triggered, this is never null
	 * @param btn			the button that was triggered, this is never null
	 */
	public default void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {}
	
	/**
	 * This method is being called every time a mouse button has been
	 * released.<br>
	 * The implementation decides whether this event is only fired while
	 * the application has focus and/or the mouse is over the
	 * applications window.<br>
	 * @param mouse			the mouse which had its button released, this is never null
	 * @param btn			the button that was released, this is never null
	 * @param clickCount	the number of times the button was clicked
	 */
	public default void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {}
	
}