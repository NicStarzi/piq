package edu.udo.piq;

import edu.udo.piq.PFontResource.Style;

public interface PDialog extends PRoot, PDisposable {
	
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
	@Override
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
	 * Returns the {@link PBounds} of this {@link PDialog}.<br>
	 * Since a dialog does not have a parent its bounds are not defined
	 * by a layout, instead the bounds of a dialog should be set directly
	 * by the user.<br>
	 * 
	 * @return the bounds of this root
	 * @see PBounds
	 */
	@Override
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
	@Override
	public PStyleSheet getStyleSheet();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	@Override
	public PDialog createDialog();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	@Override
	public PFontResource fetchFontResource(String fontName, double pointSize, Style style) throws NullPointerException;
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	@Override
	public PImageResource fetchImageResource(Object imageID) throws NullPointerException;
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	@Override
	public PMouse getMouse();
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	@Override
	public PKeyboard getKeyboard();
	
}