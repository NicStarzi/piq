package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;

public class PCheckBox extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	private final List<PCheckBoxObs> obsList = new CopyOnWriteArrayList<>();
	protected final PCheckBoxModelObs modelObs = new PCheckBoxModelObs() {
		public void onChange(PCheckBoxModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	protected PCheckBoxModel model = new DefaultPCheckBoxModel();
	
	protected void onUpdate() {
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			return;
		}
		if (mouse.isTriggered(MouseButton.LEFT) 
				&& PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
			model.setChecked(!model.isChecked());
//			model.fireClickEvent();
		}
	}
	
	public void setModel(PCheckBoxModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
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
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(x, y, fx, fy);
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
	
	protected void fireClickEvent() {
		for (PCheckBoxObs obs : obsList) {
			obs.clicked(this);
		}
	}
	
}