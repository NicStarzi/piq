package edu.udo.piq.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.PComponent;

public class AncestorIterator implements Iterator<PComponent>, Iterable<PComponent> {
	
	private final PComponent original;
	private PComponent current;
	
	public AncestorIterator(PComponent component, boolean includeSelf) {
		if (includeSelf) {
			current = component;
		} else {
			current = component.getParent();
		}
		original = current;
	}
	
	public boolean hasNext() {
		return current != null;
	}
	
	public PComponent next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		PComponent next = current;
		current = current.getParent();
		return next;
	}
	
	public Iterator<PComponent> iterator() {
		if (current == original) {
			return this;
		}
		return new AncestorIterator(original, true);
	}
}