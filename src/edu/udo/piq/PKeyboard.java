package edu.udo.piq;

public interface PKeyboard {
	
	/**
	 * Returns true if the given {@link Key} is being pressed 
	 * down at this update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key is being pressed right now
	 * @throws NullPointerException if key is null
	 */
	public boolean isPressed(Key key);
	
	/**
	 * Returns true if the given {@link Key} has just been 
	 * released in the last update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key has been released just now
	 * @throws NullPointerException if key is null
	 */
	public boolean isReleased(Key key);
	
	/**
	 * Returns true if the given {@link Key} has just been 
	 * triggered in the last update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key has been triggered just now
	 * @throws NullPointerException if key is null
	 */
	public boolean isTriggered(Key key);
	
	public void addObs(PKeyboardObs obs);
	
	public void removeObs(PKeyboardObs obs);
	
	/**
	 * An enumeration of all Keyboard keys that are supported by 
	 * a {@link PKeyboard} implementation.<br>
	 * A particular keyboard implementation does not need to support 
	 * all of these keys. If a key is not supported a method will 
	 * simply ignore this key.
	 */
	public static enum Key {
		A, B, C, D, E, F, G, H, I, 
		J, K, L, M, N, O, P, Q, R, 
		S, T, U, V, W, X, Y, Z,
		
		NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, 
		NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,
		
		UP, DOWN, LEFT, RIGHT,
		
		SHIFT, TAB, CTRL, SPACE, ENTER, BACKSPACE, 
		DEL, HOME, PAGE_UP, PAGE_DOWN, ALT_GR, 
		ESC, CAPSLOCK, ALT, END, 
		;
	}
	
}