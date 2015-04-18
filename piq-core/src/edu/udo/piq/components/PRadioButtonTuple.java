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

public class PRadioButtonTuple extends AbstractPLayoutOwner {
	
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void keyTriggered(PKeyboard keyboard, Key key) {
			if (!hasFocus() || getRadioButton() == null || getRadioButton().getModel() == null) {
				return;
			}
			PRadioButtonModel model = getRadioButton().getModel();
			if (key == Key.ENTER) {
				getRadioButton().setSelected();
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
					getRadioButton().setSelected();
					takeFocus();
				}
			} else if (isMouseOver()) {
				getRadioButton().setSelected();
				takeFocus();
			}
		}
	};
	private final PRadioButtonObs radBtnObs = new PRadioButtonObs() {
		public void onClick(PRadioButton RadioButton) {
			takeFocus();
		}
	};
	private final PLayoutObs layoutObs = new PLayoutObs() {
		public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (constraint == Constraint.FIRST) {
				if (!(child instanceof PRadioButton)) {
					throw new IllegalArgumentException("child="+child);
				}
				((PRadioButton) child).addObs(radBtnObs);
			}
		}
		public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (constraint == Constraint.FIRST) {
				((PRadioButton) child).removeObs(radBtnObs);
			}
		}
	};
	
	public PRadioButtonTuple() {
		super();
		setLayout(new PTupleLayout(this));
		getLayoutInternal().addChild(new PRadioButton(), Constraint.FIRST);
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
	
	public PRadioButtonTuple(PComponent secondComponent) {
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
	
	public PRadioButton getRadioButton() {
		return (PRadioButton) getLayoutInternal().getAt(Constraint.FIRST);
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
			renderer.strokeQuad(x, y, fx, fy);
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public void addObs(PRadioButtonObs obs) {
		getRadioButton().addObs(obs);
	}
	
	public void removeObs(PRadioButtonObs obs) {
		getRadioButton().removeObs(obs);
	} 
	
	public void addObs(PRadioButtonModelObs obs) {
		getRadioButton().addObs(obs);
	}
	
	public void removeObs(PRadioButtonModelObs obs) {
		getRadioButton().removeObs(obs);
	}
	
}