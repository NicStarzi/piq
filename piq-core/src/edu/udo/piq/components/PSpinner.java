package edu.udo.piq.components;

import java.util.function.Consumer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PLayout;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpinner.PSpinnerButton.PSpinnerBtnDir;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.components.util.DefaultPKeyInput;
import edu.udo.piq.components.util.ObjToStr;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.FocusPolicy;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.layouts.PSpinnerLayout;
import edu.udo.piq.layouts.PSpinnerLayout.Constraint;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PSpinner extends AbstractPInputLayoutOwner {
	
	/*
	 * Input: Press Up
	 * If the UP key is pressed while the spinner has focus, a model and 
	 * is enabled, the next value of the spinners model will be selected.
	 */
	public static final String INPUT_IDENTIFIER_PRESS_UP = "pressUp";
	public static final PKeyInput<PSpinner> INPUT_PRESS_UP = new DefaultPKeyInput<>(
			FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS, Key.UP, PSpinner::canTriggerUpOrDown);
	public static final Consumer<PSpinner> REACTION_PRESS_UP = self -> self.selectNext();
	
	/*
	 * Input: Press Down
	 * If the DOWN key is pressed while the spinner has focus, a model and 
	 * is enabled, the previous value of the spinners model will be selected.
	 */
	public static final String INPUT_IDENTIFIER_PRESS_DOWN = "pressDown";
	public static final PKeyInput<PSpinner> INPUT_PRESS_DOWN = new DefaultPKeyInput<>(
			FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS, Key.DOWN, PSpinner::canTriggerUpOrDown);
	public static final Consumer<PSpinner> REACTION_PRESS_DOWN = self -> self.selectPrevious();
	
	protected static boolean canTriggerUpOrDown(PSpinner self) {
		return self.isEnabled() && self.getModel() != null;
	}
	
	protected final ObserverList<PSpinnerModelObs> modelObsList = 
			PCompUtil.createDefaultObserverList();
	protected final PSpinnerModelObs modelObs = this::onModelValueChanged;
	protected PSpinnerModel model;
	protected ObjToStr encoder;
	
	public PSpinner(PSpinnerModel model) {
		this();
		setModel(model);
	}
	
	public PSpinner() {
		setLayout(new PSpinnerLayout(this));
		getLayoutInternal().addChild(new PSpinnerEditor(), Constraint.EDITOR);
		getLayoutInternal().addChild(new PSpinnerButton(PSpinnerBtnDir.NEXT), Constraint.BTN_NEXT);
		getLayoutInternal().addChild(new PSpinnerButton(PSpinnerBtnDir.PREV), Constraint.BTN_PREV);
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PSpinnerModel defaultModel = new PSpinnerModelInt();
		if (modelFac != null) {
			defaultModel = (PSpinnerModel) modelFac.getModelFor(this, defaultModel);
		}
		setModel(defaultModel);
		
		defineInput(INPUT_IDENTIFIER_PRESS_UP, INPUT_PRESS_UP, REACTION_PRESS_UP);
		defineInput(INPUT_IDENTIFIER_PRESS_DOWN, INPUT_PRESS_DOWN, REACTION_PRESS_DOWN);
	}
	
	protected void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PSpinnerLayout.class, 
				"! layout instanceof PSpinnerLayout");
		super.setLayout(layout);
	}
	
	protected PSpinnerLayout getLayoutInternal() {
		return (PSpinnerLayout) super.getLayout();
	}
	
	public void setOutputEncoder(ObjToStr outputEncoder) {
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
	
	public ObjToStr getOutputEncoder() {
		return encoder;
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
			for (PSpinnerModelObs obs : modelObsList) {
				getModel().removeObs(obs);
			}
		}
		this.model = model;
		if (getModel() != null) {
			for (PSpinnerModelObs obs : modelObsList) {
				getModel().addObs(obs);
			}
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
	
	public void selectNext() {
		PSpinnerModel model = getModel();
		if (!model.hasNext()) {
			return;
		}
		Object value = model.getNext();
		if (model.canSetValue(value)) {
			model.setValue(value);
		}
	}
	
	public void selectPrevious() {
		PSpinnerModel model = getModel();
		if (!model.hasPrevious()) {
			return;
		}
		Object value = model.getPrevious();
		if (model.canSetValue(value)) {
			model.setValue(value);
		}
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	public boolean defaultFillsAllPixels() {
		PComponent editor = getEditor();
		PComponent btnNext = getNextButton();
		PComponent btnPrev = getPrevButton();
		return editor != null && PCompUtil.fillsAllPixels(editor)
				&& btnNext != null && PCompUtil.fillsAllPixels(btnNext)
				&& btnPrev != null && PCompUtil.fillsAllPixels(btnPrev);
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
	
	public static class PSpinnerEditor extends PTextField implements PSpinnerPart {
		
		protected final PSpinnerModelObs spnrModelObs = 
				(model, oldVal) -> synchronizeModelValue();
		protected PSpinnerModel spnrModel;
		protected ObjToStr encoder;
		
		public PSpinnerEditor() {
			super("");
			
			addObs((PTextFieldObs) (self) -> onUserInput());
		}
		
		public void setOutputEncoder(ObjToStr outputEncoder) {
			encoder = outputEncoder;
			synchronizeModelValue();
		}
		
		public ObjToStr getOutputEncoder() {
			return encoder;
		}
		
		public void setSpinnerModel(PSpinnerModel model) {
			if (getSpinnerModel() != null) {
				getSpinnerModel().removeObs(spnrModelObs);
			}
			spnrModel = model;
			if (getSpinnerModel() == null) {
				getModel().setValue("");
				setEditable(false);
			} else {
				synchronizeModelValue();
				getSpinnerModel().addObs(spnrModelObs);
			}
		}
		
		public PSpinnerModel getSpinnerModel() {
			return spnrModel;
		}
		
		public PSpinner getSpinner() {
			if (getParent() instanceof PSpinner) {
				return (PSpinner) getParent();
			}
			return null;
		}
		
		public boolean isEnabled() {
			PSpinner parent = getSpinner();
			if (parent != null) {
				return parent.isEnabled();
			}
			return super.isEnabled();
		}
		
		protected void onUserInput() {
			String value = getText();
			PSpinnerModel model = getSpinnerModel();
			if (model != null && model.canSetValue(value)) {
				model.setValue(value);
			}
			synchronizeModelValue();
		}
		
		protected void synchronizeModelValue() {
			if (getModel() != null) {
				Object output;
				if (getOutputEncoder() == null) {
					output = getSpinnerModel().getValue();
				} else {
					try {
						output = getOutputEncoder().parse(getSpinnerModel().getValue());
					} catch (Exception e) {
						e.printStackTrace();
						output = null;
					}
				}
				getModel().setValue(output);
			}
		}
	}
	
	public static class PSpinnerButton extends PButton implements PSpinnerPart {
		
		protected static final PSize PREF_SIZE = new ImmutablePSize(16, 12);
		
		protected final PSpinnerModelObs spnrModelObs = 
				(model, oldVal) -> onSpinnerModelValueChanged();
		protected PSpinnerBtnDir dir;
		protected PSpinnerModel spnrModel;
		protected ObjToStr encoder; // <- not used
		
		public PSpinnerButton(PSpinnerBtnDir direction) {
			super();
			setSpinnerButtonOrientation(direction);
			setLayout(null);
			addObs((PClickObs) (btn) -> onClick());
			setRepeatTimer(20, 5);
		}
		
		protected void onSpinnerModelValueChanged() {
			if (getPSpinnerBtnDir() == PSpinnerBtnDir.NEXT) {
				setEnabled(getSpinnerModel().hasNext());
			} else {
				setEnabled(getSpinnerModel().hasPrevious());
			}
		}
		
		protected void onClick() {
			PSpinnerModel model = getSpinnerModel();
			if (model == null) {
				return;
			}
			Object newVal;
			if (getPSpinnerBtnDir() == PSpinnerBtnDir.NEXT) {
				newVal = model.getNext();
			} else {
				newVal = model.getPrevious();
			}
			if (model.canSetValue(newVal)) {
				model.setValue(newVal);
			}
		}
		
		public void setContent(PComponent component) {
			throw new IllegalStateException(
					getClass().getSimpleName()+" does not support content.");
		}
		
		public PComponent getContent() {
			return null;
		}
		
		public void setOutputEncoder(ObjToStr outputEncoder) {
			encoder = outputEncoder;
		}
		
		public ObjToStr getOutputEncoder() {
			return encoder;
		}
		
		public void setSpinnerModel(PSpinnerModel model) {
			if (getSpinnerModel() != null) {
				getSpinnerModel().removeObs(spnrModelObs);
			}
			spnrModel = model;
			if (getSpinnerModel() == null) {
				setEnabled(false);
			} else {
				onSpinnerModelValueChanged();
				getSpinnerModel().addObs(spnrModelObs);
			}
		}
		
		public PSpinnerModel getSpinnerModel() {
			return spnrModel;
		}
		
		public void setSpinnerButtonOrientation(PSpinnerBtnDir direction) {
			dir = direction;
			fireReRenderEvent();
		}
		
		public PSpinnerBtnDir getPSpinnerBtnDir() {
			return dir;
		}
		
		public boolean isEnabled() {
			PComponent parent = getParent();
			if (parent instanceof PSpinner) {
				return ((PSpinner) parent).isEnabled();
			}
			return super.isEnabled();
		}
		
		public void defaultRender(PRenderer renderer) {
			PBounds bnds = getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int fx = bnds.getFinalX();
			int fy = bnds.getFinalY();
			if (isEnabled()) {
				if (isPressed()) {
					renderer.setRenderMode(renderer.getRenderModeOutline());
					renderer.setColor(PColor.GREY25);
					renderer.drawQuad(x, y, fx, fy);
					renderer.setRenderMode(renderer.getRenderModeFill());
					renderer.setColor(PColor.GREY75);
					renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
				} else {
					renderer.setColor(PColor.BLACK);
					renderer.strokeBottom(x, y, fx, fy);
					renderer.strokeRight(x, y, fx, fy);
					renderer.setColor(PColor.WHITE);
					renderer.strokeTop(x, y, fx, fy);
					renderer.strokeLeft(x, y, fx, fy);
					renderer.setColor(PColor.GREY75);
					renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
				}
			} else {
				renderer.setRenderMode(renderer.getRenderModeOutline());
				renderer.setColor(PColor.GREY25);
				renderer.drawQuad(x, y, fx, fy);
				renderer.setRenderMode(renderer.getRenderModeFill());
				renderer.setColor(PColor.GREY50);
				renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
			}
			
			int triaX1 = x + 4;
			int triaY1 = y + 4;
			int triaX2 = x + (fx - x) / 2;
			int triaY2 = fy - 4;
			int triaX3 = fx - 4;
			int triaY3 = triaY1;
			if (getPSpinnerBtnDir() == PSpinnerBtnDir.NEXT) {
				int yUp = triaY1;
				int yDwn = triaY2;
				triaY1 = triaY3 = yDwn;
				triaY2 = yUp;
			}
			renderer.setColor(PColor.BLACK);
			renderer.drawTriangle(triaX1, triaY1, triaX2, triaY2, triaX3, triaY3);
		}
		
		public PSize getDefaultPreferredSize() {
			return PREF_SIZE;
		}
		
		public static enum PSpinnerBtnDir {
			NEXT,
			PREV,
			;
		}
	}
	
}