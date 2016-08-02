package edu.udo.piq.layouts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PTreeLayout extends AbstractMapPLayout implements PReadOnlyLayout {
	
	public void debug() {
		System.out.println("### childmap ###");
		
		StringBuilder sb = new StringBuilder();
		for (PComponent cmp : childMap.keySet()) {
			PCellComponent cell = (PCellComponent) cmp;
			
			sb.delete(0, sb.length());
			sb.append("obj=");
			sb.append(cell.getElement());
			sb.append(", list=");
			sb.append(toString(getChildNodesOf(cmp)));
			System.out.println(sb.toString());
		}
		System.out.println("### !content - constraints! ###");
		for (PComponent cmp : getChildren()) {
			PCellComponent cell = (PCellComponent) cmp;
			System.out.println("obj="+cell.getElement()+", cnstr="+getChildConstraint(cmp));
		}
		System.out.println();
	}
	
	private static String toString(List<PComponent> l) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (PComponent gc : l) {
			sb.append(((PCellComponent) gc).getElement());
			sb.append(", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static final int DEFAULT_INDENT_SIZE = 20;
	public static final int DEFAULT_GAP = 2;
	
	protected final Map<PComponent, List<PComponent>> childMap = new HashMap<>();
	protected PComponent rootComp;
	protected PInsets insets = new ImmutablePInsets(4);
	protected int indentSize = DEFAULT_INDENT_SIZE;
	protected int gap = DEFAULT_GAP;
	
	public PTreeLayout(PComponent component) {
		super(component);
	}
	
	public void removeChild(Object constraint) {
		PComponent child = getChildForConstraint(constraint);
		ThrowException.ifNull(child, "containsChild(constraint) == false");
		removeChild(child);
	}
	
	public void removeChild(PComponent child) {
		List<PComponent> grandChildren = getChildNodesOf(child);
		if (grandChildren.size() > 0) {
			grandChildren = new ArrayList<>(grandChildren);
			for (PComponent grandChild : grandChildren) {
				removeChild(grandChild);
			}
		}
		super.removeChild(child);
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		PTreeIndex index = (PTreeIndex) constraint;
		
		PCellComponent cell = (PCellComponent) child;
		System.out.println("PTreeLayout2.onChildAdded obj="+cell.getElement()+", idx="+index);
		if (rootComp == null) {
			ThrowException.ifNotEqual(index.getDepth(), 0, "rootIndex.getDepth() != 0");
			rootComp = child;
			return;
		}
		
		PComponent parent = getParentAt(index);
		int indexVal = index.getLastIndex();
		
		List<PComponent> sibblings = childMap.get(parent);
		if (sibblings == null) {
			sibblings = new ArrayList<>();
			childMap.put(parent, sibblings);
		}
		sibblings.add(indexVal, child);
		if (index.getLastIndex() != sibblings.size() - 1) {
			correctChildConstraints(sibblings, index.getLastIndex());
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		PTreeIndex index = (PTreeIndex) constraint;
		
		PCellComponent cell = (PCellComponent) child;
		System.out.println("PTreeLayout2.onChildRemoved obj="+cell.getElement()+", idx="+index);
		if (child == rootComp) {
			rootComp = null;
			childMap.clear();
			return;
		}
		childMap.remove(child);
		
		PComponent parent = getParentAt(index);
		List<PComponent> sibblings = childMap.get(parent);
		if (sibblings == null) {
			return;
		}
		
		sibblings.remove(child);
		if (sibblings.isEmpty()) {
			childMap.remove(parent);
		}
		if (index.getLastIndex() != sibblings.size()) {
			correctChildConstraints(sibblings, index.getLastIndex());
		}
		invalidate();
	}
	
	protected void correctChildConstraints(List<PComponent> children, int from) {
		System.out.println("PTreeLayout2.correctChildConstraints list="+toString(children));
		for (int i = from; i < children.size(); i++) {
			PComponent sibbling = children.get(i);
			PTreeIndex currentIndex = (PTreeIndex) getChildConstraint(sibbling);
			if (currentIndex.getLastIndex() != i) {
				PTreeIndex newIndex = currentIndex.replaceLastIndex(i);
				setChildConstraint(sibbling, newIndex);
				System.out.println("bad="+currentIndex+", good="+newIndex);
				
				List<PComponent> grandChildren = getChildNodesOf(sibbling);
				if (!grandChildren.isEmpty()) {
					correctGrandChildConstraints(grandChildren, newIndex.getDepth() - 1, i);
				}
			}
		}
	}
	
	protected void correctGrandChildConstraints(List<PComponent> children, int level, int value) {
		System.out.println("PTreeLayout2.correctGrandChildConstraints lvl="+level+", list="+toString(children));
		for (int i = 0; i < children.size(); i++) {
			PComponent sibbling = children.get(i);
			PTreeIndex currentIndex = (PTreeIndex) getChildConstraint(sibbling);
			if (currentIndex.getChildIndex(level) != value) {
				PTreeIndex newIndex = currentIndex.replaceIndex(level, value);
				setChildConstraint(sibbling, newIndex);
				System.out.println("bad="+currentIndex+", good="+newIndex);
				
				List<PComponent> grandChildren = getChildNodesOf(sibbling);
				if (!grandChildren.isEmpty()) {
					correctGrandChildConstraints(grandChildren, level, value);
				}
			}
		}
	}
	
	public PComponent getRootComponent() {
		return rootComp;
	}
	
	public void setInsets(PInsets insets) {
		this.insets = insets;
		invalidate();
	}
	
	public PInsets getInsets() {
		PLayoutDesign design = getDesign();
		if (design == null) {
			return insets;
		}
		Object maybeInsets = getDesign().getAttribute(ATTRIBUTE_KEY_INSETS);
		if (maybeInsets != null && maybeInsets instanceof PInsets) {
			return (PInsets) maybeInsets;
		}
		return insets;
	}
	
	public void setIndentSize(int value) {
		indentSize = value;
		invalidate();
	}
	
	public int getIndentSize() {
		return indentSize;
	}
	
	public void setGap(int value) {
		gap = value;
		invalidate();
	}
	
	public int getGap() {
		return gap;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint == null || !(constraint instanceof PTreeIndex)) {
			return false;
		}
		PTreeIndex index = (PTreeIndex) constraint;
		if (index.getDepth() == 0) {
			return rootComp == null;
		}
		int indexVal = index.getLastIndex();
		PComponent parent = getParentAt(index);
		if (parent == null) {
			return false;
		}
		List<PComponent> sibblings = getChildNodesOf(parent);
		
		return indexVal >= 0 && sibblings.size() >= indexVal;
	}
	
	public PComponent getParentAt(PTreeIndex index) {
		return getComponentAt(index, index.getDepth() - 1);
	}
	
	public PComponent getComponentAt(PTreeIndex index) {
		return getComponentAt(index, index.getDepth());
	}
	
	public PComponent getComponentAt(PTreeIndex index, int depth) {
		PComponent current = rootComp;
		int level = 0;
		while (level < depth) {
			if (current == null) {
				return null;
			}
			int childIndex = index.getChildIndex(level++);
			List<PComponent> children = getChildNodesOf(current);
			if (childIndex >= children.size()) {
				return null;
			}
			current = children.get(childIndex);
		}
		return current;
	}
	
	public List<PComponent> getChildNodesOf(PComponent parentInTree) {
		if (parentInTree == null) {
			return Collections.emptyList();
		}
		List<PComponent> children = childMap.get(parentInTree);
		if (children == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(children);
	}
	
	protected List<PComponent> getChildNodesInternal(PComponent parentInTree) {
		if (parentInTree == null) {
			return Collections.emptyList();
		}
		return childMap.get(parentInTree);
	}
	
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		
		int indentSize = getIndentSize();
		int gap = getGap();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
		stack.push(new StackInfo(getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.comp;
			PSize compPrefSize = getPreferredSizeOf(current);
			int compX = x + info.lvl * indentSize;
			int compY = y;
			int compW = compPrefSize.getWidth();
			int compH = compPrefSize.getHeight();
			
			setChildBounds(current, compX, compY, compW, compH);
			
			y += compH + gap;
			
			List<PComponent> children = getChildNodesInternal(current);
			if (children == null) {
				continue;
			}
			ListIterator<PComponent> iter = children.listIterator(children.size());
			while (iter.hasPrevious()) {
				PComponent child = iter.previous();
				stack.push(new StackInfo(child, info.lvl + 1));
			}
		}
	}
	
	protected void onInvalidated() {
		PInsets insets = getInsets();
		
		int maxFx = 0;
		int prefH = 0;
		
		int indentSize = getIndentSize();
		int gap = getGap();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
		stack.push(new StackInfo(getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.comp;
			PSize compPrefSize = getPreferredSizeOf(current);
			int compX = info.lvl * indentSize;
			int compW = compPrefSize.getWidth();
			int compH = compPrefSize.getHeight();
			
			if (compX + compW > maxFx) {
				maxFx = compX + compW;
			}
			prefH += compH + gap;
			
			for (PComponent child : getChildNodesOf(current)) {
				stack.push(new StackInfo(child, info.lvl + 1));
			}
		}
		if (getChildCount() > 0) {
			prefH -= gap;
		}
		
		prefSize.setWidth(maxFx + insets.getHorizontal());
		prefSize.setHeight(prefH + insets.getVertical());
	}
	
	protected static class StackInfo {
		public final PComponent comp;
		public final int lvl;
		
		public StackInfo(PComponent component, int level) {
			comp = component;
			lvl = level;
		}
	}
	
}