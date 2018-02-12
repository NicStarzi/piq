package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import edu.udo.piq.PComponent;

public class BreadthFirstDescendantIterator implements Iterator<PComponent>, Iterable<PComponent> {
	
	private final Deque<PComponent> stack = new ArrayDeque<>();
	private final PComponent root;
	
	public BreadthFirstDescendantIterator(PComponent rootComp) {
		root = rootComp;
		stack.push(root);
	}
	
	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}
	
	@Override
	public PComponent next() {
		PComponent cmp = stack.pop();
		for (PComponent child : cmp.getChildren()) {
			stack.addLast(child);
		}
		return cmp;
	}
	
	@Override
	public Iterator<PComponent> iterator() {
		if (stack.peek() != root) {
			return new BreadthFirstDescendantIterator(root);
		}
		return this;
	}
	
}
