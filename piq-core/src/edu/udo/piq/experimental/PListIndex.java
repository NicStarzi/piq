package edu.udo.piq.experimental;

public class PListIndex implements PModelIndex {
	
	private final int index;
	
	public PListIndex(int indexValue) {
		index = indexValue;
	}
	
	public int getIndexValue() {
		return index;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getIndexValue());
		sb.append("]");
		return sb.toString();
	}
	
}