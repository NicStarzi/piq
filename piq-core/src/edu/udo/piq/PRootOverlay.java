package edu.udo.piq;

import edu.udo.piq.components.containers.PGlassPanel;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PReadOnlyLayout;

/**
 * A {@link PRootOverlay} is part of a {@link PRoot} and is used to draw 
 * visual effects on top of the GUI.<br>
 * Examples are drop down menus, tool tips or visual representations of a 
 * drag and drop.<br>
 * The {@link PRootOverlay} interface does not extend {@link PComponent} 
 * but its safe to assume that the implementing class is a 
 * {@link PComponent} because the {@link PReadOnlyLayout} returned by the 
 * {@link #getLayout()} requires an owner as defined by 
 * {@link PReadOnlyLayout#getOwner()}.<br>
 * <br>
 * A default implementation of {@link PRootOverlay} is the 
 * {@link PGlassPanel} which is recommended to be as the root overlay in 
 * custom root implementations.<br>
 * 
 * @see PRoot#getOverlay()
 * @see PDnDManager#startDrag(PDnDTransfer)
 */
public interface PRootOverlay {
	
	/**
	 * Returns the {@link PReadOnlyLayout} for this overlay which is always a {@link PFreeLayout} 
	 * or a subclass of it.<br>
	 * The returned layout will never change over the life cycle of an overlay and is never 
	 * null.<br>
	 * Please note, that the returned layout needs a non null owner which could be this 
	 * root overlay or an other component used as a delegate. The parent of the layouts 
	 * owner is always the root though.<br>
	 * 
	 * @return a layout of type {@link PFreeLayout} or a subclass of it
	 */
	public PFreeLayout getLayout();
	
	public default Iterable<PComponent> getChildren() {
		return getLayout().getChildren();
	}
	
	/**
	 * Returns the {@link PBounds} for this overlay. This might be useful to certain components 
	 * that make use of the overlay and try to stay within the visible area. Usually these bounds 
	 * will be equal to the bounds of the root but implementations are free to differ from this 
	 * norm.<br>
	 * The returned bounds are never null.<br>
	 * 
	 * @return the current {@link PBounds} of the {@link PRootOverlay}
	 */
	public PBounds getBounds();
	
}