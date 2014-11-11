package edu.udo.piq;

import edu.udo.piq.PFontResource.Style;

public interface PDialog extends PRoot {
	
	/**
	 * Makes the {@link PDialog} appear on screen.<br>
	 * 
	 * @throws IllegalStateException if this dialog was already shown or disposed
	 */
	public void show() throws IllegalStateException;
	
	/**
	 * Disposes the {@link PDialog}.<br>
	 * After a dialog was disposed it is no longer visible and can 
	 * not be interacted with.<br>
	 * Any attempt to change the PDialog after it was disposed should 
	 * result in an {@link IllegalStateException}.<br>
	 * A PDialog is usually disposed when the user closes it or when 
	 * it is no longer needed.<br>
	 * After a call to this method any call to {@link #isDisposed()} 
	 * should return true.
	 * 
	 * @throws IllegalStateException if this dialog was already disposed
	 * @see #isDisposed()
	 */
	public void dispose() throws IllegalStateException;
	
	/**
	 * Returns true if this {@link PDialog} was disposed before by the 
	 * use of the {@link #dispose()} method.<br>
	 * The disposal of a PDialog can not be reversed. If this method 
	 * ever returns true it will return true for the rest of the life 
	 * cycle of this object.<br>
	 * 
	 * @return true if this dialog is disposed, otherwise false
	 * @see #dispose()
	 */
	public boolean isDisposed();
	
	/**
	 * Sets the {@link PLayout} of this {@link PDialog} to layout.<br>
	 * Afterwards any future calls to {@link #getLayout()} will return 
	 * the layout given as argument.<br>
	 * 
	 * @param layout the new layout for this dialog
	 * @see #getLayout()
	 * @see PLayout
	 */
	public void setLayout(PLayout layout);
	
	/**
	 * Returns the {@link PBounds} of this {@link PDialog}.<br>
	 * Since a dialog does not have a parent its bounds are not defined 
	 * by a layout, instead the bounds of a dialog should be set directly 
	 * by the user.<br>
	 * 
	 * @return the bounds of this root
	 * @see PBounds
	 */
	public PBounds getBounds();
	
	/**
	 * Returns the {@link PDesignSheet} of the {@link PRoot} that 
	 * created this {@link PDialog}.<br>
	 * 
	 * @return the design sheet for this GUI tree
	 * @see PDesignSheet
	 * @see PDesign
	 * @see PRenderer
	 */
	public PDesignSheet getDesignSheet();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PDialog createDialog();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) throws NullPointerException;
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PImageResource fetchImageResource(String imgPath) throws NullPointerException;
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PMouse getMouse();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PKeyboard getKeyboard();
	
}