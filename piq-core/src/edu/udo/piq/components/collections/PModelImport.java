package edu.udo.piq.components.collections;

public interface PModelImport {
	
	public boolean canImportData(PModel dst, PModelIndex dstIndex, PModel src);
	
	/**
	 * Imports all data from the source model to the destination model 
	 * starting at the destination index.<br>
	 * This method assumes that the destination index is compatible 
	 * with the destination model. The source model may be incompatible 
	 * with the destination model in which case the model data will be 
	 * transformed internally.<br>
	 * @param dst			a non-null {@link PModel} used as the destination
	 * @param dstIndex		a non-null {@link PModelIndex} which is compatible with the destination model
	 * @param src			a non-null {@link PModel} used as the source
	 */
	public void importData(PModel dst, PModelIndex dstIndex, PModel src);
	
}