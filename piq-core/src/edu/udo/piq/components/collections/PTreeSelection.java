package edu.udo.piq.components.collections;

public interface PTreeSelection extends PSelection {
	
	public PTreeIndex getLastSelected();
	
	public default PTreeIndex asTreeIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTreeIndex) {
			return (PTreeIndex) index;
		}
		throw new WrongIndexType(index, PTreeIndex.class);
	}
	
}