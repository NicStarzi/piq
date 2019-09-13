package edu.udo.piq.components;

import java.util.function.Function;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class PSpinnerButton extends PButton implements PSpinnerPart {
	
	protected static final PSize PREF_SIZE = new ImmutablePSize(16, 12);
	
	protected final PSpinnerModelObs spnrModelObs =
			(model, oldVal) -> onSpinnerModelValueChanged();
	protected PSpinnerButton.PSpinnerBtnDir dir;
	protected PSpinnerModel spnrModel;
	
	public PSpinnerButton(PSpinnerButton.PSpinnerBtnDir direction) {
		super();
		setSpinnerButtonOrientation(direction);
		setLayout(null);
		addObs((PClickObs) (btn) -> onClick());
		setRepeatTimer(300, 100);
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
		Object newVal = null;
		boolean hasNewVal = false;
		if (getPSpinnerBtnDir() == PSpinnerBtnDir.NEXT) {
			if (model.hasNext()) {
				newVal = model.getNext();
				hasNewVal = true;
			}
		} else {
			if (model.hasPrevious()) {
				newVal = model.getPrevious();
				hasNewVal = true;
			}
		}
		if (hasNewVal && model.canSetValue(newVal)) {
			model.setValue(newVal);
		}
	}
	
	@Override
	public void setContent(PComponent component) {
		throw new IllegalStateException(
				getClass().getSimpleName()+" does not support content.");
	}
	
	@Override
	public PComponent getContent() {
		return null;
	}
	
	@Override
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
	}
	
	public Function<Object, String> getOutputEncoder() {
		return null;
	}
	
	@Override
	public void setInputDecoder(Function<String, Object> inputDecoder) {
	}
	
	public Function<String, Object> getInputDecoder() {
		return null;
	}
	
	@Override
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
	
	public void setSpinnerButtonOrientation(PSpinnerButton.PSpinnerBtnDir direction) {
		dir = direction;
		fireReRenderEvent();
	}
	
	public PSpinnerButton.PSpinnerBtnDir getPSpinnerBtnDir() {
		return dir;
	}
	
	@Override
	public boolean isEnabled() {
		PComponent parent = getParent();
		if (parent instanceof PSpinner) {
			return ((PSpinner) parent).isEnabled();
		}
		return super.isEnabled();
	}
	
	@Override
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
	
	@Override
	protected PSize getNoLayoutDefaultPreferredSize() {
		return PREF_SIZE;
	}
	
	public static enum PSpinnerBtnDir {
		NEXT,
		PREV,
		;
	}
}