package edu.udo.piq.lwjgl3;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PCursor.PCursorType;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.util.PCompUtil;

public class Lwjgl3PMouse implements PMouse {
	
	private static final Map<PCursorType, Lwjgl3PCursor> DEFAULT_CURSOR_MAP = new EnumMap<>(PCursorType.class);
	static {
		for (PCursorType type : PCursorType.values()) {
			if (type != PCursorType.CUSTOM) {
				DEFAULT_CURSOR_MAP.put(type, new Lwjgl3PCursor(type));
			}
		}
	}
	
	protected final GLFWMouseButtonCallback mouseBtnCB = new GLFWMouseButtonCallback() {
		public void invoke(long window, int glfwBtn, int glfwAction, int glfwMods) {
			updateMetaModifiers(glfwMods);
			if (glfwAction == GLFW.GLFW_PRESS) {
				onPress(getButtonID(glfwBtn));
			} else if (glfwAction == GLFW.GLFW_RELEASE) {
				onRelease(getButtonID(glfwBtn));
			} else if (glfwAction == GLFW.GLFW_REPEAT) {
				onPress(getButtonID(glfwBtn));
			}
		}
	};
	protected final GLFWCursorPosCallback mousePosCB = new GLFWCursorPosCallback() {
		public void invoke(long window, double xpos, double ypos) {
			setPosition(xpos, ypos);
		}
	};
	private final Lwjgl3PRoot root;
	private final List<PMouseObs> obsList = new CopyOnWriteArrayList<>();
	private final boolean[] btnPressed;
	private final boolean[] btnReleased;
	private final boolean[] btnTriggered;
	private Lwjgl3PCursor currentCursor = DEFAULT_CURSOR_MAP.get(PCursorType.NORMAL);
	private int x, y, dx, dy;
	private boolean compAtMouseCacheValid;
	private PComponent compAtMouseCache;
	
	public Lwjgl3PMouse(Lwjgl3PRoot lwjglRoot) {
		root = lwjglRoot;
		btnPressed = new boolean[3];
		btnReleased = new boolean[3];
		btnTriggered = new boolean[3];
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;
	}
	
	protected void update() {
		Arrays.fill(btnReleased, false);
		Arrays.fill(btnTriggered, false);
		dx = 0;
		dy = 0;
		invalidateCompAtMouseCache();
	}
	
	private void updateMetaModifiers(int glfwModifiers) {
		Lwjgl3PKeyboard kb = root.getKeyboard();
		kb.updateMetaModifiers(glfwModifiers);
	}
	
	private void invalidateCompAtMouseCache() {
		compAtMouseCacheValid = false;
		compAtMouseCache = null;
	}
	
	private void onPress(MouseButton btn) {
		if (btn == null) {
			return;
		}
		int index = getMouseButtonID(btn);
		if (!btnPressed[index]) {
			btnPressed[index] = true;
			btnTriggered[index] = true;
			btnReleased[index] = false;
			fireTriggerEvent(btn);
		}
		firePressEvent(btn);
	}
	
	private void onRelease(MouseButton btn) {
		if (btn == null) {
			return;
		}
		int index = getMouseButtonID(btn);
		if (!btnReleased[index]) {
			btnReleased[index] = true;
			btnPressed[index] = false;
			fireReleaseEvent(btn);
		}
	}
	
	private MouseButton getButtonID(int glfwBtn) {
		if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return MouseButton.LEFT;
		} else if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			return MouseButton.RIGHT;
		} else if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
			return MouseButton.MIDDLE;
		}
		return null;
	}
	
	private int getMouseButtonID(MouseButton btn) {
		switch (btn) {
		case LEFT:
		case DRAG_AND_DROP:
			return 0;
		case POPUP_TRIGGER:
		case RIGHT:
			return 1;
		case MIDDLE:
			return 2;
		default:
			throw new IllegalArgumentException("Mouse button not supported: "+btn);
		}
	}
	
	private void setPosition(double xPos, double yPos) {
		invalidateCompAtMouseCache();
		int mx = (int) Math.round(xPos);
		int my = (int) Math.round(yPos);
		dx = mx - x;
		dy = my - y;
		x = mx;
		y = my;
		if (dx != 0 || dy != 0) {
			fireMoveEvent();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getDeltaX() {
		return dx;
	}
	
	public int getDeltaY() {
		return dy;
	}
	
	public boolean isPressed(MouseButton btn) {
		return btnPressed[getMouseButtonID(btn)];
	}
	
	public boolean isReleased(MouseButton btn) {
		return btnReleased[getMouseButtonID(btn)];
	}
	
	public boolean isTriggered(MouseButton btn) {
		return btnTriggered[getMouseButtonID(btn)];
	}
	
	public PComponent getComponentAtMouse() {
		if (!compAtMouseCacheValid) {
			compAtMouseCache = PCompUtil.getComponentAt(root, getX(), getY());
			compAtMouseCacheValid = true;
		}
		return compAtMouseCache;
	}
	
	public void setCursor(PCursor cursor) {
		if (cursor == null) {
			throw new IllegalArgumentException("cursor="+cursor);
		}
		if (currentCursor != cursor) {
			currentCursor = (Lwjgl3PCursor) cursor;
		}
	}
	
	public void setCursor(PCursorType cursorType) {
		if (cursorType == null || cursorType == PCursorType.CUSTOM) {
			throw new IllegalArgumentException("cursorType="+cursorType);
		}
		setCursor(DEFAULT_CURSOR_MAP.get(cursorType));
	}
	
	public PCursor getCursor() {
		return currentCursor;
	}
	
	public PCursor getCustomCursor(PImageResource image, int offsetX, int offsetY) {
		// TODO:
		return DEFAULT_CURSOR_MAP.get(PCursorType.NORMAL);
	}
	
	public void addObs(PMouseObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PMouseObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireMoveEvent() {
		for (PMouseObs obs : obsList) {
			obs.onMouseMoved(this);
		}
	}
	
	protected void fireTriggerEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.onButtonTriggered(this, btn);
		}
	}
	
	protected void fireReleaseEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.onButtonReleased(this, btn);
		}
	}
	
	protected void firePressEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.onButtonPressed(this, btn);
		}
	}
	
}