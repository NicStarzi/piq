package edu.udo.piq.comps.selectcomps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class DefaultPTreeModel extends AbstractPModel implements PTreeModel {
	
	private DefaultPTreeNode rootNode;
	
	public Object getRoot() {
		if (rootNode == null) {
			return null;
		}
		return rootNode.getContent();
	}
	
	public Object get(PModelIndex index) {
		DefaultPTreeNode node = getNode(index);
		if (node == null) {
			throw new IllegalIndex(index);
		}
		return node.getContent();
	}
	
	public boolean contains(PModelIndex index) {
		return getNode(index) != null;
	}
	
	public PTreeIndex getIndexOf(Object content) {
		if (rootNode == null) {
			return null;
		}
		Deque<DefaultPTreeNode> stack = new ArrayDeque<>();
		stack.push(rootNode);
		while (!stack.isEmpty()) {
			DefaultPTreeNode current = stack.pop();
			if (current.getContent().equals(content)) {
				return getIndex(current);
			}
			stack.addAll(current.children);
		}
		return null;
	}
	
	public boolean canAdd(PModelIndex index, Object content) {
		if (content == null) {
			return false;
		}
		PTreeIndex treeIndex = asTreeIndex(index);
		if (treeIndex.getDepth() == 0) {
			return rootNode == null;
		}
		int lastChildIndex = treeIndex.getChildIndex(treeIndex.getDepth() - 1);
		DefaultPTreeNode parentNode = getNode(index, treeIndex.getDepth() - 1);
		return parentNode != null && parentNode.getChildCount() >= lastChildIndex;
	}
	
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		DefaultPTreeNode childNode = new DefaultPTreeNode(content);
		
		PTreeIndex treeIndex = asTreeIndex(index);
		if (treeIndex.getDepth() == 0) {
			rootNode = childNode;
			fireAddEvent(index, content);
			return;
		}
		int childIndex = treeIndex.getChildIndex(treeIndex.getDepth() - 1);
		DefaultPTreeNode parentNode = getNode(index, treeIndex.getDepth() - 1);
		parentNode.addChild(childIndex, childNode);
		fireAddEvent(index, content);
	}
	
	public boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	public void remove(PModelIndex index) {
		DefaultPTreeNode childNode = getNode(index);
		if (childNode == rootNode) {
			rootNode = null;
			fireRemoveEvent(index, childNode.getContent());
			return;
		}
		DefaultPTreeNode parentNode = childNode.getParent();
		parentNode.removeChild(childNode);
		fireRemoveEvent(index, childNode.getContent());
	}
	
	public Iterator<Object> iterator() {
		return new DefaultPTreeModelIterator(rootNode);
	}
	
	protected PTreeIndex getIndex(DefaultPTreeNode node) {
		Deque<Integer> indices = new ArrayDeque<>();
		DefaultPTreeNode current = node;
		while (current.getParent() != null) {
			DefaultPTreeNode parent = current.getParent();
			int index = parent.getChildIndex(current);
			indices.addFirst(Integer.valueOf(index));
			current = parent;
		}
		return new PTreeIndex(indices);
	}
	
	protected DefaultPTreeNode getNode(PModelIndex index) {
		PTreeIndex treeIndex = asTreeIndex(index);
		return getNode(index, treeIndex.getDepth());
	}
	
	protected DefaultPTreeNode getNode(PModelIndex index, int depth) {
		PTreeIndex treeIndex = asTreeIndex(index);
		if (rootNode == null) {
			return null;
		}
		DefaultPTreeNode current = rootNode;
		for (int lvl = 0; lvl < depth; lvl++) {
			int childIndex = treeIndex.getChildIndex(lvl);
			if (current.getChildCount() < childIndex) {
				return null;
			} else {
				current = current.getChild(childIndex);
			}
		}
		return current;
	}
	
	protected PTreeIndex asTreeIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PTreeIndex) {
			return (PTreeIndex) index;
		}
		throw new WrongIndexType(index, PTreeIndex.class);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		class PrintInfo {
			DefaultPTreeNode node;
			int level;
			
			PrintInfo(DefaultPTreeNode node, int level) {
				this.node = node;
				this.level = level;
			}
		}
		
		Deque<PrintInfo> stack = new ArrayDeque<>();
		stack.push(new PrintInfo(rootNode, 0));
		while (!stack.isEmpty()) {
			PrintInfo current = stack.pop();
			
			List<DefaultPTreeNode> children = current.node.children;
			for (int i = children.size() - 1; i >= 0; i--) {
				DefaultPTreeNode node = children.get(i);
				stack.addFirst(new PrintInfo(node, current.level + 1));
			}
			
			for (int i = 0; i < current.level; i++) {
				sb.append('\t');
			}
			sb.append(current.node.content.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
	protected static class DefaultPTreeNode {
		
		private final List<DefaultPTreeNode> children = new ArrayList<>();
		private final Object content;
		private DefaultPTreeNode parent;
		
		public DefaultPTreeNode(Object content) {
			this.content = content;
		}
		
		public Object getContent() {
			return content;
		}
		
		public DefaultPTreeNode getParent() {
			return parent;
		}
		
		public int getChildCount() {
			return children.size();
		}
		
		public DefaultPTreeNode getChild(int index) {
			return children.get(index);
		}
		
		public int getChildIndex(DefaultPTreeNode child) {
			return children.indexOf(child);
		}
		
		public List<DefaultPTreeNode> getChildren() {
			return Collections.unmodifiableList(children);
		}
		
		public void addChild(int index, DefaultPTreeNode node) {
			if (node.parent != null) {
				throw new IllegalStateException("node.parent != null");
			}
			children.add(index, node);
			node.parent = this;
		}
		
		public void removeChild(DefaultPTreeNode node) {
			if (node.parent != this) {
				throw new IllegalStateException("node.parent != this");
			}
			children.remove(node);
			node.parent = null;
		}
		
	}
	
	protected static class DefaultPTreeModelIterator implements Iterator<Object> {
		
		private final Deque<DefaultPTreeNode> stack = new ArrayDeque<>();
		
		public DefaultPTreeModelIterator(DefaultPTreeNode root) {
			stack.push(root);
		}
		
		public boolean hasNext() {
			return !stack.isEmpty();
		}
		
		public Object next() {
			DefaultPTreeNode node = stack.pop();
			for (int i = 0; i < node.getChildCount(); i++) {
				stack.push(node.getChild(i));
			}
			return node.getContent();
		}
		
	}
	
}