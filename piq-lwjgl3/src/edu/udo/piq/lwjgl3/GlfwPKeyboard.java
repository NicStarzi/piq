package edu.udo.piq.lwjgl3;

import java.lang.Character.UnicodeBlock;
import java.util.EnumMap;
import java.util.Map;

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
	protected final Map<Key, Key> ctrlMetaMap = new EnumMap<>(Key.class);
	protected final boolean[] nowPressed = new boolean[Key.COUNT];
	protected final boolean[] prevPressed = new boolean[Key.COUNT];
	protected final boolean[] modState = new boolean[Modifier.COUNT];
	protected final char[] codePointToCharArray = new char[2];
	protected boolean capsLockDown;
	
	public GlfwPKeyboard(GlfwPRoot root) {
		this.root = root;
		ctrlMetaMap.put(Key.C, Key.COPY);
		ctrlMetaMap.put(Key.X, Key.CUT);
		ctrlMetaMap.put(Key.V, Key.PASTE);
		ctrlMetaMap.put(Key.Z, Key.UNDO);
		ctrlMetaMap.put(Key.Y, Key.REDO);
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
	public boolean isPressed(Key key) {
		return nowPressed[key.ordinal()];
	}
	
	@Override
	public boolean isTriggered(Key key) {
		return nowPressed[key.ordinal()] && !prevPressed[key.ordinal()];
	}
	
	@Override
	public boolean isReleased(Key key) {
		return !nowPressed[key.ordinal()] && prevPressed[key.ordinal()];
	}
	
	@Override
	public boolean isModifierToggled(Modifier modifier) {
		return modState[modifier.ordinal()];
	}
	
	protected void onKeyCallback(int gflwKey, int glfwAct, int glfwMods) {
		boolean newPressedValue = glfwAct == GLFW.GLFW_PRESS;
		updateMetaModifiers(glfwMods);
		
		Key key = glfWKeyToPKey(gflwKey);
		if (key == Key.CAPSLOCK && newPressedValue) {
			capsLockDown = !capsLockDown;
		}
		boolean shift = (glfwMods & GLFW.GLFW_MOD_SHIFT) != 0;
		setModifierState(Modifier.CAPS, shift || capsLockDown);
		
		if (key != null) {
			setKeyPressedState(key, newPressedValue);
		}
	}
	
	protected void setKeyPressedState(Key key, boolean newPressedValue) {
		if (isModifierToggled(Modifier.CTRL)) {
			Key ctrlKey = ctrlMetaMap.get(key);
			if (ctrlKey != null) {
				key = ctrlKey;
			}
		}
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
	
	protected Key glfWKeyToPKey(int glfwKey) {
		switch (glfwKey) {
		case GLFW.GLFW_KEY_0:
			return Key.NUM_0;
		case GLFW.GLFW_KEY_1:
			return Key.NUM_1;
		case GLFW.GLFW_KEY_2:
			return Key.NUM_2;
		case GLFW.GLFW_KEY_3:
			return Key.NUM_3;
		case GLFW.GLFW_KEY_4:
			return Key.NUM_4;
		case GLFW.GLFW_KEY_5:
			return Key.NUM_5;
		case GLFW.GLFW_KEY_6:
			return Key.NUM_6;
		case GLFW.GLFW_KEY_7:
			return Key.NUM_7;
		case GLFW.GLFW_KEY_8:
			return Key.NUM_8;
		case GLFW.GLFW_KEY_9:
			return Key.NUM_9;
		case GLFW.GLFW_KEY_A:
			return Key.A;
		case GLFW.GLFW_KEY_B:
			return Key.B;
		case GLFW.GLFW_KEY_C:
			return Key.C;
		case GLFW.GLFW_KEY_D:
			return Key.D;
		case GLFW.GLFW_KEY_E:
			return Key.E;
		case GLFW.GLFW_KEY_F:
			return Key.F;
		case GLFW.GLFW_KEY_G:
			return Key.G;
		case GLFW.GLFW_KEY_H:
			return Key.H;
		case GLFW.GLFW_KEY_I:
			return Key.I;
		case GLFW.GLFW_KEY_J:
			return Key.J;
		case GLFW.GLFW_KEY_K:
			return Key.K;
		case GLFW.GLFW_KEY_L:
			return Key.L;
		case GLFW.GLFW_KEY_M:
			return Key.M;
		case GLFW.GLFW_KEY_N:
			return Key.N;
		case GLFW.GLFW_KEY_O:
			return Key.O;
		case GLFW.GLFW_KEY_P:
			return Key.P;
		case GLFW.GLFW_KEY_Q:
			return Key.Q;
		case GLFW.GLFW_KEY_R:
			return Key.R;
		case GLFW.GLFW_KEY_S:
			return Key.S;
		case GLFW.GLFW_KEY_T:
			return Key.T;
		case GLFW.GLFW_KEY_U:
			return Key.U;
		case GLFW.GLFW_KEY_V:
			return Key.V;
		case GLFW.GLFW_KEY_W:
			return Key.W;
		case GLFW.GLFW_KEY_X:
			return Key.X;
		case GLFW.GLFW_KEY_Y:
			return Key.Y;
		case GLFW.GLFW_KEY_Z:
			return Key.Z;
		case GLFW.GLFW_KEY_UP:
			return Key.UP;
		case GLFW.GLFW_KEY_DOWN:
			return Key.DOWN;
		case GLFW.GLFW_KEY_LEFT:
			return Key.LEFT;
		case GLFW.GLFW_KEY_RIGHT:
			return Key.RIGHT;
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
		case GLFW.GLFW_KEY_LEFT_CONTROL:
			return Key.CTRL;
		case GLFW.GLFW_KEY_RIGHT_ALT:
			return Key.ALT_GR;
		case GLFW.GLFW_KEY_LEFT_ALT:
			return Key.ALT;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			return Key.SHIFT;
		case GLFW.GLFW_KEY_SPACE:
			return Key.SPACE;
		case GLFW.GLFW_KEY_ENTER:
			return Key.ENTER;
		case GLFW.GLFW_KEY_ESCAPE:
			return Key.ESC;
		case GLFW.GLFW_KEY_DELETE:
			return Key.DEL;
		case GLFW.GLFW_KEY_BACKSPACE:
			return Key.BACKSPACE;
		case GLFW.GLFW_KEY_CAPS_LOCK:
			return Key.CAPSLOCK;
		case GLFW.GLFW_KEY_TAB:
			return Key.TAB;
		case GLFW.GLFW_KEY_HOME:
			return Key.HOME;
		case GLFW.GLFW_KEY_PAGE_UP:
			return Key.PAGE_UP;
		case GLFW.GLFW_KEY_PAGE_DOWN:
			return Key.PAGE_DOWN;
		case GLFW.GLFW_KEY_END:
			return Key.END;
		}
		return Key.ENTER;
	}
	
}