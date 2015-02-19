package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

public class PTreePosition {
	
	private final PTreeModel model;
	private final Object parent;
	private final int index;
	private final boolean isRoot;
	
	public PTreePosition(PTreeModel model, Object parent, int index) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (parent == null) {
			throw new NullPointerException("parent="+parent);
		} if (index < 0) {
			throw new IllegalArgumentException("index="+index);
		}
		isRoot = parent == null && index == -1;
		this.model = model;
		this.parent = parent;
		this.index = index;
	}
	
	public PTreePosition(PTreeModel model, Object node) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (node == null) {
			throw new NullPointerException("node="+node);
		}
		isRoot = node == model.getRoot();
		this.model = model;
		if (isRoot) {
			parent = null;
			index = -1;
		} else {
			parent = model.getParentOf(node);
			index = model.getChildIndex(parent, node);
		}
	}
	
	public PTreeModel getModel() {
		return model;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Object getNode() {
		if (isRoot) {
			return getModel().getRoot();
		}
		return getModel().getChild(getParent(), getIndex());
	}
	
	public boolean canBeAdded(Object node) {
		return getModel().canAddChild(getParent(), node, getIndex());
	}
	
	public void add(Object node) {
		getModel().addChild(getParent(), node, getIndex());
	}
	
	public boolean canBeAddedAsChild(Object node) {
		Object parent = getNode();
		int index = getModel().getChildCount(parent);
		return getModel().canAddChild(parent, node, index);
	}
	
	public void addAsChild(Object node) {
		Object parent = getNode();
		int index = getModel().getChildCount(parent);
		getModel().addChild(parent, node, index);
	}
	
	public boolean canBeRemoved() {
		return getModel().canRemoveChild(getParent(), getIndex());
	}
	
	public void remove() {
		getModel().removeChild(getParent(), getIndex());
	}
	
	public Object getRoot() {
		PTreeModel model = getModel();
		Object current = getParent();
		Object parent = model.getParentOf(current);
		while (parent != null) {
			current = parent;
			parent = model.getParentOf(current);
		}
		return parent;
	}
	
	public int getDepth() {
		int depth = 0;
		PTreeModel model = getModel();
		Object current = getParent();
		Object parent = model.getParentOf(current);
		while (parent != null) {
			current = parent;
			parent = model.getParentOf(current);
			depth++;
		}
		return depth;
	}
	
	public boolean isSibbling(PTreePosition position) {
		return position.getParent() == getParent();
	}
	
	public boolean isSibbling(Object node) {
		return isSibbling(new PTreePosition(getModel(), node));
	}
	
	public List<PTreePosition> getSibblings() {
		PTreeModel model = getModel();
		Object parent = getParent();
		
		List<PTreePosition> sibblings = new ArrayList<>();
		for (int i = 0; i < model.getChildCount(parent); i++) {
			sibblings.add(new PTreePosition(getModel(), parent, i));
		}
		return sibblings;
	}
	
	public PTreePosition getParentPosition() {
		return new PTreePosition(getModel(), getParent());
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		PTreePosition other = (PTreePosition) obj;
		return getModel().equals(other.getModel()) 
				&& getParent().equals(other.getParent()) 
				&& getIndex() == other.getIndex();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(parent=");
		builder.append(parent);
		builder.append(", index=");
		builder.append(index);
		builder.append(")");
		return builder.toString();
	}
	
}