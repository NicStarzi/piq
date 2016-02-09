package edu.udo.piq;

import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.popup.PPopup;
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
	
	public int getClickCount();
	
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
	
//	/**
//	 * Sets the cursor graphic that is displayed for the mouse.<br>
//	 * If the cursor graphic can not be displayed because the implementation 
//	 * does not allow it nothing will happen.<br>
//	 * 
//	 * @param cursor a non-null instance of {@link PCursor}
//	 * @throws IllegalArgumentException if cursor is null
////	 * @see #setCursor(PCursorType)
//	 * @see #fetchCustomCursor(PImageResource, int, int)
//	 */
//	public void setCursor(PCursor cursor) throws IllegalArgumentException;
	
	public PCursor getCursorDefault();
	
	public PCursor getCursorHand();
	
	public PCursor getCursorText();
	
	public PCursor getCursorScroll();
	
	public PCursor getCursorBusy();
	
//	/**
//	 * Sets the cursor graphic that is displayed for the mouse. This method will 
//	 * use a default implementation for the given {@link PCursorType} if available. 
//	 * If a default implementation for the type does not exist or if the 
//	 * implementation does not allow changing cursors nothing will happen.<br>
//	 * 
//	 * @param cursorType the type of the displayed cursor
//	 * @throws IllegalArgumentException if cursorType is null or {@link PCursorType#CUSTOM}
//	 * @see #setCursor(PCursor)
//	 * @see #getCustomCursor(PImageResource, int, int)
//	 */
//	public void setCursor(PCursorType cursorType) throws IllegalArgumentException;
	
	/**
	 * Returns the currently used {@link PCursor}.<br>
	 * This method never returns null, even if cursors are not supported by the 
	 * implementation.<br>
	 * 
	 * @return the {@link PCursor} that is currently being used
	 * @see PComponent#getMouseOverCursor(PMouse)
//	 * @see #setCursor(PCursor)
//	 * @see #setCursor(PCursorType)
	 */
	public PCursor getCurrentCursor();
	
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
		/**
		 * This mouse button is used for dragging data from one 
		 * {@link PSelectionComponent} to another via a 
		 * drag-and-drop motion.<br>
		 * This is a virtual button which should be identical to a 
		 * non virtual button.<br>
		 */
		DRAG_AND_DROP,
		/**
		 * This mouse button is used to trigger {@link PPopup popups} 
		 * on top of components.<br>
		 * This is a virtual button which should be identical to a 
		 * non virtual button.<br> 
		 */
		POPUP_TRIGGER,
		;
	}
	
}