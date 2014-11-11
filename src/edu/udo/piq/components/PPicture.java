package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPPictureModel;
import edu.udo.piq.tools.AbstractPComponent;

public class PPicture extends AbstractPComponent {
	
	private final PPictureModelObs modelObs = new PPictureModelObs() {
		public void imagePathChanged(PPictureModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PPictureModel model = new DefaultPPictureModel();
	
	public PPicture() {
		setModel(model);
	}
	
	public void setModel(PPictureModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PPictureModel getModel() {
		return model;
	}
	
	public void defaultRender(PRenderer renderer) {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return;
		}
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		renderer.drawImage(imgRes, x, y, fx, fy);
	}
	
	public PSize getDefaultPreferredSize() {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return PSize.NULL_SIZE;
		}
		return imgRes.getSize();
	}
	
	protected PImageResource getImageResource() {
		PPictureModel model = getModel();
		if (model == null || model.getImagePath() == null) {
			return null;
		}
		return getRoot().fetchImageResource(model.getImagePath());
	}
	
}