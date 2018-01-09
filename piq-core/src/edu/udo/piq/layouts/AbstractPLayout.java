package edu.udo.piq.layouts;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
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
		= PiqUtil.createDefaultObserverList();
	protected final PComponent owner;
//	private PStyleLayout style;
//	private Object styleID = getClass();
	protected MutablePSize prefSize = new MutablePSize();
	protected boolean invalidated = true;
	
	protected AbstractPLayout(PComponent component) {
		ThrowException.ifNull(component, "component == null");
		owner = component;
		owner.addObs(ownerObs);
	}
	
	protected abstract boolean canAdd(PComponent component, Object constraint);
	
	protected abstract void clearAllDataInternal();
	
	protected abstract void addDataInternal(PComponentLayoutData info);
	
	protected abstract void removeDataInternal(PComponentLayoutData info);
	
	@Override
	public PComponent getOwner() {
		return owner;
	}
	
	@CallSuper
	@Override
	public void addChild(PComponent component, Object constraint) throws NullPointerException, IllegalArgumentException, IllegalStateException {
		ThrowException.ifNull(component, "component == null");
		ThrowException.ifFalse(canAdd(component, constraint), "canAdd(component, constraint) == false");
		ThrowException.ifTrue(containsChild(component), "containsChild(component) == true");
		
		PComponentLayoutData data = new PComponentLayoutData(component, constraint);
		addDataInternal(data);
		component.setParent(getOwner());
		component.addObs(childObs);
		
		onChildAdded(data);
		fireAddEvent(data);
		invalidate();
	}
	
	@CallSuper
	@Override
	public void removeChild(PComponent child) throws NullPointerException, IllegalArgumentException {
		ThrowException.ifNull(child, "child == null");
		
		PComponentLayoutData data = getDataFor(child);
		ThrowException.ifNull(data, "containsChild(child) == false");
		
		child.removeObs(childObs);
		removeDataInternal(data);
		child.setParent(null);
		
		onChildRemoved(data);
		fireRemoveEvent(data);
	}
	
	@Override
	public void clearChildren() {
		for (PComponentLayoutData data : getAllData()) {
			PComponent child = data.getComponent();
			child.setParent(null);
			
			onChildCleared(data);
			fireRemoveEvent(data);
		}
		clearAllDataInternal();
	}
	
	protected void setChildCellFilled(PComponent child, int x, int y, int width, int height) {
		setChildCell(child, x, y, width, height, AlignmentX.FILL, AlignmentY.FILL);
	}
	
	protected void setChildCellFilled(PComponentLayoutData data, int x, int y, int width, int height) {
		setChildCell(data, x, y, width, height, AlignmentX.FILL, AlignmentY.FILL);
	}
	
	protected void setChildCellCentered(PComponent child, int x, int y, int width, int height) {
		setChildCell(child, x, y, width, height, AlignmentX.CENTER, AlignmentY.CENTER);
	}
	
	protected void setChildCellCentered(PComponentLayoutData data, int x, int y, int width, int height) {
		setChildCell(data, x, y, width, height, AlignmentX.CENTER, AlignmentY.CENTER);
	}
	
	protected void setChildCellPreferred(PComponent child, int x, int y, int width, int height) {
		setChildCell(child, x, y, width, height, 
				AlignmentX.PREFERRED_OR_CENTER, AlignmentY.PREFERRED_OR_CENTER);
	}
	
	protected void setChildCellPreferred(PComponentLayoutData data, int x, int y, int width, int height) {
		setChildCell(data, x, y, width, height, 
				AlignmentX.PREFERRED_OR_CENTER, AlignmentY.PREFERRED_OR_CENTER);
	}
	
	protected void setChildCell(PComponent child, 
			int x, int y, int width, int height, AlignmentX alignX, AlignmentY alignY) 
	{
		if (child == null) {
			return;
		}
		PComponentLayoutData data = getDataFor(child);
		ThrowException.ifNull(data, "containsChild(child) == false");
		setChildCell(data, x, y, width, height, alignX, alignY);
	}
	
	protected void setChildCell(PComponentLayoutData data, 
			int x, int y, int width, int height, AlignmentX alignX, AlignmentY alignY) 
	{
		data.setCellBySize(x, y, width, height, alignX, alignY);
		fireLaidOutEvent(data);
	}
	
	protected void setChildConstraint(PComponent child, Object constraint) {
		PComponentLayoutData data = getDataFor(child);
		ThrowException.ifNull(data, "containsChild(child) == false");
		data.setConstr(constraint);
	}
	
	protected PSize getPreferredSizeOf(PComponent child) {
		if (child == null) {
			return PSize.ZERO_SIZE;
		}
		return child.getPreferredSize();
	}
	
//	@Override
//	public void setInheritedStyle(PStyleLayout value) {
//		style = value;
//		invalidate();
//	}
//	
//	@Override
//	public PStyleLayout getInheritedStyle() {
//		return style;
//	}
//	
//	@Override
//	public Object getStyleID() {
//		return styleID;
//	}
	
	@CallSuper
	@Override
	public void invalidate() {
		invalidated = true;
		fireInvalidateEvent();
	}
	
	@CallSuper
	@Override
	public void layOut() {
		if (getOwner().getBounds().isEmpty()) {
			return;
		}
		if (invalidated) {
			invalidated = false;
			onInvalidated();
		}
		if (getChildCount() > 0) {
			layOutInternal();
		}
	}
	
	protected abstract void layOutInternal();
	
	@CallSuper
	@Override
	public PSize getPreferredSize() {
		if (invalidated) {
			invalidated = false;
			onInvalidated();
		}
		return getPreferredSizeInternal();
	}
	
	protected PSize getPreferredSizeInternal() {
		return prefSize;
	}
	
	@CallSuper
	@TemplateMethod
	protected void onChildPrefSizeChanged(PComponent child) {
		invalidate();
	}
	
	@CallSuper
	@TemplateMethod
	protected void onOwnerBoundsChanged() {
		invalidate();
	}
	
	@TemplateMethod
	protected void onInvalidated() {}
	
	@TemplateMethod
	protected void onChildAdded(PComponentLayoutData data) {}
	
	@CallSuper
	@TemplateMethod
	protected void onChildRemoved(PComponentLayoutData data) {
		invalidate();
	}
	
	@TemplateMethod
	protected void onChildCleared(PComponentLayoutData data) {}
	
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
	
	protected void fireAddEvent(PComponentLayoutData data) {
		obsList.fireEvent((obs) -> obs.onChildAdded(this, data));
	}
	
	protected void fireRemoveEvent(PComponentLayoutData data) {
		obsList.fireEvent((obs) -> obs.onChildRemoved(this, data));
	}
	
	protected void fireLaidOutEvent(PComponentLayoutData data) {
		obsList.fireEvent((obs) -> obs.onChildLaidOut(this, data));
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
		toString(builder, getChildren());
		builder.append("]");
		return builder.toString();
	}
	
	private void toString(StringBuilder sb, Iterable<?> iterable) {
		sb.append("[");
		for (Object elem : iterable) {
			sb.append(elem);
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
	}
	
}