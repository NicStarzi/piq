package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.Character.UnicodeBlock;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.tools.AbstractPKeyboard;

public class SwingPKeyboard extends AbstractPKeyboard implements PKeyboard {
	
	protected final boolean[] nowPressed = new boolean[Key.COUNT];
	protected final boolean[] prevPressed = new boolean[nowPressed.length];
	protected final boolean[] modState = new boolean[Modifier.COUNT];
	protected boolean capsLockDown;
	
	public SwingPKeyboard(Component base) {
		base.setFocusTraversalKeysEnabled(false);
		base.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (isTypeable(c)) {
					String typedString = String.valueOf(new char[] { c });
					fireStringTypedEvent(typedString);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				updateKey(e, false);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				updateKey(e, true);
			}
		});
	}
	
	protected boolean isTypeable(char c) {
		if (c == KeyEvent.CHAR_UNDEFINED) {
			return false;
		}
		if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
			return false;
		}
		UnicodeBlock block = UnicodeBlock.of(c);
		return block != null && block != UnicodeBlock.SPECIALS;
	}
	
	protected void updateKey(KeyEvent e, boolean newPressedValue) {
		setModifierState(Modifier.ALT, e.isAltDown());
		setModifierState(Modifier.ALT_GRAPH, e.isAltGraphDown());
		setModifierState(Modifier.CTRL, e.isControlDown());
		setModifierState(Modifier.META, e.isMetaDown());
		
		int keyCode = e.getKeyCode();
		Key key = SwingPKeyboard.keyCodeToKey(keyCode);
		if (key == Key.CAPSLOCK && newPressedValue) {
			capsLockDown = !capsLockDown;
		}
		setModifierState(Modifier.CAPS, e.isShiftDown() || capsLockDown);
		if (key != null) {
			int index = key.ID;
			prevPressed[index] = nowPressed[index];
			nowPressed[index] = newPressedValue;
			if (isTriggered(key)) {
				fireTriggerEvent(key);
			} else if (isReleased(key)) {
				fireReleaseEvent(key);
			}
			if (newPressedValue) {
				firePressEvent(key);
			}
		}
	}
	
	protected void setModifierState(Modifier mod, boolean newState) {
		boolean oldState = modState[mod.ID];
		if (oldState != newState) {
			modState[mod.ID] = newState;
			fireModifierToggledEvent(mod);
		}
	}
	
	@Override
	public boolean isPressed(Key key) {
		return nowPressed[key.ID];
	}
	
	@Override
	public boolean isTriggered(Key key) {
		return nowPressed[key.ID] && !prevPressed[key.ID];
	}
	
	@Override
	public boolean isReleased(Key key) {
		return !nowPressed[key.ID] && prevPressed[key.ID];
	}
	
	@Override
	public boolean isModifierToggled(Modifier modifier) {
		return modState[modifier.ID];
	}
	
	public static Key keyCodeToKey(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_0:
			return Key.NUM_0;
		case KeyEvent.VK_1:
			return Key.NUM_1;
		case KeyEvent.VK_2:
			return Key.NUM_2;
		case KeyEvent.VK_3:
			return Key.NUM_3;
		case KeyEvent.VK_4:
			return Key.NUM_4;
		case KeyEvent.VK_5:
			return Key.NUM_5;
		case KeyEvent.VK_6:
			return Key.NUM_6;
		case KeyEvent.VK_7:
			return Key.NUM_7;
		case KeyEvent.VK_8:
			return Key.NUM_8;
		case KeyEvent.VK_9:
			return Key.NUM_9;
		case KeyEvent.VK_A:
			return Key.A;
		case KeyEvent.VK_B:
			return Key.B;
		case KeyEvent.VK_C:
			return Key.C;
		case KeyEvent.VK_D:
			return Key.D;
		case KeyEvent.VK_E:
			return Key.E;
		case KeyEvent.VK_F:
			return Key.F;
		case KeyEvent.VK_G:
			return Key.G;
		case KeyEvent.VK_H:
			return Key.H;
		case KeyEvent.VK_I:
			return Key.I;
		case KeyEvent.VK_J:
			return Key.J;
		case KeyEvent.VK_K:
			return Key.K;
		case KeyEvent.VK_L:
			return Key.L;
		case KeyEvent.VK_M:
			return Key.M;
		case KeyEvent.VK_N:
			return Key.N;
		case KeyEvent.VK_O:
			return Key.O;
		case KeyEvent.VK_P:
			return Key.P;
		case KeyEvent.VK_Q:
			return Key.Q;
		case KeyEvent.VK_R:
			return Key.R;
		case KeyEvent.VK_S:
			return Key.S;
		case KeyEvent.VK_T:
			return Key.T;
		case KeyEvent.VK_U:
			return Key.U;
		case KeyEvent.VK_V:
			return Key.V;
		case KeyEvent.VK_W:
			return Key.W;
		case KeyEvent.VK_X:
			return Key.X;
		case KeyEvent.VK_Y:
			return Key.Y;
		case KeyEvent.VK_Z:
			return Key.Z;
		case KeyEvent.VK_UP:
			return Key.UP;
		case KeyEvent.VK_DOWN:
			return Key.DOWN;
		case KeyEvent.VK_LEFT:
			return Key.LEFT;
		case KeyEvent.VK_RIGHT:
			return Key.RIGHT;
		case KeyEvent.VK_CONTROL:
			return Key.CTRL;
		case KeyEvent.VK_ALT:
			return Key.ALT;
		case KeyEvent.VK_ALT_GRAPH:
			return Key.ALT_GR;
		case KeyEvent.VK_SHIFT:
			return Key.SHIFT;
		case KeyEvent.VK_SPACE:
			return Key.SPACE;
		case KeyEvent.VK_ENTER:
			return Key.ENTER;
		case KeyEvent.VK_ESCAPE:
			return Key.ESC;
		case KeyEvent.VK_DELETE:
			return Key.DEL;
		case KeyEvent.VK_BACK_SPACE:
			return Key.BACKSPACE;
		case KeyEvent.VK_CAPS_LOCK:
			return Key.CAPSLOCK;
		case KeyEvent.VK_TAB:
			return Key.TAB;
		case KeyEvent.VK_HOME:
			return Key.HOME;
		case KeyEvent.VK_PAGE_UP:
			return Key.PAGE_UP;
		case KeyEvent.VK_PAGE_DOWN:
			return Key.PAGE_DOWN;
		case KeyEvent.VK_END:
			return Key.END;
		default:
			return null;
		}
	}
	
}