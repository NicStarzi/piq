package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.components.PExpandButton;
import edu.udo.piq.layouts.PCollapsibleLayout;
import edu.udo.piq.layouts.PCollapsibleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PCollapsiblePanel extends AbstractPLayoutOwner {
	
	private final PCheckBoxModelObs expandModelObs = (mdl) -> getLayout().setExpanded(mdl.isChecked());
	
	public PCollapsiblePanel() {
		this(null, new PPanel());
	}
	
	public PCollapsiblePanel(PComponent headerComponent) {
		this(headerComponent, new PPanel());
	}
	
	public PCollapsiblePanel(PComponent headerComponent, PComponent bodyComponent) {
		super();
		setLayout(new PCollapsibleLayout(this));
		setExpandButton(new PExpandButton());
		setHeader(headerComponent);
		setBody(bodyComponent);
	}
	
	public PCollapsiblePanel(boolean initiallyExpanded) {
		super();
		setLayout(new PCollapsibleLayout(this));
		setExpandButton(new PExpandButton());
		getExpandButton().getModel().setValue(initiallyExpanded);
	}
	
	public void setExpanded(boolean value) {
		PCheckBox btn = getExpandButton();
		if (btn == null || btn.getModel() == null) {
			getLayout().setExpanded(value);
		} else {
			btn.getModel().setValue(value);
		}
	}
	
	public boolean isExpanded() {
		return getLayout().isExpanded();
	}
	
	public PCollapsibleLayout getLayout() {
		return (PCollapsibleLayout) super.getLayout();
	}
	
	public void setExpandButton(PCheckBox chkBox) {
		PCheckBox oldBtn = getExpandButton();
		if (oldBtn != null) {
			oldBtn.removeObs(expandModelObs);
		}
		getLayout().setChildForConstraint(chkBox, Constraint.EXPAND_BUTTON);
		boolean expanded = true;
		if (chkBox != null) {
			chkBox.addObs(expandModelObs);
			expanded = chkBox.isChecked();
		}
		getLayout().setExpanded(expanded);
	}
	
	public PCheckBox getExpandButton() {
		return (PCheckBox) getLayout().getChildForConstraint(Constraint.EXPAND_BUTTON);
	}
	
	public void setHeader(PComponent component) {
		getLayout().setChildForConstraint(component, Constraint.LABEL);
	}
	
	public PComponent getHeader() {
		return getLayout().getChildForConstraint(Constraint.LABEL);
	}
	
	public void setBody(PComponent component) {
		getLayout().setChildForConstraint(component, Constraint.BODY);
	}
	
	public PComponent getBody() {
		return getLayout().getChildForConstraint(Constraint.BODY);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(bounds);
		renderer.setColor(PColor.BLACK);
		renderer.strokeQuad(bounds);
	}
	
}