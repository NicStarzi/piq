package edu.udo.piq.components;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.PRenderUtil;

public class PDropDown extends AbstractPComponent {
	
	private final PDropDownSelectionObs selectionObs = new PDropDownSelectionObs() {
		public void selectionChanged(PDropDownSelection selection,
				Object oldSelection, Object newSelection) {
		}
	};
	private final Map<Object, PListCellComponent> elementToCompMap = new HashMap<>();
	private PDropDownSelection selection;
	private PListModel model;
	private PListCellFactory cellFac;
	
	public PDropDown() {
		super();
		setModel(new DefaultPListModel());
	}
	
	public void setCellFactory(PListCellFactory factory) {
		cellFac = factory;
	}
	
	public PListCellFactory getCellFactory() {
		return cellFac;
	}
	
	public void setSelection(PDropDownSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PDropDownSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel model) {
		if (getModel() != null) {
//			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
//			getModel().addObs(modelObs);
		}
		modelChanged();
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		renderer.setColor(PColor.BLACK);
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	private void modelChanged() {
		getLayout().clearChildren();
		elementToCompMap.clear();
		
		for (int i = 0; i < getModel().getElementCount(); i++) {
//			elementAdded(i);
		}
	}
	
}