package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPRadioButtonModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PRadioButton extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	private final List<PRadioButtonModelObs> modelObsList = new CopyOnWriteArrayList<>();
	private final List<PRadioButtonObs> obsList = new CopyOnWriteArrayList<>();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOver()) {
				setSelected();
				fireClickEvent();
			}
		}
	};
	protected final PRadioButtonModelObs modelObs = new PRadioButtonModelObs() {
		public void onChange(PRadioButtonModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	protected PRadioButtonModel model;
	
	public PRadioButton() {
		super();
		setModel(new DefaultPRadioButtonModel());
		addObs(mouseObs);
	}
	
	public void setModel(PRadioButtonModel model) {
		PRadioButtonModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PRadioButtonModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PRadioButtonModelObs obs : modelObsList) {
				model.addObs(obs);
			}
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
		
		if (isSelected()) {
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + gapW, y + gapH, fx - gapW, fy - gapH);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public void addObs(PRadioButtonObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PRadioButtonObs obs) {
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
		for (PRadioButtonObs obs : obsList) {
			obs.onClick(this);
		}
	}
	
}