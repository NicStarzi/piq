package edu.udo.piq.swing;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PCursor.PCursorType;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.util.PCompUtil;

public class SwingPMouse implements PMouse {
	
	private static final Map<PCursorType, AwtPCursor> DEFAULT_CURSOR_MAP = new EnumMap<>(PCursorType.class);
	static {
		for (PCursorType type : PCursorType.values()) {
			if (type != PCursorType.CUSTOM) {
				DEFAULT_CURSOR_MAP.put(type, new AwtPCursor(type));
			}
		}
	}
	
	private final PRoot root;
	private final JComponent base;
	private final List<PMouseObs> obsList = new CopyOnWriteArrayList<>();
	private final boolean[] btnPressed;
	private final boolean[] btnReleased;
	private final boolean[] btnTriggered;
	private AwtPCursor currentCursor = DEFAULT_CURSOR_MAP.get(PCursorType.NORMAL);
	private int x, y, dx, dy;
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
				onRelease(getButtonID(e));
			}
			public void mousePressed(MouseEvent e) {
				onPress(getButtonID(e));
			}
			public void mouseExited(MouseEvent e) {
				setPosition(e.getX(), e.getY());
			}
			public void mouseEntered(MouseEvent e) {
				setPosition(e.getX(), e.getY());
			}
			public void mouseClicked(MouseEvent e) {
			}
		});
		base.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
			}
			public void mouseDragged(MouseEvent e) {
				setPosition(e.getX(), e.getY());
				invalidateCompAtMouseCache();
			}
		});
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
			currentCursor = (AwtPCursor) cursor;
			currentCursor.applyTo(base);
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
		Image bufferedImg = ((BufferedPImageResource) image).getBufferedImage();
		return new AwtPCursor(bufferedImg, offsetX, offsetY);
	}
	
	public void addObs(PMouseObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PMouseObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireMoveEvent() {
		for (PMouseObs obs : obsList) {
			obs.mouseMoved(this);
		}
	}
	
	protected void fireTriggerEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.buttonTriggered(this, btn);
		}
	}
	
	protected void fireReleaseEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.buttonReleased(this, btn);
		}
	}
	
	protected void firePressEvent(MouseButton btn) {
		for (PMouseObs obs : obsList) {
			obs.buttonPressed(this, btn);
		}
	}
	
}