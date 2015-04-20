package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import edu.udo.piq.PComponent;

public class PGuiTreeIterator implements Iterator<PComponent> {
	
	private final Deque<PComponent> stack = new ArrayDeque<>();
	
	public PGuiTreeIterator(PComponent root) {
		stack.push(root);
	}
	
	public boolean hasNext() {
		return !stack.isEmpty();
	}
	
	public PComponent next() {
		PComponent cmp = stack.pop();
		for (PComponent child : cmp.getChildren()) {
			stack.push(child);
		}
		return cmp;
	}
	
}