package edu.udo.piq.components.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import edu.udo.piq.util.ThrowException;

public class PTreeIndex implements PModelIndex {
	
	public static final Comparator<PModelIndex> TREE_INDEX_DEPTH_COMPARATOR = 
			(PModelIndex o1, PModelIndex o2) -> {
				PTreeIndex ti1 = (PTreeIndex) o1;
				PTreeIndex ti2 = (PTreeIndex) o2;
				int depthCompare = ti1.getDepth() - ti2.getDepth();
				if (depthCompare == 0) {
					return ti1.getLastIndex() - ti2.getLastIndex();
				}
				return depthCompare;
			};
	
	private static final int[] ROOT_ARR = new int[0];
	public static final PTreeIndex ROOT = new PTreeIndex();
	
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
//		if (depth == 0) {
//			indices = ROOT_ARR;
//		} else {
			indices = childIndices;//Arrays.copyOf(childIndices, depth);
//		}
		this.depth = depth;//indices.length;
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
	
	public PTreeIndex getCommonAncestorIndex(PTreeIndex other) {
		if (this == other || this.equals(other)) {
			return this;
		}
		int[] indicesThis = indices;
		int[] indicesOther = other.indices;
		int searchLength = Math.min(getDepth(), other.getDepth());
		int commonLength = 0;
		for (int i = 0; i < searchLength; i++) {
			if (indicesThis[i] != indicesOther[i]) {
				break;
			}
			commonLength++;
		}
		if (commonLength == getDepth()) {
			return this;
		}
		if (commonLength == other.getDepth()) {
			return other;
		}
		return new PTreeIndex(indicesThis, commonLength);
	}
	
	public boolean isAncestorOf(PTreeIndex maybeDescendant) {
		int depth = getDepth();
		if (maybeDescendant.getDepth() < depth) {
			return false;
		}
		for (int i = 0; i < depth; i++) {
			if (getChildIndex(i) != maybeDescendant.getChildIndex(i)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isDescendantOf(PTreeIndex maybeAncestor) {
		return maybeAncestor.isAncestorOf(this);
	}
	
	public PTreeIndex createParentIndex() {
		if (getDepth() == 0) {
			return null;
		}
		if (getDepth() == 1) {
			return ROOT;
		}
		return new PTreeIndex(indices, depth - 1);
	}
	
	public PTreeIndex replaceLastIndex(int index) {
		return replaceIndex(getDepth() - 1, index);
	}
	
	public PTreeIndex replaceIndex(int level, int index) {
		if (getChildIndex(level) == index) {
			return this;
		}
		int[] childIndices = Arrays.copyOf(indices, depth);
		childIndices[level] = index;
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
		return append(childIndices, childIndices.length);
	}
	
	public PTreeIndex append(int[] childIndices, int from, int length) {
		int oldDepth = getDepth();
		int newDepth = oldDepth + length;
		int[] newIndices = Arrays.copyOf(indices, newDepth);
		System.arraycopy(childIndices, from, newIndices, oldDepth, length);
		return new PTreeIndex(newIndices);
	}
	
	public PTreeIndex append(int[] childIndices, int length) {
		return append(childIndices, 0, length);
	}
	
	public PTreeIndex append(PTreeIndex index) {
		return append(index.indices, index.getDepth());
	}
	
	public PTreeIndex append(PTreeIndex index, int from) {
		return append(index, from, index.getDepth());
	}
	
	public PTreeIndex append(PTreeIndex index, int from, int depth) {
		return append(index.indices, from, depth - from);
	}
	
	public PTreeIndex insertIndex(int level, int index) {
		int oldDepth = getDepth();
		int newDepth = oldDepth + 1;
		int[] newIndices = new int[newDepth];
		if (level == 0) {
			System.arraycopy(indices, 0, newIndices, 1, indices.length);
		} else if (level == oldDepth) {
			System.arraycopy(indices, 0, newIndices, 0, indices.length);
		} else {
			System.arraycopy(indices, 0, newIndices, 0, level);
			System.arraycopy(indices, level, newIndices, level + 1, indices.length - level);
		}
		newIndices[level] = index;
		return new PTreeIndex(newIndices);
	}
	
	public PTreeIndex withOffset(PModelIndex offset) {
		PTreeIndex offsetTree = ThrowException.ifTypeCastFails(offset, 
				PTreeIndex.class, "(offset instanceof PTreeIndex) == false");
		return offsetTree.append(this);
	}
	
	public String toString() {
		if (getDepth() == 0) {
			return getClass().getSimpleName() + "[ROOT]";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		for (int i = 0; i < getDepth() - 1; i++) {
			sb.append(getChildIndex(i));
			sb.append(", ");
		}
		if (getDepth() > 0) {
			sb.append(getLastIndex());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < getDepth(); i++) {
			hash = hash * 31 + getChildIndex(i);
		}
		return hash;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof PTreeIndex))
			return false;
		PTreeIndex other = (PTreeIndex) obj;
		if (getDepth() != other.getDepth()) {
			return false;
		}
		for (int i = 0; i < getDepth(); i++) {
			if (getChildIndex(i) != other.getChildIndex(i)) {
				return false;
			}
		}
		return true;
	}
	
}