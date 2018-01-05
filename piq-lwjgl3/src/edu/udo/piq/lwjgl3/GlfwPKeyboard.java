package edu.udo.piq.lwjgl3;

import java.lang.Character.UnicodeBlock;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.tools.AbstractPKeyboard;

public class GlfwPKeyboard extends AbstractPKeyboard implements PKeyboard {
	
	protected final GLFWKeyCallback keyCB = new GLFWKeyCallback() {
		
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (action != GLFW.GLFW_REPEAT) {
				onKeyCallback(key, action, mods);
			}
		}
	};
	protected final GLFWCharCallback charCB = new GLFWCharCallback() {
		
		@Override
		public void invoke(long window, int codePoint) {
			onCharCallback(codePoint);
		}
	};
	protected final GlfwPRoot root;
	protected final boolean[] nowPressed = new boolean[ActualKey.COUNT];
	protected final boolean[] prevPressed = new boolean[ActualKey.COUNT];
	protected final boolean[] modState = new boolean[Modifier.COUNT];
	protected final char[] codePointToCharArray = new char[2];
	protected boolean capsLockDown;
	
	public GlfwPKeyboard(GlfwPRoot root) {
		this.root = root;
	}
	
	protected void install() {
		GLFW.glfwSetCharCallback(root.wndHandle, charCB);
		GLFW.glfwSetKeyCallback(root.wndHandle, keyCB);
	}
	
	protected void uninstall() {
		charCB.free();
		keyCB.free();
	}
	
	@Override
	public boolean isPressed(ActualKey key) {
		int idx = key.ordinal();
		return nowPressed[idx];
	}
	
	@Override
	public boolean isTriggered(ActualKey key) {
		int idx = key.ordinal();
		return nowPressed[idx] && !prevPressed[idx];
	}
	
	@Override
	public boolean isReleased(ActualKey key) {
		int idx = key.ordinal();
		return !nowPressed[idx] && prevPressed[idx];
	}
	
	@Override
	public boolean isModifierToggled(Modifier modifier) {
		return modState[modifier.ordinal()];
	}
	
	protected void onKeyCallback(int gflwKey, int glfwAct, int glfwMods) {
		boolean newPressedValue = glfwAct == GLFW.GLFW_PRESS;
		updateMetaModifiers(glfwMods);
		
		ActualKey key = glfWKeyToPKey(gflwKey);
		if (key == ActualKey.CAPSLOCK && newPressedValue) {
			capsLockDown = !capsLockDown;
		}
		boolean shift = (glfwMods & GLFW.GLFW_MOD_SHIFT) != 0;
		setModifierState(Modifier.CAPS, shift || capsLockDown);
		
		if (key != null) {
			setKeyPressedState(key, newPressedValue);
		}
	}
	
	protected void setKeyPressedState(ActualKey key, boolean newPressedValue) {
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
	
	protected void updateMetaModifiers(int glfwMods) {
		boolean alt = (glfwMods & GLFW.GLFW_MOD_ALT) != 0;
		boolean altGr = false;
		boolean ctrl = (glfwMods & GLFW.GLFW_MOD_CONTROL) != 0;
		boolean meta = (glfwMods & GLFW.GLFW_MOD_SUPER) != 0;
		setModifierState(Modifier.ALT, alt);
		setModifierState(Modifier.ALT_GRAPH, altGr);
		setModifierState(Modifier.CTRL, ctrl);
		setModifierState(Modifier.META, meta);
	}
	
	protected void setModifierState(Modifier mod, boolean newState) {
		boolean oldState = modState[mod.ordinal()];
		if (oldState != newState) {
			modState[mod.ordinal()] = newState;
			fireModifierToggledEvent(mod);
		}
	}
	
	protected void onCharCallback(int codePoint) {
		if (isTypeable(codePoint)) {
			int length = Character.toChars(codePoint, codePointToCharArray, 0);
			String typedString = String.valueOf(codePointToCharArray, 0, length);
			fireStringTypedEvent(typedString);
		}
	}
	
	protected boolean isTypeable(int codePoint) {
		if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
			return false;
		}
		UnicodeBlock block = UnicodeBlock.of(codePoint);
		return block != null && block != UnicodeBlock.SPECIALS;
	}
	
	protected ActualKey glfWKeyToPKey(int glfwKey) {
		switch (glfwKey) {
		case GLFW.GLFW_KEY_0:
			return ActualKey.NUM_0;
		case GLFW.GLFW_KEY_1:
			return ActualKey.NUM_1;
		case GLFW.GLFW_KEY_2:
			return ActualKey.NUM_2;
		case GLFW.GLFW_KEY_3:
			return ActualKey.NUM_3;
		case GLFW.GLFW_KEY_4:
			return ActualKey.NUM_4;
		case GLFW.GLFW_KEY_5:
			return ActualKey.NUM_5;
		case GLFW.GLFW_KEY_6:
			return ActualKey.NUM_6;
		case GLFW.GLFW_KEY_7:
			return ActualKey.NUM_7;
		case GLFW.GLFW_KEY_8:
			return ActualKey.NUM_8;
		case GLFW.GLFW_KEY_9:
			return ActualKey.NUM_9;
		case GLFW.GLFW_KEY_A:
			return ActualKey.A;
		case GLFW.GLFW_KEY_B:
			return ActualKey.B;
		case GLFW.GLFW_KEY_C:
			return ActualKey.C;
		case GLFW.GLFW_KEY_D:
			return ActualKey.D;
		case GLFW.GLFW_KEY_E:
			return ActualKey.E;
		case GLFW.GLFW_KEY_F:
			return ActualKey.F;
		case GLFW.GLFW_KEY_G:
			return ActualKey.G;
		case GLFW.GLFW_KEY_H:
			return ActualKey.H;
		case GLFW.GLFW_KEY_I:
			return ActualKey.I;
		case GLFW.GLFW_KEY_J:
			return ActualKey.J;
		case GLFW.GLFW_KEY_K:
			return ActualKey.K;
		case GLFW.GLFW_KEY_L:
			return ActualKey.L;
		case GLFW.GLFW_KEY_M:
			return ActualKey.M;
		case GLFW.GLFW_KEY_N:
			return ActualKey.N;
		case GLFW.GLFW_KEY_O:
			return ActualKey.O;
		case GLFW.GLFW_KEY_P:
			return ActualKey.P;
		case GLFW.GLFW_KEY_Q:
			return ActualKey.Q;
		case GLFW.GLFW_KEY_R:
			return ActualKey.R;
		case GLFW.GLFW_KEY_S:
			return ActualKey.S;
		case GLFW.GLFW_KEY_T:
			return ActualKey.T;
		case GLFW.GLFW_KEY_U:
			return ActualKey.U;
		case GLFW.GLFW_KEY_V:
			return ActualKey.V;
		case GLFW.GLFW_KEY_W:
			return ActualKey.W;
		case GLFW.GLFW_KEY_X:
			return ActualKey.X;
		case GLFW.GLFW_KEY_Y:
			return ActualKey.Y;
		case GLFW.GLFW_KEY_Z:
			return ActualKey.Z;
		case GLFW.GLFW_KEY_UP:
			return ActualKey.UP;
		case GLFW.GLFW_KEY_DOWN:
			return ActualKey.DOWN;
		case GLFW.GLFW_KEY_LEFT:
			return ActualKey.LEFT;
		case GLFW.GLFW_KEY_RIGHT:
			return ActualKey.RIGHT;
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
		case GLFW.GLFW_KEY_LEFT_CONTROL:
			return ActualKey.CTRL;
		case GLFW.GLFW_KEY_RIGHT_ALT:
			return ActualKey.ALT_GRAPH;
		case GLFW.GLFW_KEY_LEFT_ALT:
			return ActualKey.ALT;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			return ActualKey.SHIFT;
		case GLFW.GLFW_KEY_SPACE:
			return ActualKey.SPACE;
		case GLFW.GLFW_KEY_ENTER:
			return ActualKey.ENTER;
		case GLFW.GLFW_KEY_ESCAPE:
			return ActualKey.ESCAPE;
		case GLFW.GLFW_KEY_DELETE:
			return ActualKey.DELETE;
		case GLFW.GLFW_KEY_BACKSPACE:
			return ActualKey.BACKSPACE;
		case GLFW.GLFW_KEY_CAPS_LOCK:
			return ActualKey.CAPSLOCK;
		case GLFW.GLFW_KEY_TAB:
			return ActualKey.TAB;
		case GLFW.GLFW_KEY_HOME:
			return ActualKey.HOME;
		case GLFW.GLFW_KEY_PAGE_UP:
			return ActualKey.PAGE_UP;
		case GLFW.GLFW_KEY_PAGE_DOWN:
			return ActualKey.PAGE_DOWN;
		case GLFW.GLFW_KEY_END:
			return ActualKey.END;
		default:
			return ActualKey.ENTER;
		}
	}
	
}