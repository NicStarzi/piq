package edu.udo.piq.tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public class ReRenderSet implements Iterable<PComponent> {
	
	protected final PRoot root;
	protected final List<PComponent> buffer = createBufferImplementation();
	protected boolean containsRoot = false;
	
	public ReRenderSet(PRoot root) {
		this.root = root;
	}
	
	public boolean add(PComponent component) {
		if (containsRoot) {
			 return false;
		}
		while (component != root && !component.fillsAllPixels()) {
			component = component.getParent();
		}
		if (root == component) {
			buffer.clear();
			containsRoot = true;
			return buffer.add(component);
		} else {
//			int compDepth = component.getDepth();
			
			Iterator<PComponent> iter = buffer.iterator();
			while (iter.hasNext()) {
				PComponent top = iter.next();
				if (top == component) {
					return false;
				}
//				int topDepth = top.getDepth();
//				if (topDepth < compDepth) {
					if (top.isAncestorOf(component)) {
						return false;
					}
//				} else if (topDepth > compDepth) {
					if (component.isAncestorOf(top)) {
						iter.remove();
					}
//				}
			}
			return buffer.add(component);
		}
	}
	
	public boolean remove(PComponent component) {
		boolean removed = buffer.remove(component);
		if (removed && component == root) {
			containsRoot = false;
		}
		return removed;
	}
	
	public void clear() {
		buffer.clear();
		containsRoot = false;
	}
	
	public boolean contains(PComponent component) {
		return buffer.contains(component);
	}
	
	public boolean containsRoot() {
		return containsRoot;
	}
	
	public int getSize() {
		return buffer.size();
	}
	
	public boolean isEmpty() {
		return buffer.isEmpty();
	}
	
	public PComponent[] toArray() {
		return buffer.toArray(new PComponent[buffer.size()]);
	}
	
	@Override
	public Iterator<PComponent> iterator() {
		return buffer.iterator();
	}
	
	/**
	 * This is a {@link LinkedList} because we often need to
	 * delete elements at arbitrary indices.<br>
	 * @return a new {@link LinkedList}
	 */
	protected List<PComponent> createBufferImplementation() {
		return new LinkedList<>();
	}
	
}