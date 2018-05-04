package edu.udo.piq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.popup.PPopup;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.tools.ImmutablePPoint;
import edu.udo.piq.util.PiqUtil;

public interface PMouse {
	
	/**
	 * Returns the x-coordinate of the mouse pointer (or similar
	 * pointing device) on screen.<br>
	 * 
	 * @return x-coordinate on screen
	 */
	public int getX();
	
	/**
	 * Returns the y-coordinate of the mouse pointer (or similar
	 * pointing device) on screen.<br>
	 * 
	 * @return y-coordinate on screen
	 */
	public int getY();
	
	public default PPoint getPosition() {
		return new ImmutablePPoint(getX(), getY());
	}
	
	/**
	 * Returns the distance the mouse pointer has traveled on the
	 * x-axis since the last update cycle of the GUI.<br>
	 * 
	 * @return distance traveled on x-axis
	 */
	public int getDeltaX();
	
	/**
	 * Returns the distance the mouse pointer has traveled on the
	 * y-axis since the last update cycle of the GUI.<br>
	 * 
	 * @return distance traveled on y-axis
	 */
	public int getDeltaY();
	
	/**
	 * Returns true if the given {@link MouseButton} is being pressed
	 * down at this update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button is being pressed right now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isPressed(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns true if the given {@link MouseButton} has just been
	 * released in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been released just now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isReleased(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns true if the given {@link MouseButton} has just been
	 * triggered in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been triggered just now
	 * @throws NullPointerException if btn is null
	 */
	public boolean isTriggered(MouseButton btn) throws NullPointerException;
	
	/**
	 * Returns true if the given {@link MouseButton} is being pressed
	 * down at this update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button is being pressed right now
	 * @throws NullPointerException if btn is null
	 */
	public default boolean isPressed(VirtualMouseButton btn) {
		switch (btn) {
		case DRAG_AND_DROP:
			return isPressed(MouseButton.LEFT);
		case POPUP_TRIGGER:
			return isPressed(MouseButton.RIGHT);
		default:
			return false;
		}
	}
	
	/**
	 * Returns true if the given {@link MouseButton} has just been
	 * released in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been released just now
	 * @throws NullPointerException if btn is null
	 */
	public default boolean isReleased(VirtualMouseButton btn) {
		switch (btn) {
		case DRAG_AND_DROP:
			return isReleased(MouseButton.LEFT);
		case POPUP_TRIGGER:
			return isReleased(MouseButton.RIGHT);
		default:
			return false;
		}
	}
	
	/**
	 * Returns true if the given {@link MouseButton} has just been
	 * triggered in the last update cycle.<br>
	 * 
	 * @param btn the mouse button
	 * @return whether the button has been triggered just now
	 * @throws NullPointerException if btn is null
	 */
	public default boolean isTriggered(VirtualMouseButton btn) {
		switch (btn) {
		case DRAG_AND_DROP:
			return isTriggered(MouseButton.LEFT);
		case POPUP_TRIGGER:
			return isTriggered(MouseButton.RIGHT);
		default:
			return false;
		}
	}
	
	/**
	 * Returns the topmost component that is directly underneath the mouse.<br>
	 * This component may be cached by the mouse to improve performance.<br>
	 * The returned component may be null if the mouse is outside of the
	 * {@link PRoot PRoots} bounds.<br>
	 * 
	 * @return the {@link PComponent} that is positioned at this {@link PMouse PMouses} x- and y-coordinates or null
	 * @see PComponent#getClippedBounds()
	 * @see PiqUtil#getComponentAt(PComponent, int, int)
	 * @see PLayout#getChildAt(int, int)
	 */
	public PComponent getComponentAtMouse();
	
	public default PPoint getOffsetToComponent(PComponent component) {
		return new ImmutablePPoint(getOffsetToComponentX(component), getOffsetToComponentY(component));
	}
	
	public default int getOffsetToComponent(PComponent component, Axis axis) {
		return axis.getCoordinate(this) - axis.getFirstCoordinate(component);
	}
	
	public default int getOffsetToComponentX(PComponent component) {
		return getX() - component.getBounds().getX();
	}
	
	public default int getOffsetToComponentY(PComponent component) {
		return getY() - component.getBounds().getY();
	}
	
	public PCursor getCursorDefault();
	
	public PCursor getCursorHand();
	
	public PCursor getCursorText();
	
	public PCursor getCursorScroll();
	
	public PCursor getCursorBusy();
	
	/**
	 * Returns the currently used {@link PCursor}.<br>
	 * This method never returns null, even if cursors are not supported by the
	 * implementation.<br>
	 * 
	 * @return the {@link PCursor} that is currently being used
	 * @see PComponent#getMouseOverCursor(PMouse)
	 */
	public PCursor getCurrentCursor();
	
	public boolean isCursorSupported(PCursor cursor);
	
	public void addObs(PMouseObs obs);
	
	public void removeObs(PMouseObs obs);
	
	/**
	 * An enumeration of different mouse buttons which can be used by the GUI.<br>
	 * The available buttons are LEFT, RIGHT and MIDDLE. The left mouse button
	 * is the primary button used by most all components. The right mouse button
	 * is the secondary mouse button used mostly by pop-ups and context menus.<br>
	 * A mouse does not need to actually use all or any of these buttons.<br>
	 * If a mouse does not support one of these buttons no exception will be
	 * thrown, instead the method using them will simply return false.<br>
	 */
	public static enum MouseButton {
		/**
		 * The primary mouse button.<br>
		 * Most functionality is using this button.<br>
		 */
		LEFT,
		/**
		 * The secondary mouse button.<br>
		 * This button is mostly used for pop-ups and context menus.<br>
		 */
		RIGHT,
		/**
		 * Standard components do not use this button.<br>
		 */
		MIDDLE,
		;
		public static final List<PMouse.MouseButton> ALL =
				Collections.unmodifiableList(Arrays.asList(MouseButton.values()));
		public static final int COUNT = ALL.size();
		
		public final int ID = ordinal();
	}
	public static enum VirtualMouseButton {
		/**
		 * This mouse button is used for dragging data from one
		 * {@link PSelectionComponent} to another via a
		 * drag-and-drop motion.<br>
		 * This is a virtual button which should be identical to a
		 * non virtual button.<br>
		 */
		DRAG_AND_DROP,
		/**
		 * This mouse button is used to trigger {@link PPopup popups}
		 * on top of components.<br>
		 * This is a virtual button which should be identical to a
		 * non virtual button.<br>
		 */
		POPUP_TRIGGER,
		;
		public static final List<PMouse.VirtualMouseButton> ALL
			= Collections.unmodifiableList(Arrays.asList(PMouse.VirtualMouseButton.values()));
		public static final int COUNT = ALL.size();
		
		public final int ID = ordinal();
	}
	
}