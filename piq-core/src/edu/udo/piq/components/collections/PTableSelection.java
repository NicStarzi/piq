package edu.udo.piq.components.collections;

public interface PTableSelection extends PSelection {
	
	public PTableCellIndex getLastSelected();
	
	public default PTableCellIndex asTableIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTableCellIndex) {
			return (PTableCellIndex) index;
		}
		throw new WrongIndexType(index, PTableCellIndex.class);
	}	
}