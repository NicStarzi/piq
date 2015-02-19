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
	
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
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
		List<Object> children = childMap.get(parent);
		Object child = children.remove(index);
		parentMap.remove(child);
		if (children.isEmpty()) {
			childMap.remove(parent);
		}
		fireRemovedEventForBranch(parent, child, index);
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
}