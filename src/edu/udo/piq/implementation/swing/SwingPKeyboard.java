package edu.udo.piq.implementation.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;

public class SwingPKeyboard implements PKeyboard {
	
	private final List<PKeyboardObs> obsList = new CopyOnWriteArrayList<>();
	private final boolean[] nowPressed = new boolean[Key.values().length];
	private final boolean[] prevPressed = new boolean[Key.values().length];
	
	public SwingPKeyboard(JComponent base) {
		base.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				updateKey(e, false);
			}
			public void keyPressed(KeyEvent e) {
				updateKey(e, true);
			}
		});
	}
	
	private void updateKey(KeyEvent e, boolean newPressedValue) {
		int keyCode = e.getKeyCode();
		Key key = keyCodeToKey(keyCode);
		if (key != null) {
			int index = key.ordinal();
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
	
	protected void update() {
		
	}
	
	public boolean isPressed(Key key) {
		return nowPressed[key.ordinal()];
	}
	
	public boolean isTriggered(Key key) {
		return nowPressed[key.ordinal()] && !prevPressed[key.ordinal()];
	}
	
	public boolean isReleased(Key key) {
		return !nowPressed[key.ordinal()] && prevPressed[key.ordinal()];
	}
	
	public void addObs(PKeyboardObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PKeyboardObs obs) {
		obsList.remove(obs);
	}
	
	protected void firePressEvent(Key key) {
		for (PKeyboardObs obs : obsList) {
			obs.keyPressed(this, key);
		}
	}
	
	protected void fireTriggerEvent(Key key) {
		for (PKeyboardObs obs : obsList) {
			obs.keyTriggered(this, key);
		}
	}
	
	protected void fireReleaseEvent(Key key) {
		for (PKeyboardObs obs : obsList) {
			obs.keyReleased(this, key);
		}
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
		}
		return null;
	}

	
}