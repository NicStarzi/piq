package edu.udo.piq.tools;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.util.LinkedArray;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractEnumPLayout<E extends Enum<E>> extends AbstractPLayout implements PLayout {
	
	private final Class<E> enumClass;
	private final List<PComponent> compList;
	private final LinkedArray<PCompInfo> infoArray;
	
	protected AbstractEnumPLayout(PComponent component, Class<E> enumClass) {
		super(component);
		this.enumClass = enumClass;
		
		int enumCount = enumClass.getEnumConstants().length;
		infoArray = new LinkedArray<>(enumCount);
		compList = new ArrayList<>(enumCount);
	}
	
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	public Collection<PComponent> getChildren() {
		return compList;
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
		int index = getOrdinal(constraint);
		ThrowException.ifEqual(-1, index, getErrorMsgConstraintIllegal());
		PCompInfo info = infoArray.get(index);
		ThrowException.ifNull(info, "containsChild(child) == false");
		
		PComponent child = info.comp;
		removeInfoInternal(info);
		child.setParent(null);
		fireRemoveEvent(child, constraint);
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		int index = getOrdinal(constraint);
		ThrowException.ifEqual(-1, index, getErrorMsgConstraintIllegal());
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
		int index = getOrdinal(info);
		ThrowException.ifEqual(-1, index, getErrorMsgConstraintIllegal());
		infoArray.set(index, info);
		compList.add(info.comp);
	}
	
	protected void removeInfoInternal(PCompInfo info) {
		int index = getOrdinal(info);
		ThrowException.ifEqual(-1, index, getErrorMsgConstraintIllegal());
		infoArray.set(index, null);
		compList.remove(info.comp);
	}
	
	protected int getOrdinal(PCompInfo info) {
		return getOrdinal(info.constr);
	}
	
	protected int getOrdinal(Object constr) {
		if (enumClass.isInstance(constr)) {
			@SuppressWarnings("unchecked")
			Enum<? extends E> e = (Enum<? extends E>) constr;
			return e.ordinal();
		}
		return -1;
	}
	
	protected static class CompList extends AbstractList<PComponent> {
		
		private final PCompInfo[] arr;
		
		public CompList(PCompInfo[] compArr) {
			arr = compArr;
		}
		
		public PComponent get(int index) {
			PCompInfo info = arr[index];
			if (info == null) {
				return null;
			}
			return info.comp;
		}
		
		public int size() {
			return arr.length;
		}
		
	}
}