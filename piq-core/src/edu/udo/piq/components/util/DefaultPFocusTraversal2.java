package edu.udo.piq.components.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public class DefaultPFocusTraversal2 implements PFocusTraversal {
	
	private final PComponent owner;
	
	public DefaultPFocusTraversal2(PComponent component) {
		owner = component;
	}
	
	public PRoot getRoot() {
		return owner.getRoot();
	}
	
	public void focusNext() {
		PComponent focusOwner = getRoot().getFocusOwner();
		if (focusOwner == null) {
			System.out.println("DefaultPFocusTraversal2.focusNext=INITIAL");
			focusInitial();
			return;
		}
		PComponent focusChild = getFocusChild(focusOwner);
		if (focusChild != null) {
			System.out.println("DefaultPFocusTraversal2.focusNext="+focusChild);
			focusChild.takeFocus();
			return;
		}
		PComponent newFocusOwner = null;
		while (newFocusOwner == null && focusOwner.getParent() != null) {
			newFocusOwner = getFocusSibbling(focusOwner);
			focusOwner = focusOwner.getParent();
		}
		if (newFocusOwner == null || focusOwner.getParent() == null) {
			System.out.println("DefaultPFocusTraversal2.focusNext=NULL");
			focusInitial();
		} else {
			System.out.println("DefaultPFocusTraversal2.focusNext="+newFocusOwner);
			newFocusOwner.takeFocus();
		}
	}
	
	public void focusPrevious() {
	}
	
	protected PComponent getFocusChild(PComponent focusOwner) {
		return iterate(focusOwner);
	}
	
	protected PComponent getFocusSibbling(PComponent focusOwner) {
		PComponent parent = focusOwner.getParent();
		System.out.println("getFocusSibbling.current="+focusOwner+", parent="+parent);
		boolean selectNext = false;
		for (PComponent child : parent.getChildren()) {
			System.out.println("child="+child+", focusOwner="+focusOwner+", select="+selectNext);
			if (selectNext) {
				System.out.println("focusable="+child.isFocusable());
				if (child.isFocusable()) {
					return child;
				}
				PComponent grandChild = iterate(child);
				System.out.println("grandChild="+grandChild);
				if (grandChild != null) {
					return grandChild;
				}
			} else if (child == focusOwner) {
				selectNext = true;
			}
		}
		return null;
	}
	
	protected PComponent iterate(PComponent parent) {
		Collection<PComponent> children = parent.getChildren();
		if (children.isEmpty()) {
			return null;
		}
		Deque<PComponent> stack = new ArrayDeque<>();
		for (PComponent child : children) {
			stack.addLast(child);
		}
		while (!stack.isEmpty()) {
			PComponent current = stack.removeFirst();
			System.out.println("current="+current);
			if (current.isFocusable()) {
				return current;
			}
			for (PComponent child : current.getChildren()) {
				stack.addFirst(child);
			}
		}
		return null;
	}
	
	protected void focusInitial() {
		PComponent focusOwner = iterate(getRoot());
		if (focusOwner != null) {
			focusOwner.takeFocus();
		}
	}
}