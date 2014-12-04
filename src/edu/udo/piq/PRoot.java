package edu.udo.piq;

import edu.udo.piq.PFontResource.Style;

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
	public PRoot getRoot();
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public void setParent(PComponent parent) throws UnsupportedOperationException;
	
	/**
	 * Returns null.<br>
	 * @return null
	 */
	public PComponent getParent();
	
	/**
	 * Throws an {@link UnsupportedOperationException}.<br>
	 * @throws UnsupportedOperationException
	 */
	public void defaultRender(PRenderer renderer) throws UnsupportedOperationException;
	
	/**
	 * Always returns true for {@link PRoot} instances.<br>
	 * @return true
	 */
	public boolean isDefaultOpaque();
	
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
	 * desires font defined by the name, size and style.<br>
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
	 */
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) throws NullPointerException;
	
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
	 * @throws NullPointerException if imgStream is null
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
	 * @throws IllegalStateException if a component already has the focus and the parameter is not null
	 * @see #getFocusOwner()
	 */
	public void setFocusOwner(PComponent component) throws IllegalStateException;
}