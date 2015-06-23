package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.util.PInput;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;

public class PCheckBoxTuple extends AbstractPInputLayoutOwner {
	
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
	protected final Runnable pressEnterReaction = new Runnable() {
		public void run() {
			getCheckBox().toggleChecked();
		}
	};
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
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
	};
	private final PCheckBoxObs chkBxObs = new PCheckBoxObs() {
		public void onClick(PCheckBox checkBox) {
			takeFocus();
		}
	};
	private final PLayoutObs layoutObs = new PLayoutObs() {
		public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (constraint == Constraint.FIRST) {
				if (!(child instanceof PCheckBox)) {
					throw new IllegalArgumentException("child="+child);
				}
				((PCheckBox) child).addObs(chkBxObs);
			}
		}
		public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (constraint == Constraint.FIRST) {
				((PCheckBox) child).removeObs(chkBxObs);
			}
		}
	};
	
	public PCheckBoxTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PCheckBox(), Constraint.FIRST);
		addObs(mouseObs);
		addObs(new PFocusObs() {
			public void focusGained(PComponent oldOwner, PComponent newOwner) {
				fireReRenderEvent();
			}
			public void focusLost(PComponent oldOwner) {
				fireReRenderEvent();
			}
		});
		defineInput("enter", pressEnterInput, pressEnterReaction);
	}
	
	public PCheckBoxTuple(PComponent secondComponent) {
		this();
		setSecondComponent(secondComponent);
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
		return getLayoutInternal().getAt(Constraint.SECOND);
	}
	
	public PCheckBox getCheckBox() {
		return (PCheckBox) getLayoutInternal().getAt(Constraint.FIRST);
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
			renderer.strokeQuad(x, y, fx, fy);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public void addObs(PCheckBoxObs obs) {
		getCheckBox().addObs(obs);
	}
	
	public void removeObs(PCheckBoxObs obs) {
		getCheckBox().removeObs(obs);
	} 
	
	public void addObs(PCheckBoxModelObs obs) {
		getCheckBox().addObs(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		getCheckBox().removeObs(obs);
	}
	
}