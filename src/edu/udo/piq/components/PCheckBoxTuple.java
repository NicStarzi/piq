package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PCheckBoxTuple extends AbstractPLayoutOwner {
	
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void keyTriggered(PKeyboard keyboard, Key key) {
			if (!hasFocus() || getCheckBox() == null || getCheckBox().getModel() == null) {
				return;
			}
			PCheckBoxModel model = getCheckBox().getModel();
			if (key == Key.ENTER) {
				model.setChecked(!model.isChecked());
			} else if (key == Key.UNDO) {
				PModelHistory history = model.getHistory();
				if (history != null && history.canUndo()) {
					history.undo();
				}
			} else if (key == Key.REDO) {
				PModelHistory history = model.getHistory();
				if (history != null && history.canUndo()) {
					history.redo();
				}
			}
		}
	};
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			PComponent scndCmp = getSecondComponent();
			if (scndCmp != null && scndCmp.isMouseOver()) {
				if (!scndCmp.isFocusable()) {
					getCheckBox().toggleModel();
					takeFocus();
				}
			} else if (isMouseOver()) {
				getCheckBox().toggleModel();
				takeFocus();
			}
		}
	};
	private final PCheckBoxObs chkBxObs = new PCheckBoxObs() {
		public void clicked(PCheckBox checkBox) {
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
		addObs(keyObs);
		addObs(mouseObs);
		addObs(new PFocusObs() {
			public void focusGained(PComponent oldOwner, PComponent newOwner) {
				fireReRenderEvent();
			}
			public void focusLost(PComponent oldOwner) {
				fireReRenderEvent();
			}
		});
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
	
	public boolean isFocusable() {
		return true;
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}