package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.util.LinkedArray;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractArrayPLayout extends AbstractPLayout implements PLayout {
	
	private final LinkedArray<PComponentLayoutData> dataArray;
	private final int cap;
//	private List<PComponent> childrenList;
	
	protected AbstractArrayPLayout(PComponent component, int capacity) {
		super(component);
		cap = capacity;
		dataArray = new LinkedArray<>(cap);
	}
	
	protected String getErrorMsgIndexIllegal(int index) {
		return "illegal index";
	}
	
	protected int getCapacity() {
		return cap;
	}
	
	protected PComponentLayoutData getDataForIndex(int index) {
		return dataArray.get(index);
	}
	
	protected PComponent getComponentForIndex(int index) {
		PComponentLayoutData data = dataArray.get(index);
		if (data == null) {
			return null;
		}
		return data.getComponent();
	}
	
	@Override
	public int getChildCount() {
		return dataArray.size();
	}
	
	@Override
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, getCapacity(), index, getErrorMsgIndexIllegal(index));
		PComponentLayoutData data = dataArray.get(index);
		ThrowException.ifNull(data, "containsChild(child) == false");
		
		PComponent child = data.getComponent();
		child.removeObs(childObs);
		removeDataInternal(data);
		child.setParent(null);
		
		onChildRemoved(data);
		fireRemoveEvent(data);
	}
	
	@Override
	public PBounds getChildBounds(Object constraint)
			throws IllegalStateException, IllegalArgumentException
	{
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, getCapacity(), index, getErrorMsgIndexIllegal(index));
		ThrowException.ifNull(dataArray.get(index), "getChildForConstraint(constraint) == null");
		return dataArray.get(index).getComponentBounds();
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, getCapacity(), index, getErrorMsgIndexIllegal(index));
		if (dataArray.get(index) == null) {
			return null;
		}
		return dataArray.get(index).getComponent();
	}
	
	@Override
	public Iterable<PComponentLayoutData> getAllData() {
		return dataArray;
	}
	
	@Override
	protected void clearAllDataInternal() {
		dataArray.clear();
	}
	
	@Override
	protected void addDataInternal(PComponentLayoutData data) {
		int index = getIndexFor(data);
		ThrowException.ifNotWithin(0, getCapacity(), index, getErrorMsgIndexIllegal(index));
		dataArray.set(index, data);
	}
	
	@Override
	protected void removeDataInternal(PComponentLayoutData data) {
		int index = getIndexFor(data);
		ThrowException.ifNotWithin(0, getCapacity(), index, getErrorMsgIndexIllegal(index));
		dataArray.set(index, null);
	}
	
	protected abstract int getIndexFor(Object constr);
	
	protected int getIndexFor(PComponentLayoutData data) {
		return getIndexFor(data.getConstraint());
	}
}