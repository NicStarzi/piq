package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.List;

public interface PDropComponent extends PSelectionComponent {
	
	/**
	 * Returns an instance of {@link PModelIndex} for which 
	 * {@link PModel#canAdd(PModelIndex, Object)} returns true 
	 * for the {@link PModel} returned by {@link #getModel()}, 
	 * or null if dropping is not possible at this location.<br>
	 * @param x		the x-coordinate on screen
	 * @param y		the y-coordinate on screen
	 * @return		an instance of {@link PModelIndex} or null
	 */
	public PModelIndex getDropIndex(int x, int y);
	
	/**
	 * Highlights the given {@link PModelIndex} in some way to 
	 * indicate to the user that a drop is possible at this location.<br>
	 * If index is null then no drop highlight should be shown.<br>
	 * @param index		a valid instance of {@link PModelIndex} or null
	 */
	public void setDropHighlight(PModelIndex index);
	
	/**
	 * Returns a list of {@link PModelIndex PModelIndices} that are to be 
	 * dragged in a drag-and-drop action. If dragging is currently not 
	 * possible for any reason, perhaps because this component does not 
	 * have a model at the moment, <b><u>null</u> is returned instead of 
	 * an empty List.</b><br>
	 * The default implementation of this method returns all the selected 
	 * indices of the current {@link PSelection selection} of this component, 
	 * or null if {@link #getSelection()} returns null.<br>
	 * 
	 * @return a List of {@link PModelIndex PModelIndices} or null
	 * @see #getSelection()
	 */
	public default List<PModelIndex> getDragIndices() {
		PSelection selection = getSelection();
		if (selection == null) {
			return null;
		}
		return new ArrayList<>(getSelection().getAllSelected());
	}
	
//	public PModel getDragModel();
	
}