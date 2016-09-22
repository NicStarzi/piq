package edu.udo.piq.components;

import java.util.function.Consumer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.DefaultPKeyInput;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PCheckBoxTuple extends AbstractPInputLayoutOwner implements PClickable, PGlobalEventGenerator {
	
	public static final PKeyInput<PCheckBoxTuple> INPUT_TRIGGER_ENTER = 
			new DefaultPKeyInput<>(KeyInputType.TRIGGER, Key.ENTER, PCheckBoxTuple::canTriggerEnter);
	public static final Consumer<PCheckBoxTuple> REACTION_TRIGGER_ENTER = PCheckBoxTuple::onTriggerEnter;
	public static final String INPUT_IDENTIFIER_TRIGGER_ENTER = "triggerEnter";
	
	protected static boolean canTriggerEnter(PCheckBoxTuple self) {
		return self.isEnabled() && self.getCheckBox() != null 
				&& self.getCheckBox().getModel() != null;
	}
	
	protected static void onTriggerEnter(PCheckBoxTuple self) {
		self.getCheckBox().toggleChecked();
	}
	
	protected final ObserverList<PClickObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PClickObs chkBxObs = (chkBx) -> PCheckBoxTuple.this.onCheckBoxClick();
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
		
		defineInput(INPUT_IDENTIFIER_TRIGGER_ENTER, INPUT_TRIGGER_ENTER, REACTION_TRIGGER_ENTER);
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
	
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
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
				onCheckBoxClick();
			}
		} else if (isMouseOver()) {
			getCheckBox().toggleChecked();
			onCheckBoxClick();
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