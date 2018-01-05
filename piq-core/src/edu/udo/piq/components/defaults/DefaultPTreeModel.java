package edu.udo.piq.components.defaults;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.IllegalIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;
import edu.udo.piq.components.collections.WrongIndexType;
import edu.udo.piq.tools.AbstractPModel;
import edu.udo.piq.util.ThrowException;

public class DefaultPTreeModel extends AbstractPModel implements PTreeModel {
	
	protected DefaultPTreeNode rootNode;
	protected int size;
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public Object getRoot() {
		if (rootNode == null) {
			return null;
		}
		return rootNode.getContent();
	}
	
	@Override
	public int getChildCount(PTreeIndex index) {
		DefaultPTreeNode parentNode = getNode(index);
		return parentNode == null ? 0 : parentNode.getChildCount();
	}
	
	@Override
	public boolean canSet(PModelIndex index, Object content) {
		return index instanceof PTreeIndex && contains(index);
	}
	
	@Override
	public void set(PModelIndex index, Object content) {
		DefaultPTreeNode node = getNode(index);
		if (node == null) {
			throw new IllegalIndex(index);
		}
		node.content = content;
		fireChangeEvent(index, content);
	}
	
	@Override
	public Object get(PModelIndex index) {
		DefaultPTreeNode node = getNode(index);
		if (node == null) {
			throw new IllegalIndex(index);
		}
		return node.getContent();
	}
	
	@Override
	public boolean contains(PModelIndex index) {
		return getNode(index) != null;
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
		size++;
		fireAddEvent(index, content);
	}
	
	@Override
	public boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	@Override
	public void remove(PModelIndex index) {
		DefaultPTreeNode childNode = getNode(index);
		remove(childNode, index);
	}
	
	protected void remove(DefaultPTreeNode childNode) {
		PTreeIndex index = getIndex(childNode);
		remove(childNode, index);
	}
	
	protected void remove(DefaultPTreeNode childNode, PModelIndex index) {
		if (childNode == rootNode) {
			rootNode = null;
			fireRemoveEvent(index, childNode.getContent());
			return;
		}
		DefaultPTreeNode parentNode = childNode.getParent();
		removeChildrenOf(childNode, (PTreeIndex) index);
		parentNode.removeChild(childNode);
		size--;
		fireRemoveEvent(index, childNode.getContent());
	}
	
	protected final void removeChildrenOf(DefaultPTreeNode parentNode, PTreeIndex parentIndex) {
		for (DefaultPTreeNode childNode : parentNode.children) {
			childNode.parent = null;
			size--;
			fireRemoveEvent(parentIndex.append(0), childNode.getContent());
		}
		parentNode.children.clear();
	}
	
	@Override
	public void removeAll(Iterable<PModelIndex> indices) {
		Iterator<PModelIndex> iter = indices.iterator();
		if (!iter.hasNext()) {
			return;
		}
		List<DefaultPTreeNode> toRemove = new ArrayList<>();
		while (iter.hasNext()) {
			PTreeIndex idx = (PTreeIndex) iter.next();
			
			DefaultPTreeNode node = getNode(idx);
			toRemove.add(node);
		}
		for (DefaultPTreeNode node : toRemove) {
			remove(node);
		}
	}
	
	@Override
	public void removeAll(PModelIndex ... indices) {
		removeAll(Arrays.asList(indices));
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
			if (current.getChildCount() <= childIndex) {
				return null;
			} else {
				current = current.getChild(childIndex);
			}
		}
		return current;
	}
	
	protected PTreeIndex asTreeIndex(PModelIndex index) {
		ThrowException.ifNull(index, "index == null");
		if (index instanceof PTreeIndex) {
			return (PTreeIndex) index;
		}
		throw new WrongIndexType(index, PTreeIndex.class);
	}
	
	@Override
	public String toString() {
		if (getRoot() == null) {
			return "EMPTY-TREE";
		}
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
		
		protected final List<DefaultPTreeNode> children = new ArrayList<>();
		protected Object content;
		protected DefaultPTreeNode parent;
		
		public DefaultPTreeNode(Object content) {
			this.content = content;
		}
		
		public void setContent(Object value) {
			content = value;
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
			if (index < 0 || index >= children.size()) {
				return null;
			}
			return children.get(index);
		}
		
		public int getChildIndex(DefaultPTreeNode child) {
			return children.indexOf(child);
		}
		
		public List<DefaultPTreeNode> getChildren() {
			return Collections.unmodifiableList(children);
		}
		
		public PTreeIndex createTreeIndex() {
			if (parent == null) {
				return PTreeIndex.ROOT;
			}
			if (parent.parent == null) {
				return new PTreeIndex(parent.children.indexOf(this));
			}
			Deque<Integer> indexStack = new ArrayDeque<>();
			DefaultPTreeNode current = parent;
			DefaultPTreeNode currentChild = this;
			while (current != null) {
				indexStack.addFirst(current.children.indexOf(currentChild));
				currentChild = current;
				current = current.parent;
			}
			return new PTreeIndex(indexStack);
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
	
}