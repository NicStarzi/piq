package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.defaults.DefaultPRadioButtonModel;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PRadioButton extends AbstractPInteractiveComponent implements PClickable {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	protected final ObserverList<PSingleValueModelObs<Boolean>> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onMouseMoved(PMouse mouse) {
			PRadioButton.this.onMouseMoved(mouse);
		}
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PRadioButton.this.onMouseButtonTriggered(mouse, btn, clickCount);
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			PRadioButton.this.onMouseButtonPressed(mouse, btn, clickCount);
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			PRadioButton.this.onMouseButtonReleased(mouse, btn, clickCount);
		}
	};
	protected final PSingleValueModelObs<Boolean> modelObs = this::onModelChange;
	protected PRadioButtonModel model;
	
	public PRadioButton() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPRadioButtonModel::new, PRadioButtonModel.class));
		addObs(mouseObs);
	}
	
	public void setModel(PRadioButtonModel model) {
		PRadioButtonModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			modelObsList.forEach(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
		}
		fireReRenderEvent();
	}
	
	public PRadioButtonModel getModel() {
		return model;
	}
	
	public void setSelected() {
		if (getModel() != null) {
			getModel().setSelected(true);
		}
	}
	
	public boolean isSelected() {
		if (getModel() == null) {
			return false;
		}
		return getModel().isSelected();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int w = bnds.getWidth();
		int h = bnds.getHeight();
		
		renderer.setColor(PColor.BLACK);
		renderer.drawEllipse(x, y, w, h);
		renderer.setColor(PColor.WHITE);
		renderer.drawEllipse(x + 1, y + 1, w - 2, h - 2);
		
		if (isSelected()) {
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(PColor.BLACK);
			renderer.drawEllipse(x + gapW, y + gapH, w - gapW * 2, h - gapH * 2);
		}
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	@Override
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	public void addObs(PSingleValueModelObs<Boolean> obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSingleValueModelObs<Boolean> obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent((obs) -> obs.onClick(this));
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
		if (btn == MouseButton.LEFT && isMouseOver(mouse)) {
			setSelected();
			fireClickEvent();
		}
	}
	
	@TemplateMethod
	protected void onModelChange(PSingleValueModel<Boolean> model, Boolean oldValue, Boolean newValue) {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
}