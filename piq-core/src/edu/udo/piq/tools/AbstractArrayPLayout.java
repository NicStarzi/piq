package edu.udo.piq.tools;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.util.LinkedArray;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractArrayPLayout extends AbstractPLayout implements PLayout {
	
	private final LinkedArray<PCompInfo> infoArray;
	private final int cap;
	private List<PComponent> childrenList;
	
	protected AbstractArrayPLayout(PComponent component, int capacity) {
		super(component);
		cap = capacity;
		infoArray = new LinkedArray<>(cap);
	}
	
	protected String getErrorMsgIndexIllegal(int index) {
		return "illegal index";
	}
	
	protected int getCapacity() {
		return cap;
	}
	
	protected PCompInfo getInfoAt(int index) {
		return infoArray.get(index);
	}
	
	protected PComponent getCompAt(int index) {
		PCompInfo info = infoArray.get(index);
		if (info == null) {
			return null;
		}
		return info.comp;
	}
	
	@Override
	public boolean isEmpty() {
		return childrenList == null || childrenList.isEmpty();
	}
	
	@Override
	public int getChildCount() {
		if (childrenList == null) {
			return 0;
		}
		return childrenList.size();
	}
	
	@Override
	public Collection<PComponent> getChildren() {
		if (childrenList == null) {
			childrenList = new CompList(infoArray);
		}
		return childrenList;
	}
	
	@Override
	protected PCompInfo getInfoFor(PComponent child) {
		for (PCompInfo info : infoArray) {
			if (info.comp == child) {
				return info;
			}
		}
		return null;
	}
	
	@Override
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		PCompInfo info = infoArray.get(index);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		PComponent child = info.comp;
		child.removeObs(childObs);
		removeInfoInternal(info);
		child.setParent(null);
		
		onChildRemoved(info);
		fireRemoveEvent(child, constraint);
	}
	
	@Override
	public PBounds getChildBounds(Object constraint)
			throws IllegalStateException, IllegalArgumentException
	{
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		ThrowException.ifNull(infoArray.get(index), "getChildForConstraint(constraint) == null");
		return infoArray.get(index).bounds;
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		if (infoArray.get(index) == null) {
			return null;
		}
		return infoArray.get(index).comp;
	}
	
	@Override
	protected Iterable<PCompInfo> getAllInfos() {
		return infoArray;
	}
	
	@Override
	protected void clearAllInfosInternal() {
		infoArray.clear();
	}
	
	@Override
	protected void addInfoInternal(PCompInfo info) {
		int index = getIndexFor(info);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		infoArray.set(index, info);
	}
	
	@Override
	protected void removeInfoInternal(PCompInfo info) {
		int index = getIndexFor(info);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		infoArray.set(index, null);
	}
	
	protected abstract int getIndexFor(Object constr);
	
	protected int getIndexFor(PCompInfo info) {
		return getIndexFor(info.constr);
	}
	
	protected static class CompList extends AbstractList<PComponent> {
		
		private final LinkedArray<PCompInfo> arr;
		
		public CompList(LinkedArray<PCompInfo> compArr) {
			arr = compArr;
		}
		
		@Override
		public PComponent get(int index) {
			PCompInfo info = arr.get(index);
			if (info == null) {
				return null;
			}
			return info.comp;
		}
		
		@Override
		public int size() {
			return arr.size();
		}
		
		@Override
		public Iterator<PComponent> iterator() {
			Iterator<PCompInfo> iter = arr.iterator();
			return new Iterator<PComponent>() {
				@Override
				public PComponent next() {
					return iter.next().comp;
				}
				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}
			};
		}
		
	}
}