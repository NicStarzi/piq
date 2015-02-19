package edu.udo.piq.scroll;

import edu.udo.piq.scroll.PScrollBarButton.Direction;
import edu.udo.piq.scroll.PScrollBarLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.scroll.PScrollBarLayout.Orientation;

public class PScrollBar extends AbstractPLayoutOwner {
	
	private PScrollBarModel model;
	
	public PScrollBar() {
		super();
		setLayout(new PScrollBarLayout(this));
		getLayoutInternal().addChild(new PScrollBarButton(), Constraint.BTN1);
		getLayoutInternal().addChild(new PScrollBarButton(), Constraint.BTN2);
		getLayoutInternal().addChild(new PScrollBarBackground(), Constraint.BG1);
		getLayoutInternal().addChild(new PScrollBarBackground(), Constraint.BG2);
		getLayoutInternal().addChild(new PScrollBarThumb(), Constraint.THUMB);
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
		this.model = model;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PScrollBarModel getModel() {
		return model;
	}
	
}