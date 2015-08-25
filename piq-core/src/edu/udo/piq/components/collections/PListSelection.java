package edu.udo.piq.components.collections;

public interface PListSelection extends PSelection {
	
	public PListIndex getLastSelected();
	
	public default int asListIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PListIndex) {
			return ((PListIndex) index).getIndexValue();
		}
		throw new WrongIndexType(index, PListIndex.class);
	}
	
}