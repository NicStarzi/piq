package edu.udo.piq.components.collections;

import java.util.Arrays;
import java.util.Collection;

import edu.udo.piq.util.ThrowException;

public class PTreeIndex implements PModelIndex {
	
	private static final int[] ROOT_ARR = new int[0];
	
	private final int[] indices;
	private final int depth;
	
	public PTreeIndex(Collection<Integer> childIndices) {
		// Do not copy empty collections
		if (childIndices.size() == 0) {
			indices = ROOT_ARR;
		} else {
			indices = new int[childIndices.size()];
			int id = 0;
			for (Integer i : childIndices) {
				indices[id++] = i.intValue();
			}
		}
		depth = indices.length;
	}
	
	public PTreeIndex() {
		this(ROOT_ARR, 0);
	}
	
	public PTreeIndex(int ... childIndices) {
		// Do not copy empty arrays
		if (childIndices.length == 0) {
			indices = ROOT_ARR;
		} else {
			indices = Arrays.copyOf(childIndices, childIndices.length);
		}
		depth = childIndices.length;
	}
	
	protected PTreeIndex(int[] childIndices, int depth) {
		// Do not copy empty arrays
		if (depth == 0) {
			indices = ROOT_ARR;
		} else {
			indices = Arrays.copyOf(childIndices, depth);
		}
		this.depth = indices.length;
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
	
	public PTreeIndex createCommonAncestorIndex(PTreeIndex other) {
		int[] indicesThis = indices;
		int[] indicesOther = other.indices;
		int searchLength = Math.min(indicesThis.length, indicesOther.length);
		int commonLength = 0;
		for (int i = 0; i < searchLength; i++) {
			if (indicesThis[i] != indicesOther[i]) {
				break;
			}
			commonLength++;
		}
		return new PTreeIndex(indicesThis, commonLength);
	}
	
	public PTreeIndex createParentIndex() {
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
		return append(childIndices.length, childIndices);
	}
	
	public PTreeIndex append(int length, int[] childIndices) {
		int oldDepth = getDepth();
		int newDepth = oldDepth + length;
		int[] newIndices = Arrays.copyOf(indices, newDepth);
		System.arraycopy(childIndices, 0, newIndices, oldDepth, length);
		return new PTreeIndex(newIndices);
	}
	
	public PTreeIndex append(PTreeIndex index) {
		return append(index.depth, index.indices);
	}
	
	public PTreeIndex withOffset(PModelIndex offset) {
		PTreeIndex offsetTree = ThrowException.ifTypeCastFails(offset, 
				PTreeIndex.class, "(offset instanceof PTreeIndex) == false");
		return offsetTree.append(this);
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