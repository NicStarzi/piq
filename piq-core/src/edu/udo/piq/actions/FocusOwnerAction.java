package edu.udo.piq.actions;

import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.util.Throw;

public class FocusOwnerAction<E extends PComponent> extends AbstractPComponentAction {
	
	protected final Class<E> compCls;
	protected final Predicate<E> cond;
	protected final Consumer<E> act;
	protected final boolean strongFocus;
	
	public FocusOwnerAction(Class<E> focusComponentClass, Consumer<E> action)
	{
		this(focusComponentClass, false, null, null, action);
	}
	
	public FocusOwnerAction(Class<E> focusComponentClass, PAccelerator accel,
			Consumer<E> action)
	{
		this(focusComponentClass, false, accel, null, action);
	}
	
	public FocusOwnerAction(Class<E> focusComponentClass, PAccelerator accel,
			Predicate<E> condition, Consumer<E> action)
	{
		this(focusComponentClass, false, accel, condition, action);
	}
	
	public FocusOwnerAction(Class<E> focusComponentClass, boolean checkStrongFocusOwner,
			PAccelerator accel, Predicate<E> condition, Consumer<E> action)
	{
		super(accel);
		Throw.ifNull(focusComponentClass, "focusComponentClass == null");
		compCls = focusComponentClass;
		strongFocus = checkStrongFocusOwner;
		cond = condition;
		act = action;
	}
	
	protected PComponent getFocusedComponent(PRoot root) {
		return strongFocus ? root.getLastStrongFocusOwner() : root.getFocusOwner();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isEnabled(PRoot root) {
		PComponent focusOwner = getFocusedComponent(root);
		return compCls.isInstance(focusOwner) && (cond == null || cond.test((E) focusOwner));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void tryToPerform(PRoot root) {
		if (act != null) {
			PComponent focusOwner = getFocusedComponent(root);
			act.accept((E) focusOwner);
		}
	}
	
}