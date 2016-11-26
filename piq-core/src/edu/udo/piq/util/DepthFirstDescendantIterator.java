package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import edu.udo.piq.PComponent;

public class DepthFirstDescendantIterator implements Iterator<PComponent>, Iterable<PComponent> {
	
	private final Deque<PComponent> stack = new ArrayDeque<>();
	private final PComponent root;
	
	public DepthFirstDescendantIterator(PComponent rootComp) {
		root = rootComp;
		stack.push(root);
	}
	
	public boolean hasNext() {
		return !stack.isEmpty();
	}
	
	public PComponent next() {
		PComponent cmp = stack.pop();
		for (PComponent child : cmp.getChildren()) {
			stack.addFirst(child);
		}
		return cmp;
	}
	
	public Iterator<PComponent> iterator() {
		if (stack.peek() != root) {
			return new DepthFirstDescendantIterator(root);
		}
		return this;
	}
	
}