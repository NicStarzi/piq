//package edu.udo.piq.components.util;
//
//import java.util.ArrayDeque;
//import java.util.ArrayList;
//import java.util.Deque;
//import java.util.List;
//import java.util.ListIterator;
//
//import edu.udo.piq.PComponent;
//import edu.udo.piq.PRoot;
//
//public class DefaultPFocusTraversal implements PFocusTraversal {
//
//	private final PComponent owner;
//
//	public DefaultPFocusTraversal(PComponent component) {
//		owner = component;
//	}
//
//	public PRoot getRoot() {
//		return owner.getRoot();
//	}
//
//	public void focusNext() {
//		PComponent focusOwner = getRoot().getFocusOwner();
//		if (focusOwner == null) {
//			focusInitial();
//			return;
//		}
//		PComponent parent = focusOwner;
//		PComponent current = null;
//		PComponent child = findNextChild(parent, current);
//		while (child == null) {
////			System.out.println();
////			System.out.println("--- GO UP");
//			current = parent;
//			parent = parent.getParent();
//			if (parent == null) {
////				System.out.println("no more parents");
////				System.out.println();
//				focusInitial();
//				return;
//			}
//			child = findNextChild(parent, current);
//		}
//		child.takeFocus();
//	}
//
//	protected PComponent findNextChild(PComponent parent, PComponent current) {
////		System.out.println();
////		System.out.println("parent="+parent);
////		System.out.println("current="+current);
//		boolean foundCurrent = current == null;
//		for (PComponent sibbling : parent.getChildren()) {
////			System.out.println("sibbling="+sibbling);
//			if (foundCurrent) {
//				if (sibbling.isFocusable()) {
//					return sibbling;
//				} else {
//					if (!sibbling.getChildren().isEmpty()) {
////						System.out.println("--- GO DOWN");
//						PComponent deepChild = findNextChild(sibbling, null);
//						if (deepChild != null) {
//							return deepChild;
//						}
//					}
//				}
//			} else if (sibbling == current) {
//				foundCurrent = true;
//			}
//		}
//		return null;
//	}
//
//	public void focusPrevious() {
//		focusPrevious(getRoot().getFocusOwner());
//	}
//
//	protected void focusPrevious(PComponent current) {
//		PComponent parent = current.getParent();
//		if (parent == null) {
//			focusInitial();
//			return;
//		}
//		boolean foundCurrent = false;
//		List<PComponent> sibblingList = new ArrayList<>(parent.getChildren());
//		ListIterator<PComponent> sibblingIter = sibblingList.listIterator(sibblingList.size());
//		while (sibblingIter.hasPrevious()) {
//			PComponent sibbling = sibblingIter.previous();
//			if (foundCurrent) {
//				if (sibbling.isFocusable()) {
//					sibbling.takeFocus();
//					return;
//				}
//			} else if (sibbling == current) {
//				foundCurrent = true;
//			}
//		}
//		focusPrevious(parent);
//	}
//
//	protected void focusInitial() {
//		Deque<PComponent> stack = new ArrayDeque<>();
//		stack.addFirst(getRoot());
//
////		System.out.println();
////		System.out.println("focusInitial");
//		while (!stack.isEmpty()) {
//			PComponent current = stack.removeFirst();
////			System.out.println("current="+current);
//			if (current.isFocusable()) {
//				current.takeFocus();
//				return;
//			}
//			for (PComponent child : current.getChildren()) {
//				stack.addLast(child);
//			}
//		}
//	}
//}