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
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PTreeLayout extends AbstractMapPLayout implements PReadOnlyLayout {
	
	public void debug() {
		System.out.println("### childmap ###");
		
		StringBuilder sb = new StringBuilder();
		for (PComponent cmp : childMap.keySet()) {
			edu.udo.piq.components.collections.PCellComponent cell =
					(edu.udo.piq.components.collections.PCellComponent) cmp;
			
			sb.delete(0, sb.length());
			sb.append("obj=");
			sb.append(cell.getElement());
			sb.append(", list=");
			sb.append(PTreeLayout.toElementListString(getChildNodesOf(cmp)));
			System.out.println(sb.toString());
		}
		System.out.println("### !content - constraints! ###");
		for (PComponent cmp : getChildren()) {
			edu.udo.piq.components.collections.PCellComponent cell =
					(edu.udo.piq.components.collections.PCellComponent) cmp;
			System.out.println("obj="+cell.getElement()+", cnstr="+getChildConstraint(cmp));
		}
		System.out.println();
	}
	
	private static String toElementListString(List<PComponent> l) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (PComponent gc : l) {
			edu.udo.piq.components.collections.PCellComponent cell =
					(edu.udo.piq.components.collections.PCellComponent) gc;
			sb.append(cell.getElement());
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
	
	protected final ObserverList<PTreeLayoutObs> obsList =
			PCompUtil.createDefaultObserverList();
	protected final Map<PComponent, List<PComponent>> childMap = new HashMap<>();
	protected final List<LayoutData> layoutCache = new ArrayList<>(20);
	protected PComponent rootComp;
	protected PInsets insets = new ImmutablePInsets(4);
	protected int indentSize = DEFAULT_INDENT_SIZE;
	protected int gap = DEFAULT_GAP;
	protected boolean hideRootNode;
	
	public PTreeLayout(PComponent component) {
		super(component);
	}
	
	@Override
	public void removeChild(Object constraint) {
		PComponent child = getChildForConstraint(constraint);
		ThrowException.ifNull(child, "containsChild(constraint) == false");
		removeChild(child);
	}
	
	@Override
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
	
	@Override
	protected void onChildAdded(PComponent child, Object constraint) {
		PTreeIndex index = (PTreeIndex) constraint;
		
//		PCellComponent cell = (PCellComponent) child;
//		System.out.println("PTreeLayout2.onChildAdded obj="+cell.getElement()+", idx="+index);
		if (rootComp == null) {
			ThrowException.ifFalse(index.isRoot(), "rootIndex.isRoot() == false");
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
	
	@Override
	protected void onChildRemoved(PCompInfo removedCompInfo) {
		PComponent child = removedCompInfo.getComponent();
		PTreeIndex index = (PTreeIndex) removedCompInfo.getConstraint();
		
//		PCellComponent cell = (PCellComponent) child;
//		System.out.println("PTreeLayout2.onChildRemoved obj="+cell.getElement()+", idx="+index);
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
	
	@Override
	protected void onChildCleared(PComponent child, Object constraint) {
		rootComp = null;
		childMap.clear();
		invalidate();
	}
	
	protected void correctChildConstraints(List<PComponent> children, int from) {
//		System.out.println("PTreeLayout2.correctChildConstraints list="+toString(children));
		for (int i = from; i < children.size(); i++) {
			PComponent sibbling = children.get(i);
			PTreeIndex currentIndex = (PTreeIndex) getChildConstraint(sibbling);
			if (currentIndex.getLastIndex() != i) {
				PTreeIndex newIndex = currentIndex.replaceLastIndex(i);
				setChildConstraint(sibbling, newIndex);
				fireChildMoved(sibbling, currentIndex, newIndex);
//				System.out.println("bad="+currentIndex+", good="+newIndex);
				
				List<PComponent> grandChildren = getChildNodesOf(sibbling);
				if (!grandChildren.isEmpty()) {
					correctGrandChildConstraints(grandChildren, newIndex.getDepth() - 1, i);
				}
			}
		}
	}
	
	protected void correctGrandChildConstraints(List<PComponent> children, int level, int value) {
//		System.out.println("PTreeLayout2.correctGrandChildConstraints lvl="+level+", list="+toString(children));
		for (int i = 0; i < children.size(); i++) {
			PComponent sibbling = children.get(i);
			PTreeIndex currentIndex = (PTreeIndex) getChildConstraint(sibbling);
			if (currentIndex.getChildIndex(level) != value) {
				PTreeIndex newIndex = currentIndex.replaceIndex(level, value);
				setChildConstraint(sibbling, newIndex);
				fireChildMoved(sibbling, currentIndex, newIndex);
//				System.out.println("bad="+currentIndex+", good="+newIndex);
				
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
		ThrowException.ifNull(insets, "insets == null");
		if (!getInsets().equals(insets)) {
			this.insets = insets;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public void setIndentSize(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (indentSize != value) {
			indentSize = value;
			invalidate();
		}
	}
	
	public int getIndentSize() {
		return indentSize;
	}
	
	public void setGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (gap != value) {
			gap = value;
			invalidate();
		}
	}
	
	public int getGap() {
		return getStyleAttribute(ATTRIBUTE_KEY_GAP, gap);
	}
	
	public void setHideRootNode(boolean value) {
		if (hideRootNode != value) {
			hideRootNode = value;
			invalidate();
		}
	}
	
	public boolean isHideRootNode() {
		return hideRootNode;
	}
	
	@Override
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
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		ThrowException.ifTypeCastFails(constraint, PTreeIndex.class, "(constraint instanceof PTreeIndex) == false");
		return getComponentAt((PTreeIndex) constraint);
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
		ThrowException.ifFalse(containsChild(parentInTree), "containsChild(parentInTree) == false");
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
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		
		if (isHideRootNode()) {
			setChildBounds(getRootComponent(), x, y, 0, 0);
		}
		for (LayoutData data : layoutCache) {
			setChildBounds(data.comp, x + data.x, y + data.y, data.w, data.h);
		}
		layoutCache.clear();
	}
	
	@Override
	protected void onInvalidated() {
		PInsets insets = getInsets();
		
		int maxFx = 0;
		int y = 0;
		
		int indentSize = getIndentSize();
		int gap = getGap();
		
		layoutCache.clear();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
		if (isHideRootNode()) {
			List<PComponent> children = getChildNodesInternal(getRootComponent());
			if (children != null) {
				ListIterator<PComponent> iter = children.listIterator(children.size());
				while (iter.hasPrevious()) {
					PComponent child = iter.previous();
					stack.push(new StackInfo(child, 0));
				}
			}
		} else {
			stack.push(new StackInfo(getRootComponent(), 0));
		}
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.comp;
			LayoutData data = new LayoutData(current);
			layoutCache.add(data);
			
			PSize compPrefSize = getPreferredSizeOf(current);
			data.x = info.lvl * indentSize;
			data.y = y;
			data.w = compPrefSize.getWidth();
			data.h = compPrefSize.getHeight();
			
			if (data.x + data.w > maxFx) {
				maxFx = data.x + data.w;
			}
			y += data.h + gap;
			
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
		if (getChildCount() > 0) {
			y -= gap;
		}
		
		prefSize.setWidth(maxFx + insets.getHorizontal());
		prefSize.setHeight(y + insets.getVertical());
	}
	
	public void addObs(PTreeLayoutObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTreeLayoutObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChildMoved(PComponent child, PTreeIndex oldIndex, PTreeIndex newIndex) {
		obsList.fireEvent((obs) -> obs.onChildMoved(child, oldIndex, newIndex));
	}
	
	public static interface PTreeLayoutObs {
		public void onChildMoved(PComponent child, PTreeIndex oldIndex, PTreeIndex newIndex);
	}
	
	protected static class StackInfo {
		public final PComponent comp;
		public final int lvl;
		
		public StackInfo(PComponent component, int level) {
			comp = component;
			lvl = level;
		}
	}
	
	protected static class LayoutData {
		public final PComponent comp;
		public int x;
		public int y;
		public int w;
		public int h;
		
		public LayoutData(PComponent component) {
			comp = component;
		}
	}
	
}