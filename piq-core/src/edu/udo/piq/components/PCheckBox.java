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
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PCheckBox extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	private final List<PCheckBoxModelObs> modelObsList = new CopyOnWriteArrayList<>();
	private final List<PCheckBoxObs> obsList = new CopyOnWriteArrayList<>();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOver()) {
				toggleModel();
				fireClickEvent();
			}
		}
	};
	protected final PCheckBoxModelObs modelObs = new PCheckBoxModelObs() {
		public void onChange(PCheckBoxModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	protected PCheckBoxModel model;
	
	public PCheckBox() {
		super();
		setModel(new DefaultPCheckBoxModel());
		addObs(mouseObs);
	}
	
	public void setModel(PCheckBoxModel model) {
		PCheckBoxModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PCheckBoxModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PCheckBoxModelObs obs : modelObsList) {
				model.addObs(obs);
			}
		}
		fireReRenderEvent();
	}
	
	public PCheckBoxModel getModel() {
		return model;
	}
	
	public boolean isChecked() {
		if (getModel() == null) {
			return false;
		}
		return getModel().isChecked();
	}
	
	protected void toggleModel() {
		if (getModel() != null) {
			getModel().setChecked(!getModel().isChecked());
		}
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
		
		if (isChecked()) {
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + gapW, y + gapH, fx - gapW, fy - gapH);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public void addObs(PCheckBoxObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PCheckBoxObs obs) {
		obsList.remove(obs);
	} 
	
	public void addObs(PCheckBoxModelObs obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		modelObsList.remove(obs);
	}
	
	protected void fireClickEvent() {
		for (PCheckBoxObs obs : obsList) {
			obs.clicked(this);
		}
	}
	
}