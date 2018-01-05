package edu.udo.piq.actions;

import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.udo.piq.PRoot;

public class MutableAction extends AbstractPComponentAction {
	
	protected Predicate<PRoot> enabledFunc;
	protected Consumer<PRoot> performAct;
	
	public void setEnabledValue(boolean constantEnabledValue) {
		setEnabledTest(root -> constantEnabledValue);
	}
	
	public void setEnabledTest(Predicate<PRoot> predicate) {
		enabledFunc = predicate;
		fireChangeEvent();
	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		return enabledFunc == null || enabledFunc.test(root);
	}
	
	public void setPerformAction(Runnable action) {
		if (action == null) {
			setPerformAction((Consumer<PRoot>) null);
		} else {
			setPerformAction(root -> action.run());
		}
	}
	
	public void setPerformAction(Consumer<PRoot> action) {
		performAct = action;
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		if (performAct != null) {
			performAct.accept(root);
		}
	}
	
	@Override
	public void fireChangeEvent() {
		super.fireChangeEvent();
	}
	
}