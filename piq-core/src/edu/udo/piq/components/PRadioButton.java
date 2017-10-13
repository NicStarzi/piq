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
import edu.udo.piq.components.defaults.DefaultPRadioButtonModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PRadioButton extends AbstractPComponent implements PClickable, PGlobalEventGenerator {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	protected final ObserverList<PRadioButtonModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PRadioButton.this.onMouseButtonTriggered(mouse, btn);
		}
	};
	protected final PRadioButtonModelObs modelObs = (mdl) -> onModelChange();
	protected PRadioButtonModel model;
	private PGlobalEventProvider globEvProv;
	
	public PRadioButton() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPRadioButtonModel::new, PRadioButtonModel.class));
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
	
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	public void addObs(PRadioButtonModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PRadioButtonModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent((obs) -> obs.onClick(this));
		fireGlobalEvent();
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOver()) {
			setSelected();
			fireClickEvent();
		}
	}
	
	protected void onModelChange() {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
}