package edu.udo.piq.components.collections;

public class PListIndex implements PModelIndex {
	
	private final int index;
	
	public PListIndex(int indexValue) {
		if (indexValue < 0) {
			throw new IllegalArgumentException("indexValue="+indexValue);
		}
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
	
	public int hashCode() {
		return index;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof PListIndex) {
			return index == ((PListIndex) obj).index;
		}
		return false;
	}
	
}