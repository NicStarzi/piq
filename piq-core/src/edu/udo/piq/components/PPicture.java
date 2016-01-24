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
import edu.udo.piq.util.PCompUtil;

public class PPicture extends AbstractPComponent {
	
	protected final ObserverList<PPictureModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	private final PPictureModelObs modelObs = new PPictureModelObs() {
		public void onImagePathChanged(PPictureModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PPictureModel model;
	private boolean stretchToSize = false;
	
	public PPicture() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PPictureModel defaultModel = new DefaultPPictureModel();
		if (modelFac != null) {
			defaultModel = (PPictureModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
	}
	
	public PPicture(String initialPicturePath) {
		this();
		getModel().setImagePath(initialPicturePath);
	}
	
	public void setModel(PPictureModel model) {
		PPictureModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PPictureModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PPictureModelObs obs : modelObsList) {
				model.addObs(obs);
			}
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
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public PSize getDefaultPreferredSize() {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return PSize.ZERO_SIZE;
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
	
	public void addObs(PPictureModelObs obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PPictureModelObs obs) {
		modelObsList.remove(obs);
	}
	
}