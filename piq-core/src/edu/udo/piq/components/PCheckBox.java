package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PCheckBox extends AbstractPInteractiveComponent implements PClickable {
	
	public static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	protected final ObserverList<PSingleValueModelObs<Boolean>> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onMouseMoved(PMouse mouse) {
			PCheckBox.this.onMouseMoved(mouse);
		}
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PCheckBox.this.onMouseButtonTriggered(mouse, btn, clickCount);
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			PCheckBox.this.onMouseButtonPressed(mouse, btn, clickCount);
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			PCheckBox.this.onMouseButtonReleased(mouse, btn, clickCount);
		}
	};
	protected final PSingleValueModelObs<Boolean> modelObs = this::onModelChange;
	protected PCheckBoxModel model;
	
	public PCheckBox() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPCheckBoxModel::new, PCheckBoxModel.class));
		addObs(mouseObs);
	}
	
	public void setModel(PCheckBoxModel model) {
		PCheckBoxModel oldModel = getModel();
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
	
	public PCheckBoxModel getModel() {
		return model;
	}
	
	public void setModelValue(Object value) {
		if (getModel() == null) {
			return;
		}
		getModel().setValue(value);
	}
	
	public boolean isChecked() {
		if (getModel() == null) {
			return false;
		}
		return getModel().isChecked();
	}
	
	public void toggleChecked() {
		if (getModel() != null) {
			getModel().toggleChecked();
		}
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		boolean enabled = isEnabled();
		PColor darkColor;
		PColor lightColor;
		if (enabled) {
			darkColor = PColor.BLACK;
			lightColor = PColor.WHITE;
		} else {
			darkColor = PColor.GREY50;
			lightColor = PColor.GREY875;
		}
		renderer.setColor(darkColor);
		renderer.strokeQuad(x, y, fx, fy, 1);
		renderer.setColor(lightColor);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		
		if (isChecked()) {
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(darkColor);
			renderer.drawQuad(x + gapW, y + gapH, fx - gapW, fy - gapH);
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
	protected void onModelChange(PSingleValueModel<Boolean> model, Boolean oldValue, Boolean newValue) {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
		if (btn == MouseButton.LEFT && isEnabled() && isMouseOver(mouse)) {
			toggleChecked();
			fireClickEvent();
		}
	}
	
}