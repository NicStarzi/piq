package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.udo.piq.components.PTreeModel;
import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.tools.AbstractPTreeModel;

public class DefaultPTreeModel extends AbstractPTreeModel implements PTreeModel {
	
	private final Map<Object, Object> parentMap = new HashMap<>();
	private final Map<Object, List<Object>> childMap = new HashMap<>();
	private Object root;
	
	public DefaultPTreeModel() {
		this(null);
	}
	
	public DefaultPTreeModel(Object root) {
		setRoot(root);
	}
	
	private List<Object> getChildrenOf(Object node) {
		List<Object> children = childMap.get(node);
		if (children == null) {
			return Collections.emptyList();
		}
		return children;
	}
	
	public void setRoot(Object node) {
		if (getRoot() != null) {
			fireRemovedEventForBranch(null, getRoot(), -1);
		}
		root = node;
		if (getRoot() != null) {
			fireAddedEventForBranch(null, getRoot(), -1);
		}
	}
	
	public Object getRoot() {
		return root;
	}
	
	public Object getParentOf(Object child) {
		return parentMap.get(child);
	}
	
	public int getChildCount(Object parent) {
		return getChildrenOf(parent).size();
	}
	
	public Object getChild(Object parent, int childIndex) {
		return getChildrenOf(parent).get(childIndex);
	}
	
	public int getChildIndex(Object parent, Object child) {
		return getChildrenOf(parent).indexOf(child);
	}
	
	public boolean canAddChild(Object parent, Object child, int index) {
		return parent != null && child != null && index >= 0 && getChildCount(parent) >= index;
	}
	
	public void addChild(Object parent, Object child, int index) {
		List<Object> children = childMap.get(parent);
		if (children == null) {
			children = new ArrayList<>();
			childMap.put(parent, children);
		}
		System.out.println("index="+index);
		children.add(index, child);
		parentMap.put(child, parent);
		fireAddedEventForBranch(parent, child, index);
	}
	
	public boolean canRemoveChild(Object parent, int index) {
		return (parent == null && index == -1 && getRoot() != null) 
				|| (parent != null && index >= 0 && getChildCount(parent) >= index);
	}
	
	public void removeChild(Object parent, int index) {
		if (parent == null && index == -1) {
			setRoot(null);
			return;
		}
		removeBranch(parent, getChild(parent, index), index);
		
//		List<Object> children = childMap.get(parent);
//		Object child = children.remove(index);
//		
//		recursiveRemove(child);
//		
//		parentMap.remove(child);
//		if (children.isEmpty()) {
//			childMap.remove(parent);
//		}
//		fireRemovedEventForBranch(parent, child, index);
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
	protected void removeBranch(Object grandParent, Object parent, int index) {
		for (int i = 0; i < getChildCount(parent); i++) {
			Object child = getChild(parent, i);
			removeBranch(parent, child, index);
			parentMap.remove(child);
		}
		childMap.get(parent);
		fireRemovedEvent(grandParent, parent, index);
	}
	
}