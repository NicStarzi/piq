package edu.udo.piq;

import edu.udo.piq.components.PGlassPanel;
import edu.udo.piq.layouts.PFreeLayout;

/**
 * A {@link PRootOverlay} is part of a {@link PRoot} and is used to draw 
 * visual effects on top of the GUI.<br>
 * Examples are drop down menus, tooltips or visual representations of a 
 * drag and drop.<br>
 * The {@link PRootOverlay} interface does not extend {@link PComponent} 
 * but its safe to assume that the implementing class is a 
 * {@link PComponent} because the {@link PLayout} returned by the 
 * {@link #getLayout()} requires an owner as defined by 
 * {@link PLayout#getOwner()}.<br>
 * <br>
 * A default implementation of {@link PRootOverlay} is the 
 * {@link PGlassPanel} which is recommended to be used with the 
 * {@link PRoot#getOverlay()} method.<br>
 * 
 * @see PRoot#getOverlay()
 * @see PDnDManager#startDrag(PDnDTransfer)
 */
public interface PRootOverlay {
	
	/**
	 * Returns the {@link PLayout} for this overlay which is always a {@link PFreeLayout} 
	 * or a subclass of it.<br>
	 * The returned layout will never change over the life cycle of an overlay and is never 
	 * null.<br>
	 * 
	 * @return a layout of type {@link PFreeLayout} or a subclass of it
	 */
	public PFreeLayout getLayout();
	
}