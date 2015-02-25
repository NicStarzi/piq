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
		children.add(index, child);
		parentMap.put(child, parent);
		fireAddedEvent(parent, child, index);
	}
	
	public boolean canRemoveChild(Object parent, int index) {
		return (parent != null && index >= 0 && getChildCount(parent) >= index);
	}
	
	public void removeChild(Object parent, int index) {
		List<Object> children = childMap.get(parent);
		if (children == null || children.isEmpty()) {
			return;
		}
		Object child = children.get(index);
		removeAllChildren(child);
		children.remove(index);
		if (children.isEmpty()) {
			childMap.remove(parent);
		}
		parentMap.remove(child);
		fireRemovedEvent(parent, child, index);
	}
	
	private void removeAllChildren(Object parent) {
		List<Object> children = childMap.get(parent);
		if (children != null) {
			for (Object child : children) {
				removeAllChildren(child);
			}
			childMap.remove(parent);
			for (int index = 0; index < children.size(); index++) {
				Object child = children.get(index);
				parentMap.remove(child);
				fireRemovedEvent(parent, child, index);
			}
			children.clear();
		}
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
//	public void test() {
//		StringBuilder sb = new StringBuilder();
//		p(sb, getRoot(), 0);
//		System.out.println(sb.toString());
//	}
//	
//	void p(StringBuilder sb, Object node, int level) {
//		sb.append('\n');
//		for (int i = 0; i < level; i++) {
//			sb.append('\t');
//		}
//		sb.append(node);
//		for (Object child : getChildrenOf(node)) {
//			p(sb, child, level + 1);
//		}
//	}
	
}