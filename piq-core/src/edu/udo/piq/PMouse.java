package edu.udo.piq;

import edu.udo.piq.util.PCompUtil;

public interface PMouse {
	
	/**
	 * Returns the x-coordinate of the mouse pointer (or similar 
	 * pointing device) on screen.<br>
	 * 
	 * @return x-coordinate on screen
	 */
	public int getX();
	
	/**
	 * Returns the y-coordinate of the mouse pointer (or similar 
	 * pointing device) on screen.<br>
	 * 
	 * @return y-coordinate on screen
	 */
	public int getY();
	
	/**
	 * Returns the distance the mouse pointer has traveled on the 
	 * x-axis since the last update cycle of the GUI.<br>
	 * 
	 * @return distance traveled on x-axis
	 */
	public int getDeltaX();
	
	/**
	 * Returns the distance the mouse pointer has traveled on the 
	 * y-axis since the last update cycle of the GUI.<br>
	 * 
	 * @return distance traveled on y-axis
	 */
	public int getDeltaY();
	
	/**
	 * Returns true if the given {@link MouseButton} is being pressed 
	 * down at this update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button is being pressed right now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isPressed(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns true if the given {@link MouseButton} has just been 
	 * released in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been released just now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isReleased(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns true if the given {@link MouseButton} has just been 
	 * triggered in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been triggered just now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isTriggered(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns the topmost component that is directly underneath the mouse.<br>
	 * This component may be cached by the mouse to improve performance.<br>
	 * The returned component may be null if the mouse is outside of the 
	 * {@link PRoot PRoots} bounds.<br>
	 * 
	 * @return the {@link PComponent} that is positioned at this {@link PMouse PMouses} x- and y-coordinates or null
	 * @see PComponent#getClippedBounds()
	 * @see PCompUtil#getComponentAt(PComponent, int, int)
	 * @see PLayout#getChildAt(int, int)
	 */
	public PComponent getComponentAtMouse();
	
	public void addObs(PMouseObs obs);
	
	public void removeObs(PMouseObs obs);
	
	/**
	 * An enumeration of different mouse buttons which can be used by the GUI.<br>
	 * The available buttons are LEFT, RIGHT and MIDDLE. The left mouse button 
	 * is the primary button used by most all components. The right mouse button 
	 * is the secondary mouse button used mostly by pop-ups and context menus.<br>
	 * A mouse does not need to actually use all or any of these buttons.<br>
	 * If a mouse does not support one of these buttons no exception will be 
	 * thrown, instead the method using them will simply return false.<br>
	 */
	public static enum MouseButton {
		/**
		 * The primary mouse button.<br>
		 * Most functionality is using this button.<br>
		 */
		LEFT,
		/**
		 * The secondary mouse button.<br>
		 * This button is mostly used for pop-ups and context menus.<br>
		 */
		RIGHT,
		/**
		 * Standard components do not use this button.<br>
		 */
		MIDDLE,
		;
	}
	
}