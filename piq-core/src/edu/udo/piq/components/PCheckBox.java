package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PCheckBox extends AbstractPComponent implements PClickable, PGlobalEventGenerator {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	protected final ObserverList<PSingleValueModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PCheckBox.this.onMouseButtonTriggered(mouse, btn);
		}
	};
	protected final PSingleValueModelObs modelObs = this::onModelChange;
	protected PCheckBoxModel model;
	protected PGlobalEventProvider globEvProv;
	
	public PCheckBox() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPCheckBoxModel::new, PCheckBoxModel.class));
		addObs(mouseObs);
	}
	
	@Override
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	@Override
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
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
	
	protected void toggleChecked() {
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
		
		renderer.setColor(PColor.BLACK);
		renderer.strokeQuad(x, y, fx, fy, 1);
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		
		if (isChecked()) {
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + gapW, y + gapH, fx - gapW, fy - gapH);
		}
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	public void addObs(PSingleValueModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSingleValueModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent((obs) -> obs.onClick(this));
		fireGlobalEvent();
	}
	
	protected void onModelChange(PSingleValueModel model, Object oldValue, Object newValue) {
		fireGlobalEvent();
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOver()) {
			toggleChecked();
			fireClickEvent();
		}
	}
	
}