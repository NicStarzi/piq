package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPLayout implements PLayout {
	
	protected final PComponentObs ownerObs = new PComponentObs() {
		public void onBoundsChanged(PComponent component) {
			onOwnerBoundsChanged();
		}
	};
	protected final PComponentObs childObs = new PComponentObs() {
		public void onPreferredSizeChanged(PComponent component) {
			onChildPrefSizeChanged(component);
		}
	};
	
	protected final ObserverList<PLayoutObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PComponent owner;
	private PLayoutDesign design;
	protected MutablePSize prefSize = new MutablePSize();
	protected boolean invalidated = true;
	
	protected AbstractPLayout(PComponent component) {
		ThrowException.ifNull(component, "component == null");
		owner = component;
		owner.addObs(ownerObs);
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
		component.addObs(childObs);
		
		component.setParent(getOwner());
		
		onChildAdded(component, constraint);
		fireAddEvent(component, constraint);
		invalidate();
	}
	
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException {
		ThrowException.ifNull(child, "child == null");
		
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		Object constraint = info.constr;
		
		child.removeObs(childObs);
		removeInfoInternal(info);
		child.setParent(null);
		
		onChildRemoved(child, constraint);
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
			
			onChildRemoved(child, constraint);
			fireRemoveEvent(child, constraint);
		}
		clearAllInfosInternal();
	}
	
	public boolean containsChild(PComponent child) throws NullPointerException {
		return getOwner() == child.getParent();
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
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		}
		if (isEmpty()) {
			return null;
		}
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
	
	public PBounds getChildBounds(Object constraint) 
			throws IllegalStateException, IllegalArgumentException 
	{
		PComponent child = getChildForConstraint(constraint);
		if (child == null) {
			throw new IllegalStateException("getChildForConstraint(constraint) == null");
		}
		return getChildBounds(child);
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
		return constr1 == null ? constr2 == null : constr1.equals(constr2);
	}
	
	public void setDesign(PLayoutDesign design) {
		this.design = design;
		invalidate();
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
	
	public void invalidate() {
		pushState(DebugState.INVALIDATED);
		invalidated = true;
		fireInvalidateEvent();
	}
	
	public void layOut() {
		if (getOwner().getBounds().isEmpty()) {
			return;
		}
		if (invalidated) {
			invalidated = false;
			pushState(DebugState.ON_INVALIDATED);
			onInvalidated();
		}
		pushState(DebugState.LAYOUT);
		layOutInternal();
	}
	
	protected abstract void layOutInternal();
	
	public PSize getPreferredSize() {
		if (invalidated) {
			invalidated = false;
			pushState(DebugState.ON_INVALIDATED);
			onInvalidated();
		}
		pushState(DebugState.GET_PREF_SIZE);
		return getPreferredSizeInternal();
	}
	
	protected PSize getPreferredSizeInternal() {
		return prefSize;
	}
	
	protected void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		invalidate();
	}
	
	protected void onOwnerBoundsChanged() {
		invalidate();
	}
	
	protected void onInvalidated() {}
	
	protected void onChildAdded(PComponent child, Object constraint) {}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		invalidate();
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
		obsList.fireEvent((obs) -> obs.onChildAdded(this, child, constraint));
	}
	
	protected void fireRemoveEvent(PComponent child, Object constraint) {
		obsList.fireEvent((obs) -> obs.onChildRemoved(this, child, constraint));
	}
	
	protected void fireLaidOutEvent(PComponent child, Object constraint) {
		obsList.fireEvent((obs) -> obs.onChildLaidOut(this, child, constraint));
	}
	
	protected void fireInvalidateEvent() {
		obsList.fireEvent((obs) -> obs.onLayoutInvalidated(this));
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
		
		public PComponent getComponent() {
			return comp;
		}
		
		public PBounds getBounds() {
			return bounds;
		}
		
		public Object getConstraint() {
			return constr;
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
	
//	private DebugState curState;
//	private int stateCounter;
//	private static int[] staticStateCounter = new int[DebugState.values().length];
//	
	private void pushState(DebugState state) {
		//FIXME: This is just for debugging purposes
//		boolean wasOnInv = curState == DebugState.ON_INVALIDATED;
//		boolean wasGPS = curState == DebugState.GET_PREF_SIZE;
//		if (getClass() != PRootLayout.class 
//				&& state == DebugState.LAYOUT && !(wasOnInv || wasGPS)) 
//		{
//			System.err.println("current="+curState+", next="+state);
//		}
//		if (curState == state) {
//			stateCounter++;
//		} else {
//			curState = state;
//			stateCounter = 1;
//		}
//		staticStateCounter[curState.ordinal()]++;
//		if (false) {//curState == DebugState.LAYOUT || curState == DebugState.ON_INVALIDATED
//			StringBuilder sb = new StringBuilder();
//			sb.append(getOwner());
//			sb.append(" ");
//			sb.append(curState);
//			sb.append(" (");
//			sb.append(stateCounter);
//			sb.append(")[");
//			sb.append(staticStateCounter[state.ordinal()]);
//			sb.append("]");
//			System.out.println(sb.toString());
//		}
	}
	
	private static enum DebugState {
		INVALIDATED,
		ON_INVALIDATED,
		LAYOUT,
		GET_PREF_SIZE,
		;
	}
	
}