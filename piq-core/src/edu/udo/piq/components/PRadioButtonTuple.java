package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentAction;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.AbstractPKeyInput;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PRadioButtonTuple extends AbstractPInputLayoutOwner implements PClickable, PGlobalEventGenerator {
	
	public static final PKeyInput INPUT_TRIGGER_ENTER = new AbstractPKeyInput(
			KeyInputType.TRIGGER, Key.ENTER, (comp) -> 
	{
		PRadioButtonTuple btn = ThrowException.ifTypeCastFails(comp, PRadioButtonTuple.class, 
				"!(comp instanceof PRadioButtonTuple)");
		return btn.isEnabled() && btn.getRadioButton() != null && btn.getRadioButton().getModel() != null;
	});
	public static final PComponentAction REACTION_TRIGGER_ENTER = (comp) -> {
		PRadioButtonTuple btn = ThrowException.ifTypeCastFails(comp, PRadioButtonTuple.class, 
				"!(comp instanceof PRadioButtonTuple)");
		btn.getRadioButton().setSelected();
	};
	public static final String INPUT_IDENTIFIER_TRIGGER_ENTER = "triggerEnter";
	
	protected final ObserverList<PClickObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PClickObs radBtnObs = (btn) -> onRadioBtnClick();
	private PGlobalEventProvider globEvProv;
	
	public PRadioButtonTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PRadioButton(), Constraint.FIRST);
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PRadioButtonTuple.this.onMouseButtonTriggered(mouse, btn);
			}
			public void onButtonPressed(PMouse mouse, MouseButton btn) {
				PRadioButtonTuple.this.onMouseButtonPressed(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PRadioButtonTuple.this.onMouseButtonReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PRadioButtonTuple.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		defineInput(INPUT_IDENTIFIER_TRIGGER_ENTER, INPUT_TRIGGER_ENTER, REACTION_TRIGGER_ENTER);
	}
	
	public PRadioButtonTuple(PComponent secondComponent) {
		this();
		setSecondComponent(secondComponent);
	}
	
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
	}
	
	protected void setLayout(PTupleLayout layout) {
		if (getLayout() != null) {
			getLayout().removeObs(layoutObs);
		}
		super.setLayout(layout);
		if (getLayout() != null) {
			getLayout().addObs(layoutObs);
		}
	}
	
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
	
	public boolean isSelected() {
		PRadioButton radBtn = getRadioButton();
		if (radBtn == null) {
			return false;
		}
		return radBtn.isSelected();
	}
	
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
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	} 
	
	public void addObs(PRadioButtonModelObs obs) {
		getRadioButton().addObs(obs);
	}
	
	public void removeObs(PRadioButtonModelObs obs) {
		getRadioButton().removeObs(obs);
	}
	
	protected void onRadioBtnClick() {
		obsList.fireEvent((obs) -> obs.onClick(getRadioButton()));
		takeFocus();
		fireGlobalEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		PComponent scndCmp = getSecondComponent();
		if (scndCmp != null && scndCmp.isMouseOver()) {
			if (!scndCmp.isFocusable()) {
				getRadioButton().setSelected();
				takeFocus();
			}
		} else if (isMouseOver()) {
			getRadioButton().setSelected();
			takeFocus();
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (constraint == Constraint.FIRST) {
			PRadioButton btn = ThrowException.ifTypeCastFails(child, 
					PRadioButton.class, "!(child instanceof PRadioButton)");
			btn.addObs(radBtnObs);
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (constraint == Constraint.FIRST) {
			((PRadioButton) child).removeObs(radBtnObs);
		}
	}
	
}