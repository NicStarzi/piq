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
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.Throw;

public class PCheckBoxTuple extends AbstractPLayoutOwner implements PInteractiveComponent, PClickable {
	
	public static final PActionKey KEY_TRIGGER_ENTER = StandardComponentActionKey.INTERACT;
	public static final PAccelerator ACCELERATOR_TRIGGER_ENTER = new PAccelerator(
			ActualKey.ENTER, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final FocusOwnerAction<PCheckBoxTuple> ACTION_TRIGGER_ENTER = new FocusOwnerAction<>(
			PCheckBoxTuple.class, false,
			ACCELERATOR_TRIGGER_ENTER,
			self -> self.isEnabled(),
			self -> self.getCheckBox().toggleChecked());
	
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	private final PClickObs chkBxObs = (chkBx) -> PCheckBoxTuple.this.onCheckBoxClick();
	
	public PCheckBoxTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PCheckBox(), Constraint.FIRST);
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PCheckBoxTuple.this.onMouseButtonTriggered(mouse, btn);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PCheckBoxTuple.this.onMouseButtonPressed(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PCheckBoxTuple.this.onMouseButtonReleased(mouse, btn);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PCheckBoxTuple.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		addActionMapping(KEY_TRIGGER_ENTER, ACTION_TRIGGER_ENTER);
	}
	
	public PCheckBoxTuple(PComponent secondComponent) {
		this();
		setSecondComponent(secondComponent);
	}
	
	public PCheckBoxTuple(Object initialLabelValue) {
		this(new PLabel(initialLabelValue));
	}
	
	public PCheckBoxTuple(PTextModel labelModel) {
		this(new PLabel(labelModel));
	}
	
	@Override
	protected PTupleLayout getLayoutInternal() {
		return (PTupleLayout) super.getLayout();
	}
	
	protected void setCheckBox(PCheckBox checkBox) {
		if (getCheckBox() != null) {
			getLayoutInternal().removeChild(Constraint.FIRST);
		}
		if (checkBox != null) {
			getLayoutInternal().addChild(checkBox, Constraint.FIRST);
		}
	}
	
	public PCheckBox getCheckBox() {
		return (PCheckBox) getLayoutInternal().getFirst();
	}
	
	public void setSecondComponent(PComponent component) {
		if (getSecondComponent() != null) {
			getLayoutInternal().removeChild(Constraint.SECOND);
		}
		if (component != null) {
			getLayoutInternal().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayoutInternal().getSecond();
	}
	
	@Override
	public void setEnableModel(PEnableModel model) {
		Throw.ifNull(getCheckBox(), "getCheckBox() == null");
		boolean oldEnabled = isEnabled();
		getCheckBox().setEnableModel(model);
		if (oldEnabled != isEnabled()) {
			fireReRenderEvent();
		}
	}
	
	@Override
	public PEnableModel getEnableModel() {
		Throw.ifNull(getCheckBox(), "getCheckBox() == null");
		return getCheckBox().getEnableModel();
	}
	
	@Override
	public boolean isFocusable() {
		return isEnabled();
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return false;
	}
	
	public void setChecked(boolean value) {
		PCheckBox chkBx = getCheckBox();
		if (chkBx == null) {
			return;
		}
		chkBx.setModelValue(value);
	}
	
	public boolean isChecked() {
		PCheckBox chkBx = getCheckBox();
		if (chkBx == null) {
			return false;
		}
		return chkBx.isChecked();
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
		getCheckBox().addObs(obs);
	}
	
	public void removeObs(PSingleValueModelObs<Boolean> obs) {
		getCheckBox().removeObs(obs);
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (!isEnabled()) {
			return;
		}
		PComponent scndCmp = getSecondComponent();
		if (scndCmp != null && scndCmp.isMouseOver(mouse)) {
			if (!scndCmp.isFocusable()) {
				getCheckBox().toggleChecked();
				onCheckBoxClick();
			}
		} else if (isMouseOver(mouse)) {
			getCheckBox().toggleChecked();
			onCheckBoxClick();
		}
	}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {}
	
	@TemplateMethod
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		if (data.getConstraint() == Constraint.FIRST) {
			PComponent child = data.getComponent();
			if (!(child instanceof PCheckBox)) {
				throw new IllegalArgumentException("child="+child);
			}
			((PCheckBox) child).addObs(chkBxObs);
		}
	}
	
	@TemplateMethod
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		if (data.getConstraint() == Constraint.FIRST) {
			((PCheckBox) data.getComponent()).removeObs(chkBxObs);
		}
	}
	
	@TemplateMethod
	protected void onCheckBoxClick() {
		takeFocus();
		obsList.fireEvent((obs) -> obs.onClick(getCheckBox()));
	}
	
}