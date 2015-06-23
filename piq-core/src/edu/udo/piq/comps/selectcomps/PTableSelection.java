package edu.udo.piq.comps.selectcomps;

public interface PTableSelection extends PSelection {
	
	public PTableIndex getLastSelected();
	
	public default PTableIndex asTableIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTableIndex) {
			return (PTableIndex) index;
		}
		throw new WrongIndexType(index, PTableIndex.class);
	}	
}