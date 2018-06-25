package edu.udo.piq.components.collections.list;

import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.util.ThrowException;

public class PListIndex implements PModelIndex, Comparable<PListIndex> {
	
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
	
	@Override
	public PListIndex withOffset(PModelIndex offset) {
		PListIndex offsetList = ThrowException.ifTypeCastFails(offset, 
				PListIndex.class, "(offset instanceof PListIndex) == false");
		int indexValue = offsetList.getIndexValue() + getIndexValue();
		return new PListIndex(indexValue);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append(getIndexValue());
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return getIndexValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof PListIndex) {
			return getIndexValue() == ((PListIndex) obj).getIndexValue();
		}
		return false;
	}
	
	@Override
	public int compareTo(PListIndex other) {
		return Integer.compare(getIndexValue(), other.getIndexValue());
	}
	
}