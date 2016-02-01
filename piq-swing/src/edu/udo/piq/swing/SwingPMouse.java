package edu.udo.piq.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class SwingPMouse implements PMouse {
	
	private final PRoot root;
	private final JComponent base;
	private final List<PMouseObs> obsList = new CopyOnWriteArrayList<>();
	private final boolean[] btnPressed;
	private final boolean[] btnReleased;
	private final boolean[] btnTriggered;
	private AwtPCursor currentCursor = AwtPCursor.DEFAULT;
	private int x, y, dx, dy;
	private int clickCount;
	private boolean compAtMouseCacheValid;
	private PComponent compAtMouseCache;
	
	public SwingPMouse(PRoot root, JComponent base) {
		this.root = root;
		this.base = base;
		btnPressed = new boolean[3];
		btnReleased = new boolean[3];
		btnTriggered = new boolean[3];
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;
		
		base.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				clickCount = e.getClickCount();
				onRelease(getButtonID(e));
			}
			public void mousePressed(MouseEvent e) {
				clickCount = e.getClickCount();
				onPress(getButtonID(e));
			}
			public void mouseExited(MouseEvent e) {
				clickCount = e.getClickCount();
				setPosition(e.getX(), e.getY());
			}
			public void mouseEntered(MouseEvent e) {
				clickCount = e.getClickCount();
				setPosition(e.getX(), e.getY());
			}
			public void mouseClicked(MouseEvent e) {
				clickCount = e.getClickCount();
			}
		});
		base.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				clickCount = e.getClickCount();
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
			}
			public void mouseDragged(MouseEvent e) {
				clickCount = e.getClickCount();
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
			}
		});
	}
	
	protected void mouseOverCursorChanged(PComponent component) {
		if (compAtMouseCache == component) {
			refreshCursor();
		}
	}
	
	protected void refreshCursor() {
		if (compAtMouseCache == null || !compAtMouseCacheValid) {
			base.setCursor(null);
			return;
		}
		AwtPCursor cursor = ThrowException.ifTypeCastFails(
				compAtMouseCache.getMouseOverCursor(this), 
				AwtPCursor.class, "!(cursor instanceof AwtPCursor)");
		if (cursor != currentCursor) {
			currentCursor = cursor;
			currentCursor.applyTo(base);
		}
	}
	
	protected void update() {
		Arrays.fill(btnReleased, false);
		Arrays.fill(btnTriggered, false);
		dx = 0;
		dy = 0;
		invalidateCompAtMouseCache();
	}
	
	private void invalidateCompAtMouseCache() {
		compAtMouseCacheValid = false;
		compAtMouseCache = null;
		refreshCursor();
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
	
	private MouseButton getButtonID(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			return MouseButton.LEFT;
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			return MouseButton.RIGHT;
		}
		if (SwingUtilities.isMiddleMouseButton(e)) {
			return MouseButton.MIDDLE;
		}
		return null;
	}
	
	private int getMouseButtonID(MouseButton btn) {
		switch (btn) {
		case LEFT:
		case DRAG_AND_DROP:
			return 0;
		case RIGHT:
		case POPUP_TRIGGER:
			return 1;
		case MIDDLE:
			return 2;
		default:
			throw new IllegalArgumentException("Mouse button not supported: "+btn);
		}
	}
	
	private void setPosition(int mx, int my) {
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
	
	public int getClickCount() {
		return clickCount;
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
			refreshCursor();
		}
		return compAtMouseCache;
	}
	
	public PCursor getCurrentCursor() {
		return currentCursor;
	}
	
	public PCursor getCursorDefault() {
		return AwtPCursor.DEFAULT;
	}
	
	public PCursor getCursorHand() {
		return AwtPCursor.HAND;
	}
	
	public PCursor getCursorText() {
		return AwtPCursor.TEXT;
	}
	
	public PCursor getCursorScroll() {
		return AwtPCursor.SCROLL;
	}
	
	public PCursor getCursorBusy() {
		return AwtPCursor.BUSY;
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