package edu.udo.piq.tools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.util.PCompUtil;

public class ReRenderSet implements Iterable<PComponent> {
	
	private final PRoot root;
	/**
	 * This is a {@link LinkedList} because we need to clear it 
	 * regularly and delete elements at arbitrary indices.<br>
	 */
	private final List<PComponent> buffer = new LinkedList<>();
	private boolean containsRoot = false;
	
	public ReRenderSet(PRoot root) {
		this.root = root;
	}
	
	public boolean add(PComponent component) {
		if (containsRoot) {
			 return false;
		}
		while (component != root && !PCompUtil.fillsAllPixels(component)) {
			component = component.getParent();
		}
		if (root == component) {
			buffer.clear();
			containsRoot = true;
			return buffer.add(component);
		} else {
			int compDepth = component.getDepth();
			
			Iterator<PComponent> iter = buffer.iterator();
			while (iter.hasNext()) {
				PComponent top = iter.next();
				if (top == component) {
					return false;
				}
				int topDepth = top.getDepth();
				if (topDepth < compDepth) {
					if (component.isDescendantOf(top)) {
						return false;
					}
				} else if (topDepth > compDepth) {
					if (top.isDescendantOf(component)) {
						iter.remove();
					}
				}
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
	
	public PComponent[] toArray() {
		return buffer.toArray(new PComponent[buffer.size()]);
	}
	
	public Iterator<PComponent> iterator() {
		return buffer.iterator();
	}
	
}