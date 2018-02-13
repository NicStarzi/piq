package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.Throw;

public class PRadioButtonTuple extends AbstractPLayoutOwner implements PInteractiveComponent, PClickable {
	
	public static final PActionKey KEY_TRIGGER_ENTER = StandardComponentActionKey.INTERACT;
	public static final PAccelerator ACCELERATOR_TRIGGER_ENTER = new PAccelerator(
			ActualKey.ENTER, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_TRIGGER_ENTER = new FocusOwnerAction<>(
			PRadioButtonTuple.class, false,
			ACCELERATOR_TRIGGER_ENTER,
			self -> self.isEnabled(),
			self -> self.getRadioButton().setSelected());
	
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PClickObs radBtnObs = (btn) -> onRadioBtnClick();
	
	public PRadioButtonTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PRadioButton(), Constraint.FIRST);
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PRadioButtonTuple.this.onMouseButtonTriggered(mouse, btn);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PRadioButtonTuple.this.onMouseButtonPressed(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PRadioButtonTuple.this.onMouseButtonReleased(mouse, btn);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PRadioButtonTuple.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		addActionMapping(KEY_TRIGGER_ENTER, ACTION_TRIGGER_ENTER);
	}
	
	public PRadioButtonTuple(PComponent secondComponent) {
		this();
		setSecondComponent(secondComponent);
	}
	
	@Override
	protected PTupleLayout getLayoutInternal() {
		return (PTupleLayout) super.getLayout();
	}
	
	public void setSecondComponent(PComponent component) {
		if (component == null) {
			getLayoutInternal().removeChild(Constraint.SECOND);
		} else {
			getLayoutInternal().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayoutInternal().getSecond();
	}
	
	public PRadioButton getRadioButton() {
		return (PRadioButton) getLayoutInternal().getFirst();
	}
	
	@Override
	public void setEnableModel(PEnableModel model) {
		Throw.ifNull(getRadioButton(), "getRadioButton() == null");
		getRadioButton().setEnableModel(model);
	}
	
	@Override
	public PEnableModel getEnableModel() {
		Throw.ifNull(getRadioButton(), "getRadioButton() == null");
		return getRadioButton().getEnableModel();
	}
	
	@Override
	public boolean isFocusable() {
		return isEnabled();
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return false;
	}
	
	public boolean isSelected() {
		PRadioButton radBtn = getRadioButton();
		if (radBtn == null) {
			return false;
		}
		return radBtn.isSelected();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		if (hasFocus()) {
			PBounds bnds = getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int fx = bnds.getFinalX();
			int fy = bnds.getFinalY();
			
			renderer.setColor(PColor.GREY50);
			renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
			renderer.drawQuad(x, y, fx, fy);
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
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
		getRadioButton().addObs(obs);
	}
	
	public void removeObs(PSingleValueModelObs<Boolean> obs) {
		getRadioButton().removeObs(obs);
	}
	
	protected void onRadioBtnClick() {
		obsList.fireEvent((obs) -> obs.onClick(getRadioButton()));
		takeFocus();
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		PComponent scndCmp = getSecondComponent();
		if (scndCmp != null && scndCmp.isMouseOver(mouse)) {
			if (!scndCmp.isFocusable()) {
				getRadioButton().setSelected();
				takeFocus();
			}
		} else if (isMouseOver(mouse)) {
			getRadioButton().setSelected();
			takeFocus();
		}
	}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {}
	
	@Override
	@TemplateMethod
	protected void onChildAdded(PComponentLayoutData data) {
		if (data.getConstraint() == Constraint.FIRST) {
			PComponent child = data.getComponent();
			if (!(child instanceof PRadioButton)) {
				throw new IllegalArgumentException("child="+child);
			}
			((PRadioButton) child).addObs(radBtnObs);
		}
	}
	
	@Override
	@TemplateMethod
	protected void onChildRemoved(PComponentLayoutData data) {
		if (data.getConstraint() == Constraint.FIRST) {
			((PRadioButton) data.getComponent()).removeObs(radBtnObs);
		}
	}
	
}