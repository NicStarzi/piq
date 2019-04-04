package edu.udo.piq.layouts;

import edu.udo.piq.PComponent;

public abstract class AbstractEnumPLayout<K extends Enum<K>> extends AbstractArrayPLayout implements PLayout {
	
	private final Class<K> enumClass;
	
	protected AbstractEnumPLayout(PComponent component, Class<K> enumClass) {
		super(component, enumClass.getEnumConstants().length);
		this.enumClass = enumClass;
	}
	
	public void setChildForConstraint(PComponent component, Object constraint) {
		PComponent oldChild = getChildForConstraint(constraint);
		if (oldChild == component) {
			return;
		}
		if (oldChild != null) {
			removeChild(constraint);
		}
		if (component != null) {
			addChild(component, constraint);
		}
	}
	
	@Override
	protected int getIndexFor(Object constr) {
		return getOrdinal(constr);
	}
	
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return enumClass.isInstance(constraint) 
				&& getChildForConstraint(constraint) == null;
	}
	
	protected int getOrdinal(PComponentLayoutData data) {
		return getOrdinal(data.getConstraint());
	}
	
	protected int getOrdinal(Object constr) {
		if (enumClass.isInstance(constr)) {
			@SuppressWarnings("unchecked")
			Enum<? extends K> e = (Enum<? extends K>) constr;
			return e.ordinal();
		}
		return -1;
	}
}