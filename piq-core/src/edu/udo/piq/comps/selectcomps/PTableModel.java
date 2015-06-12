package edu.udo.piq.comps.selectcomps;

public interface PTableModel extends PModel {
	
	public int getColumnCount();
	
	public int getRowCount();
	
	public Object get(int column, int row);
	
	public boolean contains(int column, int row);
	
	public boolean canAdd(int column, int row, Object content);
	
	public void add(int column, int row, Object content);
	
	public boolean canRemove(int column, int row);
	
	public void remove(int column, int row);
	
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