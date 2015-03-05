package edu.udo.piq.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPLayout implements PLayout {
	
	private final List<PLayoutObs> obsList = new CopyOnWriteArrayList<>();
	private final Map<PComponent, PCompInfo> compMap = new HashMap<>();
	private final PComponent owner;
	private PLayoutDesign design;
	
	protected AbstractPLayout(PComponent component) {
		if (component == null) {
			throw new NullPointerException();
		}
		owner = component;
	}
	
	protected abstract boolean canAdd(PComponent component, Object constraint);
	
	public PComponent getOwner() {
		return owner;
	}
	
	public void addChild(PComponent component, Object constraint) throws NullPointerException, IllegalArgumentException, IllegalStateException {
		if (component == null) {
			throw new NullPointerException("component="+component);
		} if (!canAdd(component, constraint)) {
			throw new IllegalArgumentException("constraint="+constraint);
		} if (containsChild(component)) {
			throw new IllegalStateException(this+".contains "+component);
		}
		PCompInfo info = new PCompInfo(component, constraint);
		compMap.put(component, info);
		
		component.setParent(getOwner());
		fireAddEvent(component, constraint);
	}
	
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException {
		if (child == null) {
			throw new NullPointerException("component="+child);
		} if (!containsChild(child)) {
			throw new IllegalStateException(this+".contains not "+child);
		}
		Object constraint = compMap.get(child).constr;
		
		compMap.remove(child);
		child.setParent(null);
		fireRemoveEvent(child, constraint);
	}
	
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		removeChild(getChildForConstraint(constraint));
	}
	
	public void clearChildren() {
		Collection<PComponent> children = getChildren();
		for (PComponent child : children) {
			Object constraint = compMap.get(child).constr;
			child.setParent(null);
			fireRemoveEvent(child, constraint);
		}
		compMap.clear();
	}
	
	public boolean containsChild(PComponent child) throws NullPointerException {
		return compMap.containsKey(child);
	}
	
	public boolean containsChild(Object constraint) throws IllegalArgumentException {
		return getChildForConstraint(constraint) != null;
	}
	
	public PComponent getChildAt(int x, int y) {
		for (PCompInfo info : compMap.values()) {
			if (info.comp.isElusive()) {
				if (info.comp.getLayout() != null) {
					PComponent grandChild = info.comp.getLayout().getChildAt(x, y);
					if (grandChild != null) {
						return grandChild;
					}
				}
			} else if (info.bounds.contains(x, y)) {
				return info.comp;
			}
		}
		return null;
	}
	
	public PBounds getChildBounds(PComponent child) throws NullPointerException, IllegalArgumentException {
		if (child == null) {
			throw new NullPointerException("child == null");
		}
		PCompInfo info = compMap.get(child);
		if (info != null) {
			return info.bounds;
		}
		throw new IllegalArgumentException(child+" is not a child of "+getOwner());
	}
	
	public Object getChildConstraint(PComponent child) throws NullPointerException {
		if (child == null) {
			throw new NullPointerException("child == null");
		}
		PCompInfo info = compMap.get(child);
		if (info != null) {
			return info.constr;
		}
		throw new IllegalArgumentException(child+" is not a child of "+getOwner());
	}
	
	public Collection<PComponent> getChildren() {
		List<PComponent> children = new ArrayList<>(compMap.keySet());
		return Collections.unmodifiableList(children);
	}
	
	/*
	 * Utility methods
	 */
	
	protected PSize getPreferredSizeOf(PComponent child) {
		if (child == null) {
			return PSize.NULL_SIZE;
		}
		return PCompUtil.getPreferredSizeOf(child);
	}
	
	protected void setChildBounds(PComponent child, int x, int y, int width, int height) {
		if (child == null) {
			return;
		}
		PCompInfo info = compMap.get(child);
		MutablePBounds bnds = info.bounds;
		if (bnds.x != x || bnds.y != y || bnds.w != width || bnds.h != height) {
			bnds.setX(x);
			bnds.setY(y);
			bnds.setWidth(width);
			bnds.setHeight(height);
			fireLaidOutEvent(child, getChildConstraint(child));
		}
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		for (PCompInfo info : compMap.values()) {
			if ((info.constr == null && constraint == null) 
					|| info.constr.equals(constraint)) 
			{
				return info.comp;
			}
		}
		return null;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [owner=");
		builder.append(getOwner());
		builder.append(", children=");
		builder.append(getChildren());
		builder.append("]");
		return builder.toString();
	}
	
	public void setDesign(PLayoutDesign design) {
		this.design = design;
		fireInvalidateEvent();
	}
	
	public PLayoutDesign getDesign() {
		if (design != null) {
			return design;
		}
		PRoot root = getOwner().getRoot();
		if (root != null) {
			return root.getDesignSheet().getDesignFor(this);
		}
		return null;
	}
	
	/*
	 * Observers & Events
	 */
	
	public void addObs(PLayoutObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.add(obs);
	}
	
	public void removeObs(PLayoutObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		obsList.remove(obs);
	}
	
	protected void fireAddEvent(PComponent child, Object constraint) {
		for (PLayoutObs obs : obsList) {
			obs.childAdded(this, child, constraint);
		}
	}
	
	protected void fireRemoveEvent(PComponent child, Object constraint) {
		for (PLayoutObs obs : obsList) {
			obs.childRemoved(this, child, constraint);
		}
	}
	
	protected void fireLaidOutEvent(PComponent child, Object constraint) {
		for (PLayoutObs obs : obsList) {
			obs.childLaidOut(this, child, constraint);
		}
	}
	
	protected void fireInvalidateEvent() {
		for (PLayoutObs obs : obsList) {
			obs.layoutInvalidated(this);
		}
	}
	
	/*
	 * Utility classes
	 */
	
	protected static class PCompInfo {
		
		protected final PComponent comp;
		protected final Object constr;
		protected final MutablePBounds bounds;
		
		public PCompInfo(PComponent component, Object constraint) {
			comp = component;
			constr = constraint;
			bounds = new MutablePBounds();
		}
		
	}
	
	protected static class MutablePBounds extends AbstractPBounds implements PBounds {
		
		protected int x;
		protected int y;
		protected int w;
		protected int h;
		
		public MutablePBounds() {
			this(0, 0, 0, 0);
		}
		
		public MutablePBounds(int width, int height) {
			this(0, 0, width, height);
		}
		
		public MutablePBounds(int x, int y, int width, int height) {
			super();
			this.x = x;
			this.y = y;
			this.w = width;
			this.h = height;
		}
		
		protected void setX(int value) {
			x = value;
		}
		
		public int getX() {
			return x;
		}
		
		protected void setY(int value) {
			y = value;
		}
		
		public int getY() {
			return y;
		}
		
		protected void setWidth(int value) {
			w = value;
		}
		
		public int getWidth() {
			return w;
		}
		
		protected void setHeight(int value) {
			h = value;
		}
		
		public int getHeight() {
			return h;
		}
		
	}
	
}