package edu.udo.piq.components;

import java.util.function.Function;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PRenderer;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.PSpinnerButton.PSpinnerBtnDir;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.layouts.PSpinnerLayout;
import edu.udo.piq.layouts.PSpinnerLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class PSpinner extends AbstractPLayoutOwner {
	
	/*
	 * Input: Press Up
	 * If the UP key is pressed while the spinner has focus, a model and
	 * is enabled, the next value of the spinners model will be selected.
	 */
	public static final Object KEY_NEXT = StandardComponentActionKey.MOVE_NEXT;
	public static final PAccelerator ACCELERATOR_NEXT = new PAccelerator(
			ActualKey.UP, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_NEXT = new FocusOwnerAction<>(
			PSpinner.class, false,
			ACCELERATOR_NEXT,
			self -> self.isEnabled() && self.getModel().hasNext(),
			self -> self.selectNext());
	
//	public static final String INPUT_IDENTIFIER_PRESS_UP = "pressUp";
//	public static final PAccelerator<PSpinner> INPUT_PRESS_UP = new DefaultPAccelerator<>(
//			FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS, ActualKey.UP, PSpinner::canSelectNextOrPrevious);
//	public static final Consumer<PSpinner> REACTION_PRESS_UP = PSpinner::selectNext;
//
//	/*
//	 * Input: Press Down
//	 * If the DOWN key is pressed while the spinner has focus, a model and
//	 * is enabled, the previous value of the spinners model will be selected.
//	 */
//	public static final String INPUT_IDENTIFIER_PRESS_DOWN = "pressDown";
//	public static final PAccelerator<PSpinner> INPUT_PRESS_DOWN = new DefaultPAccelerator<>(
//			FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS, ActualKey.DOWN, PSpinner::canSelectNextOrPrevious);
//	public static final Consumer<PSpinner> REACTION_PRESS_DOWN = PSpinner::selectPrevious;
	
	protected final ObserverList<PSpinnerModelObs> modelObsList =
			PiqUtil.createDefaultObserverList();
	protected final PSpinnerModelObs modelObs = this::onModelValueChanged;
	protected PSpinnerModel model;
	protected Function<Object, String> encoder;
	protected Function<String, Object> decoder;
	
	public PSpinner(PSpinnerModel model) {
		this();
		setModel(model);
	}
	
	public PSpinner() {
		setLayout(new PSpinnerLayout(this));
		getLayoutInternal().addChild(new PSpinnerEditor(), Constraint.EDITOR);
		getLayoutInternal().addChild(new PSpinnerButton(PSpinnerBtnDir.NEXT), Constraint.BTN_NEXT);
		getLayoutInternal().addChild(new PSpinnerButton(PSpinnerBtnDir.PREV), Constraint.BTN_PREV);
		
		setModel(PModelFactory.createModelFor(this, PSpinnerModelInt::new, PSpinnerModel.class));
		
//		defineInput(INPUT_IDENTIFIER_PRESS_UP, INPUT_PRESS_UP, REACTION_PRESS_UP);
//		defineInput(INPUT_IDENTIFIER_PRESS_DOWN, INPUT_PRESS_DOWN, REACTION_PRESS_DOWN);
	}
	
	@Override
	protected void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PSpinnerLayout.class,
				"! layout instanceof PSpinnerLayout");
		super.setLayout(layout);
	}
	
	@Override
	protected PSpinnerLayout getLayoutInternal() {
		return (PSpinnerLayout) super.getLayout();
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		if (getEditor() != null) {
			getEditor().setOutputEncoder(getOutputEncoder());
		}
		if (getNextButton() != null) {
			getNextButton().setOutputEncoder(getOutputEncoder());
		}
		if (getPrevButton() != null) {
			getPrevButton().setOutputEncoder(getOutputEncoder());
		}
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
	public void setInputDecoder(Function<String, Object> inputDecoder) {
		decoder = inputDecoder;
		if (getEditor() != null) {
			getEditor().setInputDecoder(getInputDecoder());
		}
		if (getNextButton() != null) {
			getNextButton().setInputDecoder(getInputDecoder());
		}
		if (getPrevButton() != null) {
			getPrevButton().setInputDecoder(getInputDecoder());
		}
	}
	
	public Function<String, Object> getInputDecoder() {
		return decoder;
	}
	
	public PSpinnerEditor getEditor() {
		return (PSpinnerEditor) getLayoutInternal().getEditor();
	}
	
	public PSpinnerButton getNextButton() {
		return (PSpinnerButton) getLayoutInternal().getNextButton();
	}
	
	public PSpinnerButton getPrevButton() {
		return (PSpinnerButton) getLayoutInternal().getPrevButton();
	}
	
	public void setModel(PSpinnerModel model) {
		if (getModel() != null) {
			modelObsList.forEach(obs -> getModel().removeObs(obs));
		}
		this.model = model;
		if (getModel() != null) {
			modelObsList.forEach(obs -> getModel().addObs(obs));
		}
		setModel(getLayoutInternal().getEditor());
		setModel(getLayoutInternal().getNextButton());
		setModel(getLayoutInternal().getPrevButton());
	}
	
	private void setModel(PSpinnerPart part) {
		if (part != null) {
			part.setSpinnerModel(getModel());
		}
	}
	
	public PSpinnerModel getModel() {
		return model;
	}
	
	public void setEnabled(boolean value) {
		PSpinnerModel model = getModel();
		if (model != null) {
			model.setEnabled(value);
		}
	}
	
	public boolean isEnabled() {
		PSpinnerModel model = getModel();
		if (model == null) {
			return false;
		}
		return model.isEnabled();
	}
	
	public void selectNext() {
		if (!isEnabled()) {
			return;
		}
		PSpinnerModel model = getModel();
		if (!model.hasNext()) {// model != null because isEnabled returned true!
			return;
		}
		Object value = model.getNext();
		if (model.canSetValue(value)) {
			model.setValue(value);
		}
	}
	
	public void selectPrevious() {
		if (!isEnabled()) {
			return;
		}
		PSpinnerModel model = getModel();
		if (!model.hasPrevious()) {// model != null because isEnabled returned true!
			return;
		}
		Object value = model.getPrevious();
		if (model.canSetValue(value)) {
			model.setValue(value);
		}
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		PComponent editor = getEditor();
		PComponent btnNext = getNextButton();
		PComponent btnPrev = getPrevButton();
		return editor != null && editor.fillsAllPixels()
				&& btnNext != null && btnNext.fillsAllPixels()
				&& btnPrev != null && btnPrev.fillsAllPixels();
	}
	
	public void addObs(PSpinnerModelObs obs) throws NullPointerException {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSpinnerModelObs obs) throws NullPointerException {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void onModelValueChanged(PSpinnerModel model, Object oldVal) {
		modelObsList.fireEvent((obs) -> obs.onValueChanged(getModel(), oldVal));
	}
	
	@Override
	public void takeFocus() {
		if (getEditor() != null) {
			getEditor().takeFocus();
		} else {
			super.takeFocus();
		}
	}
	
	@Override
	public void tryToTakeFocus() {
		if (getEditor() != null) {
			getEditor().tryToTakeFocus();
		} else {
			super.tryToTakeFocus();
		}
	}
	
}