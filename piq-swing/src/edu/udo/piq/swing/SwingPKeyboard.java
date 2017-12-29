package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.Character.UnicodeBlock;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.tools.AbstractPKeyboard;

public class SwingPKeyboard extends AbstractPKeyboard implements PKeyboard {
	
	protected final boolean[] nowPressed = new boolean[ActualKey.COUNT];
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
		setModifierState(Modifier.COMMAND, e.isControlDown());
		
		int keyCode = e.getKeyCode();
		ActualKey key = SwingPKeyboard.keyCodeToKey(keyCode);
		if (key == ActualKey.CAPSLOCK && newPressedValue) {
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
	public boolean isPressed(ActualKey key) {
		return nowPressed[key.ID];
	}
	
	@Override
	public boolean isTriggered(ActualKey key) {
		return nowPressed[key.ID] && !prevPressed[key.ID];
	}
	
	@Override
	public boolean isReleased(ActualKey key) {
		return !nowPressed[key.ID] && prevPressed[key.ID];
	}
	
	@Override
	public boolean isModifierToggled(Modifier modifier) {
		return modState[modifier.ID];
	}
	
	public static ActualKey keyCodeToKey(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_0:
			return ActualKey.NUM_0;
		case KeyEvent.VK_1:
			return ActualKey.NUM_1;
		case KeyEvent.VK_2:
			return ActualKey.NUM_2;
		case KeyEvent.VK_3:
			return ActualKey.NUM_3;
		case KeyEvent.VK_4:
			return ActualKey.NUM_4;
		case KeyEvent.VK_5:
			return ActualKey.NUM_5;
		case KeyEvent.VK_6:
			return ActualKey.NUM_6;
		case KeyEvent.VK_7:
			return ActualKey.NUM_7;
		case KeyEvent.VK_8:
			return ActualKey.NUM_8;
		case KeyEvent.VK_9:
			return ActualKey.NUM_9;
		case KeyEvent.VK_A:
			return ActualKey.A;
		case KeyEvent.VK_B:
			return ActualKey.B;
		case KeyEvent.VK_C:
			return ActualKey.C;
		case KeyEvent.VK_D:
			return ActualKey.D;
		case KeyEvent.VK_E:
			return ActualKey.E;
		case KeyEvent.VK_F:
			return ActualKey.F;
		case KeyEvent.VK_G:
			return ActualKey.G;
		case KeyEvent.VK_H:
			return ActualKey.H;
		case KeyEvent.VK_I:
			return ActualKey.I;
		case KeyEvent.VK_J:
			return ActualKey.J;
		case KeyEvent.VK_K:
			return ActualKey.K;
		case KeyEvent.VK_L:
			return ActualKey.L;
		case KeyEvent.VK_M:
			return ActualKey.M;
		case KeyEvent.VK_N:
			return ActualKey.N;
		case KeyEvent.VK_O:
			return ActualKey.O;
		case KeyEvent.VK_P:
			return ActualKey.P;
		case KeyEvent.VK_Q:
			return ActualKey.Q;
		case KeyEvent.VK_R:
			return ActualKey.R;
		case KeyEvent.VK_S:
			return ActualKey.S;
		case KeyEvent.VK_T:
			return ActualKey.T;
		case KeyEvent.VK_U:
			return ActualKey.U;
		case KeyEvent.VK_V:
			return ActualKey.V;
		case KeyEvent.VK_W:
			return ActualKey.W;
		case KeyEvent.VK_X:
			return ActualKey.X;
		case KeyEvent.VK_Y:
			return ActualKey.Y;
		case KeyEvent.VK_Z:
			return ActualKey.Z;
		case KeyEvent.VK_UP:
			return ActualKey.UP;
		case KeyEvent.VK_DOWN:
			return ActualKey.DOWN;
		case KeyEvent.VK_LEFT:
			return ActualKey.LEFT;
		case KeyEvent.VK_RIGHT:
			return ActualKey.RIGHT;
		case KeyEvent.VK_CONTROL:
			return ActualKey.CTRL;
		case KeyEvent.VK_ALT:
			return ActualKey.ALT;
		case KeyEvent.VK_ALT_GRAPH:
			return ActualKey.ALT_GRAPH;
		case KeyEvent.VK_SHIFT:
			return ActualKey.SHIFT;
		case KeyEvent.VK_SPACE:
			return ActualKey.SPACE;
		case KeyEvent.VK_ENTER:
			return ActualKey.ENTER;
		case KeyEvent.VK_ESCAPE:
			return ActualKey.ESCAPE;
		case KeyEvent.VK_DELETE:
			return ActualKey.DELETE;
		case KeyEvent.VK_BACK_SPACE:
			return ActualKey.BACKSPACE;
		case KeyEvent.VK_CAPS_LOCK:
			return ActualKey.CAPSLOCK;
		case KeyEvent.VK_TAB:
			return ActualKey.TAB;
		case KeyEvent.VK_HOME:
			return ActualKey.HOME;
		case KeyEvent.VK_PAGE_UP:
			return ActualKey.PAGE_UP;
		case KeyEvent.VK_PAGE_DOWN:
			return ActualKey.PAGE_DOWN;
		case KeyEvent.VK_END:
			return ActualKey.END;
		default:
			return null;
		}
	}
	
}