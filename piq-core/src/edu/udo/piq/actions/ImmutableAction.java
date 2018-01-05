package edu.udo.piq.actions;

import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.udo.piq.PRoot;
import edu.udo.piq.util.Throw;

public class ImmutableAction extends AbstractPComponentAction {
	
	protected final Predicate<PRoot> enabledFunc;
	protected final Consumer<PRoot> performAct;
	
	public ImmutableAction(PAccelerator accelerator, Predicate<PRoot> enabledFunction, Consumer<PRoot> action) {
		super(accelerator);
		Throw.ifNull(enabledFunction, "enabledFunction == null");
		Throw.ifNull(action, "action == null");
		enabledFunc = enabledFunction;
		performAct = action;
	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		return enabledFunc.test(root);
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		performAct.accept(root);
	}
	
}