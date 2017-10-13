package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPPictureModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PPicture extends AbstractPComponent {
	
	protected final ObserverList<PPictureModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final PPictureModelObs modelObs = model -> PPicture.this.onImagePathChanged();
	protected PPictureModel model;
	protected boolean stretchToSize = false;
	
	public PPicture() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPPictureModel::new, PPictureModel.class));
	}
	
	public PPicture(Object initialPictureID) {
		this();
		getModel().setValue(initialPictureID);
	}
	
	public void setModel(PPictureModel model) {
		PPictureModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			modelObsList.forEach(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
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
	
	@Override
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
	
	@Override
	public boolean defaultFillsAllPixels() {
		PImageResource imgRes = getImageResource();
		return imgRes != null && imgRes.fillsAllPixels();
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return PSize.ZERO_SIZE;
		}
		return imgRes.getSize();
	}
	
	protected PImageResource getImageResource() {
		PPictureModel model = getModel();
		if (model == null) {
			return null;
		}
		Object imgID = model.getValue();
		if (imgID == null) {
			return null;
		}
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchImageResource(imgID);
	}
	
	public void addObs(PPictureModelObs obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PPictureModelObs obs) {
		modelObsList.remove(obs);
	}
	
	protected void onImagePathChanged() {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
}