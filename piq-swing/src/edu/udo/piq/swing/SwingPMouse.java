package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPMouse;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class SwingPMouse extends AbstractPMouse implements PMouse {
	
	protected final PRoot root;
	protected final Component base;
	protected final boolean[] btnPressed = new boolean[3];
	protected final boolean[] btnReleased = new boolean[3];
	protected final boolean[] btnTriggered = new boolean[3];
	protected AwtPCursor currentCursor = AwtPCursor.DEFAULT;
	protected int x, y, dx, dy;
	protected int ox;
	protected int oy;
	protected boolean compAtMouseCacheValid;
	protected PComponent compAtMouseCache;
	protected boolean updateMousePosOutsideBaseComponent = true;
	
	public SwingPMouse(PRoot root, Component base) {
		this.root = root;
		this.base = base;
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;
		
		base.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				onRelease(getButtonID(e), e.getClickCount());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				onPress(getButtonID(e), e.getClickCount());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setPosition(e.getX(), e.getY());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setPosition(e.getX(), e.getY());
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		base.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
				refreshCursor();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
				refreshCursor();
			}
		});
	}
	
	public void mouseOverCursorChanged(PComponent component) {
		if (compAtMouseCache == component) {
			refreshCursor();
		}
	}
	
	public void refreshCursor() {
//		System.out.println("SwingPMouse.refreshCursor()");
		PComponent comp = getComponentAtMouse();
//		if (compAtMouseCache == null || !compAtMouseCacheValid) {
//			base.setCursor(null);
//			return;
//		}
		if (comp == null) {
			base.setCursor(null);
			return;
		}
		AwtPCursor cursor = ThrowException.ifTypeCastFails(
				comp.getMouseOverCursor(this),
				AwtPCursor.class, "!(cursor instanceof AwtPCursor)");
		if (cursor != currentCursor) {
			currentCursor = cursor;
			currentCursor.applyTo(base);
		}
	}
	
	public void update() {
		Arrays.fill(btnReleased, false);
		Arrays.fill(btnTriggered, false);
		dx = 0;
		dy = 0;
		if (isUpdateMousePosOutsideBaseComponent()) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(mousePos, base);
			setPosition(mousePos.x, mousePos.y);
		}
		
		invalidateCompAtMouseCache();
	}
	
	public void setUpdateMousePosOutsideBaseComponent(boolean value) {
		updateMousePosOutsideBaseComponent = value;
	}
	
	public boolean isUpdateMousePosOutsideBaseComponent() {
		return updateMousePosOutsideBaseComponent;
	}
	
	protected void invalidateCompAtMouseCache() {
		compAtMouseCacheValid = false;
//		compAtMouseCache = null;
//		refreshCursor();
	}
	
	protected void onPress(MouseButton btn, int clickCount) {
		if (btn == null) {
			return;
		}
		if (!btnPressed[btn.ID]) {
			btnPressed[btn.ID] = true;
			btnTriggered[btn.ID] = true;
			btnReleased[btn.ID] = false;
			fireTriggerEvent(btn, clickCount);
		}
		firePressEvent(btn, clickCount);
	}
	
	protected void onRelease(MouseButton btn, int clickCount) {
		if (btn == null) {
			return;
		}
		if (!btnReleased[btn.ID]) {
			btnReleased[btn.ID] = true;
			btnPressed[btn.ID] = false;
			fireReleaseEvent(btn, clickCount);
		}
	}
	
	protected MouseButton getButtonID(MouseEvent e) {
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
	
	protected void setPosition(int mx, int my) {
		mx += ox;
		my += oy;
		dx = mx - x;
		dy = my - y;
		x = mx;
		y = my;
		if (dx != 0 || dy != 0) {
			fireMoveEvent();
		}
	}
	
	public void setOffset(int offsetX, int offsetY) {
		ox = offsetX;
		oy = offsetY;
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
	
	@Override
	public PCursor getCurrentCursor() {
		return currentCursor;
	}
	
	@Override
	public PCursor getCursorDefault() {
		return AwtPCursor.DEFAULT;
	}
	
	@Override
	public PCursor getCursorHand() {
		return AwtPCursor.HAND;
	}
	
	@Override
	public PCursor getCursorText() {
		return AwtPCursor.TEXT;
	}
	
	@Override
	public PCursor getCursorScroll() {
		return AwtPCursor.SCROLL;
	}
	
	@Override
	public PCursor getCursorBusy() {
		return AwtPCursor.BUSY;
	}
	
	@Override
	public boolean isCursorSupported(PCursor cursor) {
		return cursor instanceof AwtPCursor;
	}
	
}