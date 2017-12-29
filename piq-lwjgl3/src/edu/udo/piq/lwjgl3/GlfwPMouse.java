package edu.udo.piq.lwjgl3;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.MemoryUtil;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PMouse;
import edu.udo.piq.tools.AbstractPMouse;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class GlfwPMouse extends AbstractPMouse implements PMouse {
	
	protected final GLFWMouseButtonCallback mouseBtnCB = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int glfwBtn, int glfwAction, int glfwMods) {
			updateMetaModifiers(glfwMods);
			if (glfwAction == GLFW.GLFW_PRESS) {
				onPress(glfwButtonToPButton(glfwBtn));
			} else if (glfwAction == GLFW.GLFW_RELEASE) {
				onRelease(glfwButtonToPButton(glfwBtn));
			} else if (glfwAction == GLFW.GLFW_REPEAT) {
				onPress(glfwButtonToPButton(glfwBtn));
			}
		}
	};
	protected final GLFWCursorPosCallback mousePosCB = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			setPosition(xpos, ypos);
		}
	};
	protected final GlfwPRoot root;
	protected final int[] btnClickCount = new int[MouseButton.COUNT];
	protected final double[] btnEvtTime = new double[btnClickCount.length];
	protected final boolean[] btnPressed = new boolean[btnClickCount.length];
	protected final boolean[] btnReleased = new boolean[btnClickCount.length];
	protected final boolean[] btnTriggered = new boolean[btnClickCount.length];
	protected int x, y, dx, dy;
	protected int ox;
	protected int oy;
	protected double multiClickTimeThreshold = 0.5;
	protected boolean compAtMouseCacheValid;
	protected PComponent compAtMouseCache;
	protected GlfwPCursor currentCursor;
	
	public GlfwPMouse(GlfwPRoot root) {
		this.root = root;
		currentCursor = getCursorDefault();
	}
	
	protected void install() {
		GLFW.glfwSetMouseButtonCallback(root.wndHandle, mouseBtnCB);
		GLFW.glfwSetCursorPosCallback(root.wndHandle, mousePosCB);
	}
	
	protected void uninstall() {
		mouseBtnCB.free();
		mousePosCB.free();
	}
	
	protected void update() {
		Arrays.fill(btnReleased, false);
		Arrays.fill(btnTriggered, false);
		dx = 0;
		dy = 0;
		invalidateCompAtMouseCache();
	}
	
	protected void updateMetaModifiers(int glfwModifiers) {
		GlfwPKeyboard kb = root.getKeyboard();
		kb.updateMetaModifiers(glfwModifiers);
	}
	
	public void mouseOverCursorChanged(PComponent component) {
		if (compAtMouseCache == component) {
			refreshCursor();
		}
	}
	
	protected void invalidateCompAtMouseCache() {
		compAtMouseCacheValid = false;
	}
	
	protected void onPress(MouseButton btn) {
		if (btn == null) {
			return;
		}
		if (!btnPressed[btn.ID]) {
			btnPressed[btn.ID] = true;
			btnTriggered[btn.ID] = true;
			btnReleased[btn.ID] = false;
			double oldTime = btnEvtTime[btn.ID];
			btnEvtTime[btn.ID] = GLFW.glfwGetTime();
			if (btnEvtTime[btn.ID] - oldTime < multiClickTimeThreshold) {
				btnClickCount[btn.ID]++;
			} else {
				btnClickCount[btn.ID] = 1;
			}
			fireTriggerEvent(btn, btnClickCount[btn.ID]);
		}
		firePressEvent(btn, btnClickCount[btn.ID]);
	}
	
	protected void onRelease(MouseButton btn) {
		if (btn == null) {
			return;
		}
		if (!btnReleased[btn.ID]) {
			btnReleased[btn.ID] = true;
			btnPressed[btn.ID] = false;
			fireReleaseEvent(btn, btnClickCount[btn.ID]);
		}
	}
	
	protected MouseButton glfwButtonToPButton(int glfwBtn) {
		if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return MouseButton.LEFT;
		} else if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			return MouseButton.RIGHT;
		} else if (glfwBtn == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
			return MouseButton.MIDDLE;
		}
		return null;
	}
	
	protected void setPosition(double xPos, double yPos) {
		int mx = (int) (xPos + 0.5);
		int my = (int) (yPos + 0.5);
		dx = mx - x;
		dy = my - y;
		x = mx;
		y = my;
		if (dx != 0 || dy != 0) {
			invalidateCompAtMouseCache();
			refreshCursor();
			fireMoveEvent();
		}
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	@Override
	public int getY() {
		return y;
	}
	
	@Override
	public int getDeltaX() {
		return dx;
	}
	
	@Override
	public int getDeltaY() {
		return dy;
	}
	
	@Override
	public boolean isPressed(MouseButton btn) {
		return btnPressed[btn.ID];
	}
	
	@Override
	public boolean isReleased(MouseButton btn) {
		return btnReleased[btn.ID];
	}
	
	@Override
	public boolean isTriggered(MouseButton btn) {
		return btnTriggered[btn.ID];
	}
	
	@Override
	public PComponent getComponentAtMouse() {
		if (!compAtMouseCacheValid) {
			compAtMouseCache = PiqUtil.getComponentAt(root, getX(), getY());
			compAtMouseCacheValid = true;
			refreshCursor();
		}
		return compAtMouseCache;
	}
	
	protected void refreshCursor() {
		PComponent comp = getComponentAtMouse();
		if (comp == null) {
			GLFW.glfwSetCursor(root.wndHandle, MemoryUtil.NULL);
			return;
		}
		
		GlfwPCursor cursor = ThrowException.ifTypeCastFails(
				comp.getMouseOverCursor(this),
				GlfwPCursor.class, "!(cursor instanceof GlfwPCursor)");
		if (cursor != currentCursor) {
			currentCursor = cursor;
			GLFW.glfwSetCursor(root.wndHandle, currentCursor.getGlfwHandle());
		}
	}
	
	@Override
	public GlfwPCursor getCursorDefault() {
		return GlfwStandardCursorType.ARROW.getCursor();
	}
	
	@Override
	public GlfwPCursor getCursorHand() {
		return GlfwStandardCursorType.HAND.getCursor();
	}
	
	@Override
	public GlfwPCursor getCursorText() {
		return GlfwStandardCursorType.TEXT.getCursor();
	}
	
	@Override
	public GlfwPCursor getCursorScroll() {
		return GlfwStandardCursorType.RESIZE_H.getCursor();
	}
	
	@Override
	public GlfwPCursor getCursorBusy() {
		return GlfwStandardCursorType.ARROW.getCursor();
	}
	
	@Override
	public GlfwPCursor getCurrentCursor() {
		return currentCursor;
	}
	
	@Override
	public boolean isCursorSupported(PCursor cursor) {
		return cursor instanceof GlfwPCursor && !((GlfwPCursor) cursor).isDisposed();
	}
	
}