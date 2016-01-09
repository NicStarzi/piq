package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;

public abstract class AbstractEnumPLayout<E extends Enum<E>> extends AbstractArrayPLayout implements PLayout {
	
	private final Class<E> enumClass;
	
	protected AbstractEnumPLayout(PComponent component, Class<E> enumClass) {
		super(component, enumClass.getEnumConstants().length);
		this.enumClass = enumClass;
	}
	
	protected int getIndexFor(Object constr) {
		return getOrdinal(constr);
	}
	
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return enumClass.isInstance(constraint) 
				&& getChildForConstraint(constraint) == null;
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
}