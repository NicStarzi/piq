package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
				Constraint constr = (Constraint) constraint;
				
				PComponent parent = constr.getParent();
				int index = constr.getIndex();
				
				if (parent == null && index == -1) {
					rootComp = child;
				} else {
					List<PComponent> sibblings = childMap.get(parent);
					if (sibblings == null) {
						sibblings = new ArrayList<>();
						childMap.put(parent, sibblings);
					}
					sibblings.add(index, child);
				}
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
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
		if (constraint == null || !(constraint instanceof Constraint)) {
			return false;
		}
		PComponent parent = ((Constraint) constraint).getParent();
		int index = ((Constraint) constraint).getIndex();
		List<PComponent> sibblings = getChildrenOf(parent);
		
		boolean isNewRoot = parent == null && index == -1;
		boolean noRoot = getRootComponent() == null;
		
		return isNewRoot == noRoot || (!isNewRoot && index >= 0 && sibblings.size() >= index);
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
	
	public PComponent getComponentAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		}
		
		Deque<StackInfo> stack = new LinkedList<>();
		stack.push(new StackInfo(getRootComponent(), 0));
		PComponent before = null;
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.comp;
			PBounds cmpBnds = getChildBounds(current);
			int compY = cmpBnds.getY();
			int compFy = cmpBnds.getFinalY();
			
			if (y >= compY && y <= compFy) {
				return info.comp;
			} else if (y <= compFy) {
				return before;
			}
			
			for (PComponent child : getChildrenOf(current)) {
				stack.push(new StackInfo(child, info.lvl + 1));
			}
			before = info.comp;
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
			
			for (PComponent child : getChildrenOf(current)) {
				stack.push(new StackInfo(child, info.lvl + 1));
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
		stack.push(new StackInfo(getRootComponent(), 0));
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent current = info.comp;
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
				stack.push(new StackInfo(child, info.lvl + 1));
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