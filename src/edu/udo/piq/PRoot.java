package edu.udo.piq;

import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.PGlassPanel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.layouts.PRootLayout;

/**
 * The root of a GUI tree. Such a root is also a {@link PComponent}.<br>
 * A {@link PRoot} holds information about how a GUI is supposed to be 
 * rendered, how user input is obtained and how other system resources 
 * can be accessed.<br>
 * This interface is usually implemented by a platform dependent third 
 * party implementation. There is also an abstract default implementation 
 * which can be used as a base for new PRoot implementations.
 * 
 * @author Nic Starzi
 */
public interface PRoot extends PComponent {

	/**
	 * Returns this.<br>
	 * @return this
	 */
	public default PRoot getRoot() {
		return this;
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public default void setParent(PComponent parent) 
			throws UnsupportedOperationException 
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * Returns the {@link PLayout} of this {@link PRoot}.<br>
	 * The layout of a root is always an instance of {@link PRootLayout} as it is 
	 * needed to support {@link PRootOverlay PRootOverlays} and body {@link PPanel panels}.<br>
	 * 
	 * @see #getOverlay()
	 */
	public PRootLayout getLayout();
	
	/**
	 * Returns null.<br>
	 * @return null
	 */
	public default PComponent getParent() {
		return null;
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public default void setDesign(PDesign design) 
			throws UnsupportedOperationException  
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public default PDesign getDesign() 
			throws UnsupportedOperationException  
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public default void defaultRender(PRenderer renderer) 
			throws UnsupportedOperationException  
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * Always returns true for {@link PRoot} instances.<br>
	 * @return true
	 */
	public default boolean defaultFillsAllPixels() {
		return true;
	}
	
	/**
	 * Always returns false for {@link PRoot} instances.<br>
	 * @return true
	 */
	public default boolean isElusive() {
		return false;
	}
	
	/**
	 * Returns the {@link PBounds} of this {@link PRoot}.<br>
	 * Since a root does not have a parent its bounds are not defined 
	 * by a layout, instead the bounds of a root should be set directly 
	 * by the user.<br>
	 * 
	 * @return the bounds of this root
	 */
	public PBounds getBounds();
	
	/**
	 * Returns the {@link PDesignSheet} for this GUI.<br>
	 * This method never returns null.<br>
	 * A design sheet is used to determine how {@link PComponent}s are 
	 * supposed to be rendered.<br>
	 * 
	 * @return the design sheet for this GUI tree
	 * @see PDesignSheet
	 * @see PDesign
	 * @see PRenderer
	 */
	public PDesignSheet getDesignSheet();
	
	/**
	 * Updates all {@link PComponent}s in this GUI tree.
	 */
	public void update();
	
	/**
	 * This method should be called if a {@link PComponent} needs to 
	 * be re-rendered.<br>
	 * @param component the component that needs re-rendering
	 */
	public void reRender(PComponent component);
	
	/**
	 * Creates a new instance of {@link PDialog} and returns it.<br>
	 * The implementation is platform dependent.<br>
	 * The returned dialog will use the same {@link PDesignSheet} 
	 * as this root.<br>
	 * 
	 * @return a newly created dialog
	 * @see PDialog
	 * @see PDesignSheet
	 */
	public PDialog createDialog();
	
	/**
	 * Returns an instance of {@link PFontResource} that represents the 
	 * desired font defined by the name, size and style.<br>
	 * The implementation is platform dependent.<br>
	 * <br>
	 * The root is free to cache font resources and return the same 
	 * instance for each invocation of this method with the same 
	 * arguments.<br>
	 * This method should never return null.<br>
	 * 
	 * @param fontName the name of the font, for example "Arial"
	 * @param pointSize the point size of the font 
	 * @param style the style of the font, either PLAIN, BOLD, ITALIC or BOLD_ITALIC
	 * @return an instance of {@link PFontResource}
	 * @throws NullPointerException if fontName is null or style is null
	 * @throws IllegalArgumentException if an arguments value is not supported
	 */
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) 
			throws NullPointerException, IllegalArgumentException;
	
	/**
	 * Returns an instance of {@link PImageResource} that was loaded from 
	 * the given path.<br>
	 * The implementation is platform dependent.<br>
	 * <br>
	 * The root is free to cache image resources and return the same 
	 * instance for each invocation of this method with the same 
	 * arguments.<br>
	 * This method should never return null.<br>
	 * 
	 * @param imgPath the path from which the image source is loaded
	 * @return an instance of {@link PImageResource}
	 * @throws NullPointerException if imgPath is null
	 */
	public PImageResource fetchImageResource(String imgPath) throws NullPointerException;
	
	/**
	 * Returns an implementation of {@link PMouse}.<br>
	 * The implementation is platform dependent.<br>
	 * This method might return null if the GUI does not support a 
	 * mouse.<br>
	 * 
	 * @return an implementation of {@link PMouse} or null if a mouse is not supported
	 * @see PMouse
	 * @see PKeyboard
	 */
	public PMouse getMouse();
	
	/**
	 * Returns an implementation of {@link PKeyboard}.<br>
	 * The implementation is platform dependent.<br>
	 * This method might return null if the GUI does not support a 
	 * keyboard.<br>
	 * 
	 * @return an implementation of {@link PKeyboard} or null if a keyboard is not supported
	 * @see PKeyboard
	 * @see PMouse
	 */
	public PKeyboard getKeyboard();
	
	/**
	 * Returns the overlay for this {@link PRoot} or null if no overlay is supported.<br>
	 * The overlay is used (among other possible uses) for drop down menus, tooltips and the 
	 * visual representation of a drag and drop.<br>
	 * Custom components are free to use the overlay for other effects that need to be drawn 
	 * freely on top of the rest of the GUI.<br>
	 * If the returned overlay is not null it will never be null within the life cycle of 
	 * this {@link PRoot}.<br>
	 * <br>
	 * A common default implementation for {@link PRootOverlay} is {@link PGlassPanel}.<br>
	 * 
	 * @return an instance of {@link PRootOverlay} or null if an overlay is not supported
	 * @see PGlassPanel
	 * @see PDnDManager
	 * @see PRootLayout
	 */
	public PRootOverlay getOverlay();
	
	/**
	 * Returns the body {@link PComponent} of this {@link PRoot}, this is usually a 
	 * {@link PPanel} but could be any kind of component or even null.<br>
	 * 
	 * @return the body component of this root or null
	 * @see PRootLayout
	 */
	public PComponent getBody();
	
	/**
	 * Returns the drag and drop manager for this {@link PRoot} or null if drag and drop is 
	 * not supported.<br>
	 * The returned value for this method does never change during the life cycle of this 
	 * {@link PRoot}.<br>
	 * <br>
	 * A drag and drop manager is used to manage the dragging and dropping of a 
	 * {@link PDnDTransfer} after it was dispatched by a {@link PDnDSupport} of a component.<br>
	 * The drag and drop manager is also responsible for adding the visual representation of 
	 * a drag to the {@link PRootOverlay} of this root.<br> 
	 * <br>
	 * It does not make sense to support a drag and drop manager without a {@link PMouse} 
	 * since the mouse is the standard way of controlling a drag and drop action. It is, 
	 * however, possible to subclass the {@link PDnDManager} class to create custom drag 
	 * and drop controls.<br>
	 * 
	 * @return an instance of {@link PDnDManager} or null if drag and drop is not supported
	 * @see PDnDSupport
	 * @see PDnDTransfer
	 * @see #getOverlay()
	 * @see PComponent#getDragAndDropSupport()
	 */
	public PDnDManager getDragAndDropManager();
	
	/**
	 * Returns the {@link PComponent} that currently has the focus within 
	 * the GUI.<br>
	 * This can be null if no component has focus right now.<br>
	 * 
	 * @return the component with focus or null
	 * @see #setFocusOwner(PComponent)
	 */
	public PComponent getFocusOwner();
	
	/**
	 * Sets the focused {@link PComponent} of this GUI to the given component.<br>
	 * If component is null then no component will have focus anymore.<br>
	 * 
	 * @param component the new focused component or null
	 * @see #getFocusOwner()
	 */
	public void setFocusOwner(PComponent component);
	
	/**
	 * Registers the given {@link PTimer} at this root.<br>
	 * A registered Timer will be ticked once every millisecond.<br>
	 * A timer can not be registered more then once.<br>
	 * 
	 * @param timer the timer that is to be registered
	 * @throws NullPointerException if timer is null
	 * @throws IllegalArgumentException if timer was already registered
	 * @see PTimer
	 * @see #unregisterTimer(PTimer)
	 */
	public void registerTimer(PTimer timer) throws NullPointerException;
	
	/**
	 * Unregisters the given {@link PTimer} from this root.<br>
	 * The unregistered timer will no longer be ticked.<br>
	 * If the timer has not been registered before an 
	 * {@link IllegalArgumentException} will be thrown.<br>
	 * 
	 * @param timer the timer that is to be unregistered
	 * @throws NullPointerException if timer is null
	 * @throws IllegalArgumentException if timer was not registered
	 * @see PTimer
	 * @see #registerTimer(PTimer)
	 */
	public void unregisterTimer(PTimer timer) throws NullPointerException;
	
	/**
	 * Registers a {@link PFocusObs} at this {@link PRoot}.<br>
	 * Whenever a {@link PComponent} takes or loses focus all registered focus 
	 * observers will be notified.<br>
	 * 
	 * @param obs the observer to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PComponentObs)
	 * @see #getFocusOwner()
	 * @see #setFocusOwner(PComponent)
	 */
	public void addObs(PFocusObs obs) throws NullPointerException;
	
	/**
	 * Unregisters a {@link PFocusObs} at this {@link PRoot}.<br>
	 * If the observer was not registered before this method does nothing.<br>
	 * 
	 * @param obs the observer to be registered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PComponentObs)
	 * @see #getFocusOwner()
	 * @see #setFocusOwner(PComponent)
	 */
	public void removeObs(PFocusObs obs) throws NullPointerException;
}