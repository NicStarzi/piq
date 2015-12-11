package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPLayout implements PLayout {
	
	protected final ObserverList<PLayoutObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PComponent owner;
	private PLayoutDesign design;
	
	protected AbstractPLayout(PComponent component) {
		ThrowException.ifNull(component, "component == null");
		owner = component;
	}
	
	protected abstract boolean canAdd(PComponent component, Object constraint);
	
	protected abstract PCompInfo getInfoFor(PComponent child);
	
	protected abstract Iterable<PCompInfo> getAllInfos();
	
	protected abstract void clearAllInfosInternal();
	
	protected abstract void addInfoInternal(PCompInfo info);
	
	protected abstract void removeInfoInternal(PCompInfo info);
	
	public PComponent getOwner() {
		return owner;
	}
	
	public void addChild(PComponent component, Object constraint) throws NullPointerException, IllegalArgumentException, IllegalStateException {
		ThrowException.ifNull(component, "component == null");
		ThrowException.ifFalse(canAdd(component, constraint), "canAdd(component, constraint) == false");
		ThrowException.ifTrue(containsChild(component), "containsChild(component) == true");
		
		PCompInfo info = new PCompInfo(component, constraint);
		addInfoInternal(info);
		
		component.setParent(getOwner());
		fireAddEvent(component, constraint);
	}
	
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException {
		ThrowException.ifNull(child, "child == null");
		
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		Object constraint = info.constr;
		
		removeInfoInternal(info);
		child.setParent(null);
		fireRemoveEvent(child, constraint);
	}
	
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		PComponent child = getChildForConstraint(constraint);
		ThrowException.ifNull(child, "containsChild(constraint) == false");
		removeChild(child);
	}
	
	public void clearChildren() {
		for (PCompInfo info : getAllInfos()) {
			PComponent child = info.comp;
			Object constraint = info.constr;
			child.setParent(null);
			fireRemoveEvent(child, constraint);
		}
		clearAllInfosInternal();
	}
	
	public boolean containsChild(PComponent child) throws NullPointerException {
		return getInfoFor(child) != null;
	}
	
	public boolean containsChild(Object constraint) throws IllegalArgumentException {
		return getChildForConstraint(constraint) != null;
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		for (PCompInfo info : getAllInfos()) {
			if (constraintsAreEqual(info.constr, constraint)) {
				return info.comp;
			}
		}
		return null;
	}
	
	public PComponent getChildAt(int x, int y) {
		for (PCompInfo info : getAllInfos()) {
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
		ThrowException.ifNull(child, "child == null");
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		return info.bounds;
	}
	
	public Object getChildConstraint(PComponent child) throws NullPointerException {
		ThrowException.ifNull(child, "child == null");
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		return info.constr;
	}
	
	protected void setChildBounds(PComponent child, int x, int y, int width, int height) {
		if (child == null) {
			return;
		}
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		setChildBounds(info, x, y, width, height);
	}
	
	protected void setChildBounds(PCompInfo info, int x, int y, int width, int height) {
		MutablePBounds bnds = info.bounds;
		if (bnds.x != x || bnds.y != y || bnds.w != width || bnds.h != height) {
			bnds.setX(x);
			bnds.setY(y);
			bnds.setWidth(width);
			bnds.setHeight(height);
			fireLaidOutEvent(info.comp, info.constr);
		}
	}
	
	protected void setChildConstraint(PComponent child, Object constraint) {
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		info.constr = constraint;
	}
	
	protected PSize getPreferredSizeOf(PComponent child) {
		if (child == null) {
			return PSize.ZERO_SIZE;
		}
		return PCompUtil.getPreferredSizeOf(child);
	}
	
	protected boolean constraintsAreEqual(Object constr1, Object constr2) {
		if (constr1 == null) {
			return constr2 == null;
		}
		return constr1.equals(constr2);
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
		obsList.add(obs);
	}
	
	public void removeObs(PLayoutObs obs) throws NullPointerException {
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
	
	/*
	 * Utility classes
	 */
	
	protected static class PCompInfo {
		
		protected final PComponent comp;
		protected final MutablePBounds bounds;
		protected Object constr;
		
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