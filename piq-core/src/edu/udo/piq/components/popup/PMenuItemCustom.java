package edu.udo.piq.components.popup;

import java.util.function.Consumer;
import java.util.function.Function;

import edu.udo.piq.PRoot;

public class PMenuItemCustom extends AbstractPMenuItem {
	
	protected Function<PRoot, Object> labelFunc;
	protected Function<PRoot, Object> iconFunc;
	protected Function<PRoot, Boolean> enableFunc;
	protected Consumer<PRoot> action;
	
	public PMenuItemCustom(Object labelValue, Consumer<PRoot> action) {
		this(root -> labelValue, null, null, action);
	}
	
	public PMenuItemCustom(Object labelValue, Object iconValue, Consumer<PRoot> action) {
		this(root -> labelValue, root -> iconValue, null, action);
	}
	
	public PMenuItemCustom(Function<PRoot, Object> labelFunction,
			Function<PRoot, Object> iconFunction,
			Function<PRoot, Boolean> enableFunction,
			Consumer<PRoot> action)
	{
		labelFunc = labelFunction;
		iconFunc = iconFunction;
		enableFunc = enableFunction;
		this.action = action;
	}
	
	@Override
	protected void onAddedToUi(PRoot newRoot) {
		refreshComponents();
	}
	
	protected void refreshComponents() {
		PRoot root = getRoot();
		boolean enabled = isEnabled();
		
		Object labelVal = labelFunc == null ? null : labelFunc.apply(root);
		Object iconVal = labelFunc == null ? null : labelFunc.apply(root);
		
		compIcon.setModelValue(labelVal);
		compIcon.setEnabled(enabled);
		compLabel.setModelValue(iconVal);
		compLabel.setEnabled(enabled);
		
		compAccelerator.setModelValue(null);
		compAccelerator.setEnabled(enabled);
	}
	
	@Override
	public boolean isEnabled() {
		return enableFunc == null || enableFunc.apply(getRoot());
	}
	
	@Override
	public void performAction() {
		PRoot root = getRoot();
		if (action != null) {
			action.accept(root);
		}
		fireActionEvent();
	}
	
}