package edu.udo.piq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PKeyboard {
	
	/**
	 * Returns true if the given {@link ActualKey} is being pressed
	 * down at this update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key is being pressed right now
	 * @throws NullPointerException if key is null
	 */
	public boolean isPressed(ActualKey key);
	
	/**
	 * Returns true if the given {@link ActualKey} has just been
	 * released in the last update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key has been released just now
	 * @throws NullPointerException if key is null
	 */
	public boolean isReleased(ActualKey key);
	
	/**
	 * Returns true if the given {@link ActualKey} has just been
	 * triggered in the last update cycle.<br>
	 * 
	 * @param key the key
	 * @return whether the key has been triggered just now
	 * @throws NullPointerException if key is null
	 */
	public boolean isTriggered(ActualKey key);
	
	public default boolean isEqualKey(ActualKey actualKey, Key unknownKey) {
		if (unknownKey instanceof ActualKey) {
			return actualKey == unknownKey;
		}
		if (!(unknownKey instanceof VirtualKey)) {
			return false;
		}
		switch ((VirtualKey) unknownKey) {
		case COPY:
			return actualKey == ActualKey.C && isModifierToggled(Modifier.CTRL);
		case CUT:
			return actualKey == ActualKey.X && isModifierToggled(Modifier.CTRL);
		case FOCUS_UP:
			return actualKey == ActualKey.TAB && isModifierToggled(Modifier.CTRL);
		case FOCUS_DOWN:
			return actualKey == ActualKey.TAB && isModifierToggled(Modifier.SHIFT)
					&& isModifierToggled(Modifier.CTRL);
		case FOCUS_NEXT:
			return actualKey == ActualKey.TAB;
		case FOCUS_PREV:
			return actualKey == ActualKey.TAB && isModifierToggled(Modifier.SHIFT);
		case PASTE:
			return actualKey == ActualKey.V && isModifierToggled(Modifier.CTRL);
		case REDO:
			return actualKey == ActualKey.Y && isModifierToggled(Modifier.CTRL);
		case UNDO:
			return actualKey == ActualKey.Z && isModifierToggled(Modifier.CTRL);
		case COMMAND:
			return actualKey == ActualKey.CTRL || isModifierToggled(Modifier.CTRL);
		default:
			return false;
		}
	}
	
	public default boolean isPressed(Key key) {
		if (!(key instanceof VirtualKey)) {
			return false;
		}
		switch ((VirtualKey) key) {
		case COPY:
			return isPressed(ActualKey.C) && isModifierToggled(Modifier.CTRL);
		case CUT:
			return isPressed(ActualKey.X) && isModifierToggled(Modifier.CTRL);
		case FOCUS_UP:
			return isPressed(ActualKey.TAB) && isModifierToggled(Modifier.CTRL);
		case FOCUS_DOWN:
			return isPressed(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT)
					&& isModifierToggled(Modifier.CTRL);
		case FOCUS_NEXT:
			return isPressed(ActualKey.TAB);
		case FOCUS_PREV:
			return isPressed(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT);
		case PASTE:
			return isPressed(ActualKey.V) && isModifierToggled(Modifier.CTRL);
		case REDO:
			return isPressed(ActualKey.Y) && isModifierToggled(Modifier.CTRL);
		case UNDO:
			return isPressed(ActualKey.Z) && isModifierToggled(Modifier.CTRL);
		case COMMAND:
			return isPressed(ActualKey.CTRL) || isModifierToggled(Modifier.CTRL);
		default:
			return false;
		}
	}
	
	public default boolean isReleased(Key key) {
		if (!(key instanceof VirtualKey)) {
			return false;
		}
		switch ((VirtualKey) key) {
		case COPY:
			return isReleased(ActualKey.C) && isModifierToggled(Modifier.CTRL);
		case CUT:
			return isReleased(ActualKey.X) && isModifierToggled(Modifier.CTRL);
		case FOCUS_UP:
			return isReleased(ActualKey.TAB) && isModifierToggled(Modifier.CTRL);
		case FOCUS_DOWN:
			return isReleased(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT)
					&& isModifierToggled(Modifier.CTRL);
		case FOCUS_NEXT:
			return isReleased(ActualKey.TAB);
		case FOCUS_PREV:
			return isReleased(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT);
		case PASTE:
			return isReleased(ActualKey.V) && isModifierToggled(Modifier.CTRL);
		case REDO:
			return isReleased(ActualKey.Y) && isModifierToggled(Modifier.CTRL);
		case UNDO:
			return isReleased(ActualKey.Z) && isModifierToggled(Modifier.CTRL);
		case COMMAND:
			return isReleased(ActualKey.CTRL);
		default:
			return false;
		}
	}
	
	public default boolean isTriggered(Key key) {
		if (!(key instanceof VirtualKey)) {
			return false;
		}
		switch ((VirtualKey) key) {
		case FOCUS_UP:
			return isTriggered(ActualKey.TAB) && !isModifierToggled(Modifier.SHIFT)
					&& isModifierToggled(Modifier.CTRL);
		case FOCUS_DOWN:
			return isTriggered(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT)
					&& isModifierToggled(Modifier.CTRL);
		case FOCUS_NEXT:
			return isTriggered(ActualKey.TAB) && !isModifierToggled(Modifier.SHIFT);
		case FOCUS_PREV:
			return isTriggered(ActualKey.TAB) && isModifierToggled(Modifier.SHIFT);
		case COPY:
			return isTriggered(ActualKey.C) && isModifierToggled(Modifier.CTRL);
		case CUT:
			return isTriggered(ActualKey.X) && isModifierToggled(Modifier.CTRL);
		case PASTE:
			return isTriggered(ActualKey.V) && isModifierToggled(Modifier.CTRL);
		case REDO:
			return isTriggered(ActualKey.Y) && isModifierToggled(Modifier.CTRL);
		case UNDO:
			return isTriggered(ActualKey.Z) && isModifierToggled(Modifier.CTRL);
		case COMMAND:
			return isTriggered(ActualKey.CTRL);
		default:
			return false;
		}
	}
	
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
	
	public static interface Key {
		public String getName();
	}
	/**
	 * An enumeration of all Keyboard keys that are supported by
	 * a {@link PKeyboard} implementation.<br>
	 * A particular keyboard implementation does not need to support
	 * all of these keys. If a key is not supported by a
	 * {@link PKeyboard} implementation its methods will simply ignore
	 * this key.
	 */
	public static enum ActualKey implements Key {
		A, B, C, D, E, F, G, H, I,
		J, K, L, M, N, O, P, Q, R,
		S, T, U, V, W, X, Y, Z,
		
		NUM_0, NUM_1, NUM_2, NUM_3, NUM_4,
		NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,
		
		UP, DOWN, LEFT, RIGHT,
		
		SHIFT, TAB, CTRL, SPACE, ENTER, BACKSPACE,
		DELETE, HOME, PAGE_UP, PAGE_DOWN, ALT_GRAPH,
		ESCAPE, CAPSLOCK, ALT, END,
		;
		public static final List<PKeyboard.ActualKey> ALL =
				Collections.unmodifiableList(Arrays.asList(ActualKey.values()));
		public static final int COUNT = ALL.size();
		
		public final int ID = ordinal();
		
		public String getName() {
			return name();
		}
	}
	public static enum VirtualKey implements Key {
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
		 * This key is used for the COPY-shortcuts. By default this is COMMAND + C.<br>
		 */
		COPY,
		/**
		 * This key is used for the CUT-shortcuts. By default this is COMMAND + X.<br>
		 */
		CUT,
		/**
		 * This key is used for the PASTE-shortcuts. By default this is COMMAND + V.<br>
		 */
		PASTE,
		/**
		 * This key is used for the UNDO-shortcuts. By default this is COMMAND + Z.<br>
		 */
		UNDO,
		/**
		 * This key is used for the REDO-shortcuts. By default this is COMMAND + Y.<br>
		 */
		REDO,
		/**
		 * This key is an alias for the key used in most key combinations like copy, cut and paste.
		 * On Windows this would be Ctrl. On MacOS this would be Command.
		 */
		COMMAND,
		;
		public static final List<PKeyboard.VirtualKey> ALL
			= Collections.unmodifiableList(Arrays.asList(PKeyboard.VirtualKey.values()));
		public static final int COUNT = ALL.size();
		
		public final int ID = ordinal();
		
		public String getName() {
			return name();
		}
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
		COMMAND,
		;
		public static final List<PKeyboard.Modifier> ALL =
				Collections.unmodifiableList(Arrays.asList(Modifier.values()));
		public static final int COUNT = ALL.size();
		
		public final int ID = ordinal();
		
		public static final Modifier SHIFT = CAPS;
	}
	
}