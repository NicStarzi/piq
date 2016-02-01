package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.PInput;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PCheckBoxTuple extends AbstractPInputLayoutOwner implements PGlobalEventGenerator {
	
	protected final PInput pressEnterInput = new PInput() {
		public Key getInputKey() {
			return Key.ENTER;
		}
		public KeyInputType getKeyInputType() {
			return KeyInputType.TRIGGER;
		}
		public boolean canBeUsed(PKeyboard keyboard) {
			return isEnabled() && getCheckBox() != null && getCheckBox().getModel() != null;
		}
	};
	protected final Runnable pressEnterReaction = () -> getCheckBox().toggleChecked();
	
	protected final ObserverList<PCheckBoxObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PCheckBoxObs chkBxObs = (chkBx) -> PCheckBoxTuple.this.onCheckBoxClick();
	private PGlobalEventProvider globEvProv;
	
	public PCheckBoxTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PCheckBox(), Constraint.FIRST);
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PCheckBoxTuple.this.onMouseButtonTriggered(mouse, btn);
			}
			public void onButtonPressed(PMouse mouse, MouseButton btn) {
				PCheckBoxTuple.this.onMouseButtonPressed(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PCheckBoxTuple.this.onMouseButtonReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PCheckBoxTuple.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		defineInput("enter", pressEnterInput, pressEnterReaction);
	}
	
	public PCheckBoxTuple(PComponent secondComponent) {
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
	
	public PCheckBox getCheckBox() {
		return (PCheckBox) getLayoutInternal().getFirst();
	}
	
	public boolean isChecked() {
		PCheckBox chkBx = getCheckBox();
		if (chkBx == null) {
			return false;
		}
		return chkBx.isChecked();
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
	
	public void addObs(PCheckBoxObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PCheckBoxObs obs) {
		obsList.remove(obs);
	} 
	
	public void addObs(PCheckBoxModelObs obs) {
		getCheckBox().addObs(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		getCheckBox().removeObs(obs);
	}
	
	protected void onMouseMoved(PMouse mouse) {
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		PComponent scndCmp = getSecondComponent();
		if (scndCmp != null && scndCmp.isMouseOver()) {
			if (!scndCmp.isFocusable()) {
				getCheckBox().toggleChecked();
				takeFocus();
			}
		} else if (isMouseOver()) {
			getCheckBox().toggleChecked();
			takeFocus();
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (constraint == Constraint.FIRST) {
			if (!(child instanceof PCheckBox)) {
				throw new IllegalArgumentException("child="+child);
			}
			((PCheckBox) child).addObs(chkBxObs);
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (constraint == Constraint.FIRST) {
			((PCheckBox) child).removeObs(chkBxObs);
		}
	}
	
	protected void onCheckBoxClick() {
		takeFocus();
		obsList.fireEvent((obs) -> obs.onClick(getCheckBox()));
		fireGlobalEvent();
	}
	
}