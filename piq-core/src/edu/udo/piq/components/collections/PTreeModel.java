package edu.udo.piq.components.collections;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public interface PTreeModel extends PModel {
	
	public Object getRoot();
	
	public int getChildCount(PTreeIndex index);
	
	public default Iterator<PModelIndex> iterator() {
		return new TreeModelIterator(this, PTreeIndex.ROOT);
	}
	
	public default AncestorIterator createAncestorIterator(PTreeIndex from) {
		return new AncestorIterator(from);
	}
	
	public default NoRootBreadthOrderIterator createNoRootBreadthOrderIterator() {
		return createNoRootBreadthOrderIterator(PTreeIndex.ROOT);
	}
	
	public default NoRootBreadthOrderIterator createNoRootBreadthOrderIterator(PTreeIndex from) {
		return new NoRootBreadthOrderIterator(this, from);
	}
	
	public default BreadthOrderIterator createBreadthOrderIterator() {
		return createBreadthOrderIterator(PTreeIndex.ROOT);
	}
	
	public default BreadthOrderIterator createBreadthOrderIterator(PTreeIndex from) {
		return new BreadthOrderIterator(this, from);
	}
	
	public default PostOrderIterator createPostOrderIterator() {
		return createPostOrderIterator(PTreeIndex.ROOT);
	}
	
	public default PostOrderIterator createPostOrderIterator(PTreeIndex from) {
		return new PostOrderIterator(this, from);
	}
	
	public static class PostOrderIterator implements Iterator<PTreeIndex>, Iterable<PTreeIndex> {
		
		protected final Deque<PTreeIndex> stack = new ArrayDeque<>();
		protected final PTreeModel model;
		protected PTreeIndex lastParent;
		
		public PostOrderIterator(PTreeModel model, PTreeIndex root) {
			this.model = model;
			if (root != null && model.contains(root)) {
				stack.add(root);
			}
		}
		
		public boolean hasNext() {
			return !stack.isEmpty();
		}
		
		public PTreeIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			PTreeIndex next = null;
			while (next == null) {
				PTreeIndex current = stack.pollFirst();
				if (current.equals(lastParent)) {
					next = current;
					lastParent = current.createParentIndex();
				} else {
					int childCount = model.getChildCount(current);
					if (childCount == 0) {
						next = current;
					} else {
						stack.addFirst(current);
						lastParent = current;
						for (int i = 0; i < childCount; i++) {
							stack.addFirst(current.append(i));
						}
					}
				}
			}
			return next;
		}
		
		public Iterator<PTreeIndex> iterator() {
			return this;
		}
		
	}
	
	public static class AncestorIterator implements Iterator<PTreeIndex>, Iterable<PTreeIndex> {
		
		protected PTreeIndex current;
		
		public AncestorIterator(PTreeIndex root) {
			if (root != null) {
				current = root.createParentIndex();
			}
		}
		
		public boolean hasNext() {
			return current != null;
		}
		
		public PTreeIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			PTreeIndex next = current;
			current = current.createParentIndex();
			return next;
		}
		
		public Iterator<PTreeIndex> iterator() {
			return this;
		}
		
	}
	
	public static class NoRootBreadthOrderIterator implements Iterator<PTreeIndex>, Iterable<PTreeIndex> {
		
		protected final Deque<PTreeIndex> stack = new ArrayDeque<>();
		protected final PTreeModel model;
		
		public NoRootBreadthOrderIterator(PTreeModel model, PTreeIndex root) {
			this.model = model;
			if (root != null && model.contains(root)) {
				for (int i = 0; i < model.getChildCount(root); i++) {
					stack.addLast(root.append(i));
				}
			}
		}
		
		protected NoRootBreadthOrderIterator(PTreeModel model) {
			this.model = model;
		}
		
		public boolean hasNext() {
			return !stack.isEmpty();
		}
		
		public PTreeIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			PTreeIndex next = stack.pop();
			for (int i = 0; i < model.getChildCount(next); i++) {
				stack.addLast(next.append(i));
			}
			return next;
		}
		
		public Iterator<PTreeIndex> iterator() {
			return this;
		}
		
	}
	
	public static class BreadthOrderIterator 
		extends NoRootBreadthOrderIterator 
		implements Iterator<PTreeIndex> 
	{
		
		public BreadthOrderIterator(PTreeModel model, PTreeIndex root) {
			super(model);
			if (root != null && model.contains(root)) {
				stack.add(root);
			}
		}
		
	}
	
	public static class TreeModelIterator implements Iterator<PModelIndex> {
		
		protected final Deque<PTreeIndex> stack = new ArrayDeque<>();
		protected final PTreeModel model;
		
		public TreeModelIterator(PTreeModel model, PTreeIndex root) {
			this.model = model;
			if (root != null && model.contains(root)) {
				stack.addLast(root);
			}
		}
		
		public boolean hasNext() {
			return !stack.isEmpty();
		}
		
		public PModelIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			PTreeIndex next = stack.pop();
			for (int i = 0; i < model.getChildCount(next); i++) {
				stack.addLast(next.append(i));
			}
			return next;
		}
		
	}
	
}