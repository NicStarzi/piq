package edu.udo.piq.implementation.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;

public class SwingPMouse implements PMouse {
	
	private final List<PMouseObs> obsList = new CopyOnWriteArrayList<>();
	private final boolean[] btnPressed;
	private final boolean[] btnReleased;
	private final boolean[] btnTriggered;
	private int x, y, dx, dy;
	
	public SwingPMouse(JComponent base) {
		btnPressed = new boolean[MouseButton.values().length];
		btnReleased = new boolean[MouseButton.values().length];
		btnTriggered = new boolean[MouseButton.values().length];
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
			}
			public void mouseDragged(MouseEvent e) {
				setPosition(e.getX(), e.getY());
			}
		});
	}
	
	protected void update() {
		Arrays.fill(btnReleased, false);
		Arrays.fill(btnTriggered, false);
		dx = 0;
		dy = 0;
	}
	
	private void onPress(MouseButton btn) {
		if (btn == null) {
			return;
		}
		int index = btn.ordinal();
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
		int index = btn.ordinal();
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
		return btnPressed[btn.ordinal()];
	}
	
	public boolean isReleased(MouseButton btn) {
		return btnReleased[btn.ordinal()];
	}
	
	public boolean isTriggered(MouseButton btn) {
		return btnTriggered[btn.ordinal()];
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