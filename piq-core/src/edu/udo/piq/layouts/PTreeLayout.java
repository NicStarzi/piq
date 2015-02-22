package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PTreeLayout extends AbstractPLayout implements PReadOnlyLayout {
	
	public static final int DEFAULT_INDENT_SIZE = 20;
	public static final int DEFAULT_GAP = 2;
	
	protected final Object rootConstraint = new Object();
	
	protected final MutablePSize prefSize = new MutablePSize();
	protected final Map<PComponent, List<PComponent>> childMap = new HashMap<>();
	protected PComponent rootComp;
	protected PInsets insets = new ImmutablePInsets(4);
	protected int indentSize = DEFAULT_INDENT_SIZE;
	protected int gap = DEFAULT_GAP;
	
	public PTreeLayout(PComponent component) {
		super(component);
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				if (child == rootComp) {
					return;
				}
				Constraint constr = (Constraint) constraint;
				
				PComponent parent = constr.getParent();
				int index = constr.getIndex();
				
				List<PComponent> sibblings = childMap.get(parent);
				if (sibblings == null) {
					sibblings = new ArrayList<>();
					childMap.put(parent, sibblings);
				}
				sibblings.add(index, child);
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				if (child == rootComp) {
					rootComp = null;
					return;
				}
				Constraint constr = (Constraint) constraint;
				
				PComponent parent = constr.getParent();
				List<PComponent> sibblings = childMap.get(parent);
				
				sibblings.remove(child);
				if (sibblings.isEmpty()) {
					childMap.remove(parent);
				}
			}
		});
	}
	
	public void setRootComponent(PComponent component) {
		if (rootComp != null) {
			throw new IllegalStateException("rootComp="+rootComp+", component="+component);
		} if (component == null) {
			throw new NullPointerException("component="+component);
		}
		rootComp = component;
		addChild(component, rootConstraint);
	}
	
	public PComponent getRootComponent() {
		return rootComp;
	}
	
	public void setInsets(PInsets insets) {
		this.insets = insets;
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public void setIndentSize(int value) {
		indentSize = value;
		fireInvalidateEvent();
	}
	
	public int getIndentSize() {
		return indentSize;
	}
	
	public void setGap(int value) {
		gap = value;
		fireInvalidateEvent();
	}
	
	public int getGap() {
		return gap;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint == rootConstraint && component == rootComp) {
			return true;
		} if (constraint == null || !(constraint instanceof Constraint)) {
			return false;
		}
		PComponent parent = ((Constraint) constraint).getParent();
		int index = ((Constraint) constraint).getIndex();
		List<PComponent> sibblings = getChildrenOf(parent);
		
		return parent != null && index >= 0 && sibblings.size() >= index;
	}
	
	public List<PComponent> getChildrenOf(PComponent parent) {
		if (parent == null) {
			return Collections.emptyList();
		}
		List<PComponent> children = childMap.get(parent);
		if (children == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(children);
	}
	
	public PTreeLayoutPosition getPositionAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		} if (getRootComponent() == null) {
			return null;
		}
		Deque<StackInfo> stack = new LinkedList<>();
		stack.push(new StackInfo(null, getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.child;
			
			PBounds cmpBnds = getChildBounds(current);
			int compFy = cmpBnds.getFinalY();
			
			if (y <= compFy) {
				int compY = cmpBnds.getY();
				if (y >= compY) {
					int index = getChildrenOf(current).size();
					return new PTreeLayoutPosition(current, null, index);
				}
				PComponent parent = info.parent;
				int index = getChildrenOf(parent).indexOf(current);
				return new PTreeLayoutPosition(parent, current, index);
			}
			
			for (PComponent child : getChildrenOf(current)) {
				stack.push(new StackInfo(current, child, info.lvl + 1));
			}
		}
		return null;
	}
	
	public PComponent getComponentAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		} if (getRootComponent() == null) {
			return null;
		}
		
		Deque<StackInfo> stack = new LinkedList<>();
		stack.push(new StackInfo(null, getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.child;
			
			PReadOnlyLayout childLayout = current.getLayout();
			if (childLayout != null) {
				PComponent grandChild = childLayout.getChildAt(x, y);
				if (grandChild != null) {
					return grandChild;
				}
			}
			if (current.isElusive()) {
				continue;
			}
			PBounds cmpBnds = getChildBounds(current);
			if (cmpBnds.contains(x, y)) {
				return current;
			}
			
			for (PComponent child : getChildrenOf(current)) {
				stack.push(new StackInfo(current, child, info.lvl + 1));
			}
		}
		return null;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		
		int indentSize = getIndentSize();
		int gap = getGap();
		
		Deque<StackInfo> stack = new LinkedList<>();
		stack.push(new StackInfo(null, getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.child;
			PSize compPrefSize = getPreferredSizeOf(current);
			int compX = x + info.lvl * indentSize;
			int compY = y;
			int compW = compPrefSize.getWidth();
			int compH = compPrefSize.getHeight();
			
			setChildBounds(current, compX, compY, compW, compH);
			
			y += compH + gap;
			
			List<PComponent> children = getChildrenOf(current);
			ListIterator<PComponent> iter = children.listIterator(children.size());
			while (iter.hasPrevious()) {
				PComponent child = iter.previous();
				stack.push(new StackInfo(current, child, info.lvl + 1));
			}
		}
	}
	
	public PSize getPreferredSize() {
		PInsets insets = getInsets();
		
		int maxW = 0;
		int prefH = 0;
		
		int indentSize = getIndentSize();
		int gap = getGap();
		
		Deque<StackInfo> stack = new LinkedList<>();
		stack.push(new StackInfo(null, getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.child;
			PSize compPrefSize = getPreferredSizeOf(current);
			int compX = info.lvl * indentSize;
			int compW = compPrefSize.getWidth();
			int compH = compPrefSize.getHeight();
			
			if (compX + compW > maxW) {
				maxW = compW;
			}
			prefH += compH;
			if (!stack.isEmpty()) {
				prefH += gap;
			}
			
			for (PComponent child : getChildrenOf(current)) {
				stack.push(new StackInfo(current, child, info.lvl + 1));
			}
		}
		
		prefSize.setWidth(maxW + insets.getHorizontal());
		prefSize.setHeight(prefH + insets.getVertical());
		return prefSize;
	}
	
	public static class Constraint {
		
		final PComponent parent;
		final int index;
		
		public Constraint(PComponent parent, int index) {
			this.parent = parent;
			this.index = index;
		}
		
		public PComponent getParent() {
			return parent;
		}
		
		public int getIndex() {
			return index;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} if (obj == null || !(obj instanceof Constraint)) {
				return false;
			}
			Constraint other = (Constraint) obj;
			return index == other.index && parent == other.parent;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Constraint [parent=");
			builder.append(parent);
			builder.append(", index=");
			builder.append(index);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	protected static class StackInfo {
		public final PComponent parent;
		public final PComponent child;
		public final int lvl;
		
		public StackInfo(PComponent parent, PComponent child, int level) {
			this.parent = parent;
			this.child = child;
			lvl = level;
		}
	}
	
	public static class PTreeLayoutPosition {
		
		final PComponent parentComp;
		final PComponent childComp;
		final int index;
		
		public PTreeLayoutPosition(PComponent parentComp, PComponent childComp, int index) {
			this.parentComp = parentComp;
			this.childComp = childComp;
			this.index = index;
		}
		
		public PComponent getParentComponent() {
			return parentComp;
		}
		
		public PComponent getChildComponent() {
			return childComp;
		}
		
		public int getIndex() {
			return index;
		}
		
	}
	
}