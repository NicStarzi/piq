package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPPictureModel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PPicture extends AbstractPComponent {
	
	public static final AlignmentX DEFAULT_ALIGNMENT_X = AlignmentX.CENTER;
	public static final AlignmentY DEFAULT_ALIGNMENT_Y = AlignmentY.CENTER;
	
	protected final ObserverList<PSingleValueModelObs<Object>> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final PSingleValueModelObs<Object> modelObs = this::onModelChanged;
	protected PPictureModel model;
	protected AlignmentX alignX = DEFAULT_ALIGNMENT_X;
	protected AlignmentY alignY = DEFAULT_ALIGNMENT_Y;
	
	public PPicture() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPPictureModel::new, PPictureModel.class));
	}
	
	public PPicture(Object initialPictureID) {
		this();
		getModel().setValue(initialPictureID);
	}
	
	public PPicture(PPictureModel initialModel) {
		setModel(initialModel);
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
	
	public void setModelValue(Object value) {
		if (getModel() == null) {
			return;
		}
		getModel().setValue(value);
	}
	
	public void setAlignment(AlignmentX alignmentX, AlignmentY alignmentY) {
		setAlignmentX(alignmentX);
		setAlignmentY(alignmentY);
	}
	
	public void setAlignmentX(AlignmentX value) {
		if (alignX != value) {
			alignX = value;
			fireReRenderEvent();
		}
	}
	
	public AlignmentX getAlignmentX() {
		return alignX;
	}
	
	public void setAlignmentY(AlignmentY value) {
		if (alignY != value) {
			alignY = value;
			fireReRenderEvent();
		}
	}
	
	public AlignmentY getAlignmentY() {
		return alignY;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PImageResource imgRes = getImageResource();
		if (imgRes == null) {
			return;
		}
		AlignmentX alignX = getAlignmentX();
		AlignmentY alignY = getAlignmentY();
		
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		
		PSize imgSize = imgRes.getSize();
		int imgW = imgSize.getWidth();
		int imgH = imgSize.getHeight();
		int imgX = alignX.getLeftX(x, w, imgW);
		int imgFx = imgX + alignX.getWidth(x, w, imgW);
		int imgY = alignY.getTopY(y, h, imgH);
		int imgFy = imgY + alignY.getHeight(y, h, imgH);
		
		renderer.drawImage(imgRes, imgX, imgY, imgFx, imgFy);
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
	
	public void addObs(PSingleValueModelObs<Object> obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PSingleValueModelObs<Object> obs) {
		modelObsList.remove(obs);
	}
	
	protected void onModelChanged(PSingleValueModel<Object> model, Object oldValue, Object newValue) {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
}