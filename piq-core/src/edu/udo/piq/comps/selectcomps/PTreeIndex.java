package edu.udo.piq.comps.selectcomps;

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
	
	public PTreeIndex(int[] childIndices) {
		indices = Arrays.copyOf(childIndices, childIndices.length);
		depth = childIndices.length;
	}
	
	protected PTreeIndex(int[] childIndices, int depth) {
		indices = childIndices;
		this.depth = depth;
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
	
	public PTreeIndex makeParentIndex() {
		return new PTreeIndex(indices, depth - 1);
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
	
}