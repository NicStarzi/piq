package edu.udo.piq;

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
	 * Sets the owning {@link PComponent} of the {@link PMouse}.<br>
	 * A mouse can only have one owner at a time. If the owner is set 
	 * any previous owner will be forgotten.<br>
	 * The owner of the mouse can be set to null in which case the 
	 * mouse will not be owned by any component.<br>
	 * If a component registers itself as the mouse owner it should 
	 * reset the owner to null after it is done using the mouse.<br>
	 * 
	 * @param component the new owner of this mouse
	 * @see #getOwner()
	 */
	public void setOwner(PComponent component);
	
	/**
	 * Returns the current owner of the mouse or null if the mouse 
	 * is not being owned by a component at the moment.<br>
	 * If a component takes ownership of the mouse other components 
	 * <i>should</i> respect that and not use the mouse at the same time.<br>
	 * 
	 * @return the current owner of the mouse
	 * @see #setOwner(PComponent)
	 */
	public PComponent getOwner();
	
	/**
	 * An enumeration of different mouse buttons which can be used by the GUI.<br>
	 * The available buttons are LEFT, RIGHT, MIDDLE and OTHER where OTHER is 
	 * any additional button the mouse might have.<br>
	 * A mouse does not need to actually use all or any of these buttons.<br>
	 * If a mouse does not support one of these buttons no exception will be 
	 * thrown, instead the method will simply return false silently.<br>
	 */
	public static enum MouseButton {
		LEFT,
		RIGHT,
		MIDDLE,
		OTHER,
		;
	}
	
}