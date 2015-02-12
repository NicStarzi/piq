package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
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
	private boolean stretchToSize = false;
	
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
	
	public void setStretchToSize(boolean isEnabled) {
		stretchToSize = isEnabled;
		fireReRenderEvent();
	}
	
	public boolean isStretchToSizeEnabled() {
		return stretchToSize;
	}
	
	public void defaultRender(PRenderer renderer) {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return;
		}
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		if (isStretchToSizeEnabled()) {
			int fx = bounds.getFinalX();
			int fy = bounds.getFinalY();
			renderer.drawImage(imgRes, x, y, fx, fy);
		} else {
			PSize imgSize = imgRes.getSize();
			int imgW = imgSize.getWidth();
			int imgH = imgSize.getHeight();
			renderer.drawImage(imgRes, x, y, x + imgW, y + imgH);
		}
	}
	
	public boolean fillsAllPixels() {
		return false;
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
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchImageResource(model.getImagePath());
	}
	
}