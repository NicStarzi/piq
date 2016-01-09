package edu.udo.piq.scroll;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.scroll.PScrollBarButton.Direction;
import edu.udo.piq.scroll.PScrollBarLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.scroll.PScrollBarLayout.Orientation;

public class PScrollBar extends AbstractPLayoutOwner {
	
	private final PScrollBarModelObs modelObs = new PScrollBarModelObs() {
		public void sizeChanged(PScrollBarModel model, int oldValue, int newValue) {
			onSizeChanged();
		}
		public void preferredSizeChanged(PScrollBarModel model, int oldValue, int newValue) {
			onSizeChanged();
		}
		public void scrollChanged(PScrollBarModel model, double oldValue, double newValue) {
			fireReLayOutEvent();
		}
	};
	private PScrollBarModel model;
	private int thumbPressPos;
	
	public PScrollBar() {
		super();
		
		PScrollBarButton btn1 = new PScrollBarButton();
		btn1.addObs(new PScrollBarButtonObs() {
			public void onClick(PScrollBarButton button) {
				getModel().subSmallStep();
			}
		});
		PScrollBarButton btn2 = new PScrollBarButton();
		btn2.addObs(new PScrollBarButtonObs() {
			public void onClick(PScrollBarButton button) {
				getModel().addSmallStep();
			}
		});
		PScrollBarBackground bckGrnd1 = new PScrollBarBackground();
		bckGrnd1.addObs(new PScrollBarBackgroundObs() {
			public void onClick(PScrollBarBackground background) {
				getModel().subBigStep();
			}
		});
		PScrollBarBackground bckGrnd2 = new PScrollBarBackground();
		bckGrnd2.addObs(new PScrollBarBackgroundObs() {
			public void onClick(PScrollBarBackground background) {
				getModel().addBigStep();
			}
		});
		PScrollBarThumb thumb = new PScrollBarThumb();
		thumb.addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				if (thumb.isActive() && btn == MouseButton.LEFT && thumb.isMouseOver()) {
					thumb.getModel().setPressed(true);
					if (getOrientation() == Orientation.HORIZONTAL) {
						thumbPressPos = mouse.getX();
					} else {
						thumbPressPos = mouse.getY();
					}
				}
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				if (thumb.isPressed() && btn == MouseButton.LEFT) {
					thumb.getModel().setPressed(false);
				}
			}
			public void onMouseMoved(PMouse mouse) {
				if (thumb.isPressed()) {
					int mousePos;
					if (getOrientation() == Orientation.HORIZONTAL) {
						mousePos = mouse.getX();
					} else {
						mousePos = mouse.getY();
					}
					int thumbDragDis = mousePos - thumbPressPos;
					thumbPressPos = mousePos;
					if (thumbDragDis > 0) {
						getModel().addSmallStep();
					} else if (thumbDragDis < 0) {
						getModel().subSmallStep();
					}
				}
			}
		});
		
		setLayout(new PScrollBarLayout(this));
		getLayoutInternal().addChild(btn1, Constraint.BTN1);
		getLayoutInternal().addChild(btn2, Constraint.BTN2);
		getLayoutInternal().addChild(bckGrnd1, Constraint.BG1);
		getLayoutInternal().addChild(bckGrnd2, Constraint.BG2);
		getLayoutInternal().addChild(thumb, Constraint.THUMB);
		setOrientation(getOrientation());
		
		setModel(new DefaultPScrollBarModel());
	}
	
	protected PScrollBarLayout getLayoutInternal() {
		return (PScrollBarLayout) super.getLayout();
	}
	
	public void setOrientation(Orientation orientation) {
		getLayoutInternal().setOrientation(orientation);
		if (orientation == Orientation.HORIZONTAL) {
			getLayoutInternal().getFirstButton().setDirection(Direction.LEFT);
			getLayoutInternal().getSecondButton().setDirection(Direction.RIGHT);
		} else {
			getLayoutInternal().getFirstButton().setDirection(Direction.UP);
			getLayoutInternal().getSecondButton().setDirection(Direction.DOWN);
		}
		
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public Orientation getOrientation() {
		return getLayoutInternal().getOrientation();
	}
	
	public void setModel(PScrollBarModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PScrollBarModel getModel() {
		return model;
	}
	
	private void onSizeChanged() {
		fireReLayOutEvent();
		boolean active = isActive();
		getLayoutInternal().getFirstButton().setActive(active);
		getLayoutInternal().getSecondButton().setActive(active);
		getLayoutInternal().getFirstBackground().setActive(active);
		getLayoutInternal().getSecondBackground().setActive(active);
		getLayoutInternal().getThumb().setActive(active);
	}
	
	public boolean isActive() {
		PScrollBarModel model = getModel();
		return model.getPreferredSize() > model.getSize();
	}
	
//	private void onThumbClicked() {
//		
//	}
	
}