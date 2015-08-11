package edu.udo.piq;

import edu.udo.piq.components.util.PFocusTraversal;

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
	
	/**
	 * Returns true if the given modifier is currently toggled.<br>
	 * 
	 * @param modifier
	 * @return true if the modifier is active
	 * @throws NullPointerException
	 * @see Modifier
	 */
	public boolean isModifierToggled(Modifier modifier);
	
	public void addObs(PKeyboardObs obs);
	
	public void removeObs(PKeyboardObs obs);
	
	/**
	 * An enumeration of all Keyboard keys that are supported by 
	 * a {@link PKeyboard} implementation.<br>
	 * A particular keyboard implementation does not need to support 
	 * all of these keys. If a key is not supported by a 
	 * {@link PKeyboard} implementation its methods will simply ignore 
	 * this key.
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
		
		/**
		 * When this key is triggered the next component in the 
		 * current {@link PFocusTraversal} will become focused.<br>
		 */
		FOCUS_NEXT,
		/**
		 * When this key is triggered the previous component in the 
		 * current {@link PFocusTraversal} will become focused.<br>
		 */
		FOCUS_PREV, 
		/**
		 * When this key is triggered the current {@link PFocusTraversal} 
		 * will go up by one level if possible.<br>
		 */
		FOCUS_UP,
		/**
		 * When this key is triggered the current {@link PFocusTraversal} 
		 * will go down by one level if possible.<br>
		 */
		FOCUS_DOWN, 
		/**
		 * This key is used for the COPY-shortcuts. On windows this is Ctrl + C.<br>
		 */
		COPY, 
		/**
		 * This key is used for the CUT-shortcuts. On windows this is Ctrl + X.<br>
		 */
		CUT, 
		/**
		 * This key is used for the PASTE-shortcuts. On windows this is Ctrl + V.<br>
		 */
		PASTE, 
		/**
		 * This key is used for the UNDO-shortcuts. On windows this is Ctrl + Z.<br>
		 */
		UNDO, 
		/**
		 * This key is used for the REDO-shortcuts. On windows this is Ctrl + Y.<br>
		 */
		REDO, 
		;
	}
	
	/**
	 * An enumeration of all modifier keys that can be supported by a
	 * {@link PKeyboard}. An implementation is not required to support 
	 * all of these modifier keys. If a modifier is not supported it 
	 * will be reported as never being toggled.<br>
	 */
	public static enum Modifier {
		CAPS,
		ALT,
		ALT_GRAPH,
		CTRL,
		META,
		;
	}
	
}