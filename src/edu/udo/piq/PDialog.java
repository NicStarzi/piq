package edu.udo.piq;

import java.io.InputStream;

import edu.udo.piq.PFontResource.Style;

public interface PDialog extends PRoot {
	
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
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style);
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PImageResource fetchImageResource(InputStream imgStream);
	
	/**
	 * Delegates to the {@link PRoot} that created this {@link PDialog}.<br>
	 */
	public PMouse getMouse();
	
}