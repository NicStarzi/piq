package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpinner.PSpinnerButton.PSpinnerBtnDir;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.layouts.PSpinnerLayout;
import edu.udo.piq.layouts.PSpinnerLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ThrowException;

public class PSpinner extends AbstractPLayoutOwner {
	
	protected PSpinnerModel model;
	
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
	}
	
	protected void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PSpinnerLayout.class, 
				"! layout instanceof PSpinnerLayout");
		if (getLayout() != null) {
			getLayout().removeObs(layoutObs);
		}
		super.setLayout(layout);
		if (getLayout() != null) {
			getLayout().addObs(layoutObs);
		}
	}
	
	protected PSpinnerLayout getLayoutInternal() {
		return (PSpinnerLayout) super.getLayout();
	}
	
	public void setModel(PSpinnerModel model) {
		this.model = model;
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
	
	public static class PSpinnerEditor extends PTextField implements PSpinnerPart {
		
		protected final PSpinnerModelObs spnrModelObs = 
				(model, oldVal) -> onSpinnerModelValueChanged();
		protected PSpinnerModel spnrModel;
		
		public PSpinnerEditor() {
			super("");
			
			addObs((PTextFieldObs) (self) -> onUserInput());
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
				onSpinnerModelValueChanged();
				getSpinnerModel().addObs(spnrModelObs);
			}
		}
		
		public PSpinnerModel getSpinnerModel() {
			return spnrModel;
		}
		
		protected void onUserInput() {
			String value = getText();
			if (getSpinnerModel() != null 
					&& getSpinnerModel().canSetValue(value)) 
			{
				getSpinnerModel().setValue(value);
			} else {
				onSpinnerModelValueChanged();
			}
		}
		
		protected void onSpinnerModelValueChanged() {
			if (getModel() != null) {
				getModel().setValue(getSpinnerModel().getValue());
			}
		}
	}
	
	public static class PSpinnerButton extends PButton implements PSpinnerPart {
		
		protected static final PSize PREF_SIZE = new ImmutablePSize(16, 12);
		
		protected final PSpinnerModelObs spnrModelObs = 
				(model, oldVal) -> onSpinnerModelValueChanged();
		protected PSpinnerBtnDir dir;
		protected PSpinnerModel spnrModel;
		
		public PSpinnerButton(PSpinnerBtnDir direction) {
			super();
			setSpinnerButtonOrientation(direction);
			setLayout(null);
			addObs((PButtonObs) (btn) -> onClick());
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