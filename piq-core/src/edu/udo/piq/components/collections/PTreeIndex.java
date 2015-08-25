package edu.udo.piq.components.collections;

import java.util.Arrays;
import java.util.Collection;

public class PTreeIndex implements PModelIndex {
	
	private static final int[] ROOT_ARR = new int[0];
	
	private final int[] indices;
	private final int depth;
	
	public PTreeIndex(Collection<Integer> childIndices) {
		indices = new int[childIndices.size()];
		depth = indices.length;
		
		int id = 0;
		for (Integer i : childIndices) {
			indices[id++] = i.intValue();
		}
	}
	
	public PTreeIndex() {
		this(ROOT_ARR, 0);
	}
	
	public PTreeIndex(int ... childIndices) {
		indices = Arrays.copyOf(childIndices, childIndices.length);
		depth = childIndices.length;
	}
	
	protected PTreeIndex(int[] childIndices, int depth) {
		indices = Arrays.copyOf(childIndices, depth);
		this.depth = indices.length;
//		this.depth = depth;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getChildIndex(int level) {
		if (level < 0 || level >= getDepth()) {
			throw new IndexOutOfBoundsException("level="+level+", max="+getDepth());
		}
		return indices[level];
	}
	
	public int getLastIndex() {
		if (getDepth() == 0) {
			return -1;
		}
		return indices[depth - 1];
	}
	
	public PTreeIndex makeParentIndex() {
		return new PTreeIndex(indices, depth - 1);
	}
	
	public PTreeIndex replaceIndex(int indexAt, int newValue) {
		int[] childIndices = Arrays.copyOf(indices, depth);
		childIndices[indexAt] = newValue;
		return new PTreeIndex(childIndices);
	}
	
	public PTreeIndex append(int childIndex) {
		int oldDepth = getDepth();
		int newDepth = oldDepth + 1;
		int[] newIndices = Arrays.copyOf(indices, newDepth);
		newIndices[oldDepth] = childIndex;
		return new PTreeIndex(newIndices);
	}
	
	public PTreeIndex append(int ... childIndices) {
		int oldDepth = getDepth();
		int newDepth = oldDepth + childIndices.length;
		int[] newIndices = Arrays.copyOf(indices, newDepth);
		System.arraycopy(childIndices, 0, newIndices, oldDepth, childIndices.length);
		return new PTreeIndex(newIndices);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(Arrays.toString(indices));
		return sb.toString();
	}
	
	public int hashCode() {
		return Arrays.hashCode(indices);
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof PTreeIndex))
			return false;
		PTreeIndex other = (PTreeIndex) obj;
		return Arrays.equals(indices, other.indices);
	}
	
}