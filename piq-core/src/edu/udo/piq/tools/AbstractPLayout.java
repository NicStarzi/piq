package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.PStyleLayout;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPLayout implements PLayout {
	
	protected final PComponentObs ownerObs = new PComponentObs() {
		@Override
		public void onBoundsChanged(PComponent component) {
			AbstractPLayout.this.onOwnerBoundsChanged();
		}
	};
	protected final PComponentObs childObs = new PComponentObs() {
		@Override
		public void onPreferredSizeChanged(PComponent component) {
			AbstractPLayout.this.onChildPrefSizeChanged(component);
		}
	};
	
	protected final ObserverList<PLayoutObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PComponent owner;
	private PStyleLayout style;
	private Object styleID = getClass();
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
	
	@Override
	public PComponent getOwner() {
		return owner;
	}
	
	@Override
	public void addChild(PComponent component, Object constraint) throws NullPointerException, IllegalArgumentException, IllegalStateException {
		ThrowException.ifNull(component, "component == null");
		ThrowException.ifFalse(canAdd(component, constraint), "canAdd(component, constraint) == false");
		ThrowException.ifTrue(containsChild(component), "containsChild(component) == true");
		
		PCompInfo info = new PCompInfo(component, constraint);
		addInfoInternal(info);
		component.setParent(getOwner());
		component.addObs(childObs);
		
		onChildAdded(component, constraint);
		fireAddEvent(component, constraint);
		invalidate();
	}
	
	@Override
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException {
		ThrowException.ifNull(child, "child == null");
		
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		Object constraint = info.constr;
		
		child.removeObs(childObs);
		removeInfoInternal(info);
		child.setParent(null);
		
		onChildRemoved(info);
		fireRemoveEvent(child, constraint);
	}
	
	@Override
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		PComponent child = getChildForConstraint(constraint);
		ThrowException.ifNull(child, "containsChild(constraint) == false");
		removeChild(child);
	}
	
	@Override
	public void clearChildren() {
		for (PCompInfo info : getAllInfos()) {
			PComponent child = info.comp;
			Object constraint = info.constr;
			child.setParent(null);
			
			onChildCleared(child, constraint);
			fireRemoveEvent(child, constraint);
		}
		clearAllInfosInternal();
	}
	
	@Override
	public boolean containsChild(PComponent child) throws NullPointerException {
		return getOwner() == child.getParent();
	}
	
	@Override
	public boolean containsChild(Object constraint) throws IllegalArgumentException {
		return getChildForConstraint(constraint) != null;
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		for (PCompInfo info : getAllInfos()) {
			if (constraintsAreEqual(info.constr, constraint)) {
				return info.comp;
			}
		}
		return null;
	}
	
	@Override
	public PComponent getChildAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		}
		if (isEmpty()) {
			return null;
		}
		for (PCompInfo info : getAllInfos()) {
			if (info.comp.isIgnoredByPicking()) {
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
	
	@Override
	public PBounds getChildBounds(Object constraint)
			throws IllegalStateException, IllegalArgumentException
	{
		PComponent child = getChildForConstraint(constraint);
		if (child == null) {
			throw new IllegalStateException("getChildForConstraint(constraint) == null");
		}
		return getChildBounds(child);
	}
	
	@Override
	public PBounds getChildBounds(PComponent child) throws NullPointerException, IllegalArgumentException {
		ThrowException.ifNull(child, "child == null");
		PCompInfo info = getInfoFor(child);
		ThrowException.ifNull(info, "containsChild(child) == false");
		return info.bounds;
	}
	
	@Override
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
		LayoutPBounds bnds = info.bounds;
		if (bnds.x != x || bnds.y != y || bnds.w != width || bnds.h != height) {
			bnds.set(x, y, width, height);
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
		return child.getPreferredSize();
	}
	
	protected boolean constraintsAreEqual(Object constr1, Object constr2) {
		return constr1 == null ? constr2 == null : constr1.equals(constr2);
	}
	
	@Override
	public void setStyle(PStyleLayout style) {
		this.style = style;
		invalidate();
	}
	
	@Override
	public PStyleLayout getStyle() {
		return style;
	}
	
	@Override
	public Object getStyleID() {
		return styleID;
	}
	
	@Override
	public void invalidate() {
		pushState(DebugState.INVALIDATED);
		invalidated = true;
		fireInvalidateEvent();
	}
	
	@Override
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
	
	@Override
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
		invalidate();
	}
	
	protected void onOwnerBoundsChanged() {
		invalidate();
	}
	
	protected void onInvalidated() {}
	
	protected void onChildAdded(PComponent child, Object constraint) {}
	
	protected void onChildRemoved(PCompInfo removedCompInfo) {
		invalidate();
	}
	
	protected void onChildCleared(PComponent child, Object constraint) {}
	
	/*
	 * Observers & Events
	 */
	
	@Override
	public void addObs(PLayoutObs obs) throws NullPointerException {
		obsList.add(obs);
	}
	
	@Override
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
	
	@Override
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
		protected Object constr;
		final LayoutPBounds bounds;
		
		public PCompInfo(PComponent component, Object constraint) {
			comp = component;
			constr = constraint;
			bounds = new LayoutPBounds();
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
	
	private static class LayoutPBounds extends AbstractPBounds implements PBounds {
		
		protected int x;
		protected int y;
		protected int w;
		protected int h;
		
		protected void set(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		@Override
		public int getX() {
			return x;
		}
		
		@Override
		public int getY() {
			return y;
		}
		
		@Override
		public int getWidth() {
			return w;
		}
		
		@Override
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
//		if (curState == DebugState.LAYOUT || curState == DebugState.ON_INVALIDATED) {
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