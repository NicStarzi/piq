package edu.udo.piq.tools;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	
	protected PComponent getCompAt(int index) {
		PCompInfo info = infoArray.get(index);
		if (info == null) {
			return null;
		}
		return info.comp;
	}
	
	public Collection<PComponent> getChildren() {
		if (childrenList == null) {
			childrenList = new CompList(infoArray);
		}
		return childrenList;
	}
	
	protected PCompInfo getInfoFor(PComponent child) {
		for (PCompInfo info : infoArray) {
			if (info.comp == child) {
				return info;
			}
		}
		return null;
	}
	
	public void removeChild(Object constraint) throws IllegalArgumentException, IllegalStateException {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		PCompInfo info = infoArray.get(index);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		PComponent child = info.comp;
		removeInfoInternal(info);
		child.setParent(null);
		fireRemoveEvent(child, constraint);
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		int index = getIndexFor(constraint);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		if (infoArray.get(index) == null) {
			return null;
		}
		return infoArray.get(index).comp;
	}
	
	protected Iterable<PCompInfo> getAllInfos() {
		return infoArray;
	}
	
	protected void clearAllInfosInternal() {
		infoArray.clear();
	}
	
	protected void addInfoInternal(PCompInfo info) {
		int index = getIndexFor(info);
		ThrowException.ifNotWithin(0, cap, index, getErrorMsgIndexIllegal(index));
		infoArray.set(index, info);
	}
	
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
		
		public PComponent get(int index) {
			PCompInfo info = arr.get(index);
			if (info == null) {
				return null;
			}
			return info.comp;
		}
		
		public int size() {
			return arr.size();
		}
		
		public Iterator<PComponent> iterator() {
			Iterator<PCompInfo> iter = arr.iterator();
			return new Iterator<PComponent>() {
				public PComponent next() {
					return iter.next().comp;
				}
				public boolean hasNext() {
					return iter.hasNext();
				}
			};
		}
		
	}
}