package edu.udo.piq.comps.selectcomps;

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
	
}