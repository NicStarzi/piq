package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.scroll.PScrollBarLayout.Orientation;
import edu.udo.piq.scroll.PScrollPanelLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PScrollPanel extends AbstractPLayoutOwner {
	
	private final PScrollBarModelObs modelObs = new PScrollBarModelObs() {
		@Override
		public void sizeChanged(PScrollBarModel model, int oldValue, int newValue) {
//			System.out.println("scrollPanel.sizeChanged="+newValue);
			fireReLayOutEvent();
		}
		@Override
		public void scrollChanged(PScrollBarModel model, double oldValue, double newValue) {
//			System.out.println("scrollPanel.scrollChanged="+newValue);
			fireReLayOutEvent();
		}
		@Override
		public void preferredSizeChanged(PScrollBarModel model, int oldValue, int newValue) {
//			System.out.println("scrollPanel.preferredSizeChanged="+newValue);
			fireReLayOutEvent();
		}
	};
	private final PComponentObs bodyObs = new PComponentObs() {
		@Override
		public void onPreferredSizeChanged(PComponent component) {
			refreshPrefSize();
		}
	};
	private final PLayoutObs layoutObs = new PLayoutObs() {
		@Override
		public void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			setChild(child, (Constraint) constraint);
		}
		@Override
		public void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			setChild(null, (Constraint) constraint);
		}
		@Override
		public void onChildLaidOut(PReadOnlyLayout layout, PComponent child, Object constraint) {
			if (constraint == Constraint.BODY) {
				refreshSize();
			}
		}
	};
	private PScrollBar currentBarH = null;
	private PScrollBar currentBarV = null;
	
	public PScrollPanel() {
		this(null);
	}
	
	public PScrollPanel(PComponent body) {
		super();
		setLayout(new PScrollPanelLayout(this));
		
		setHorizontalScrollBar(new PScrollBar());
		setVerticalScrollBar(new PScrollBar());
		
		addObs(new PComponentObs() {
			@Override
			public void onBoundsChanged(PComponent component) {
				refreshSize();
			}
		});
		setBody(body);
	}
	
	protected void setLayout(PScrollPanelLayout layout) {
		if (getLayout() != null) {
			getLayout().removeObs(layoutObs);
		}
		super.setLayout(layout);
		if (getLayout() != null) {
			getLayout().addObs(layoutObs);
		}
	}
	
	protected PScrollPanelLayout getLayoutInternal() {
		return (PScrollPanelLayout) super.getLayout();
	}
	
	public void setBody(PComponent component) {
		if (getBody() != null) {
			getBody().removeObs(bodyObs);
		}
		getLayoutInternal().setBody(component);
		if (getBody() != null) {
			getBody().addObs(bodyObs);
		}
		refreshPrefSize();
		refreshSize();
	}
	
	public PComponent getBody() {
		return getLayoutInternal().getBody();
	}
	
	public void setHorizontalScrollBar(PScrollBar scrollBar) {
		PScrollPanelLayout layout = getLayoutInternal();
		PComponent oldBar = getHorizontalScrollBar();
		if (oldBar != null) {
			layout.removeChild(oldBar);
		}
		if (scrollBar != null) {
			scrollBar.setOrientation(Orientation.HORIZONTAL);
			layout.addChild(scrollBar, Constraint.BAR_H);
		}
	}
	
	public PScrollBar getHorizontalScrollBar() {
		return getLayoutInternal().getHorizontalScrollBar();
	}
	
	public void setVerticalScrollBar(PScrollBar scrollBar) {
		PScrollPanelLayout layout = getLayoutInternal();
		PComponent oldBar = getVerticalScrollBar();
		if (oldBar != null) {
			layout.removeChild(oldBar);
		}
		if (scrollBar != null) {
			scrollBar.setOrientation(Orientation.VERTICAL);
			layout.addChild(scrollBar, Constraint.BAR_V);
		}
	}
	
	public PScrollBar getVerticalScrollBar() {
		return getLayoutInternal().getVerticalScrollBar();
	}
	
	private void setChild(PComponent child, Constraint cnstr) {
		switch (cnstr) {
		case BAR_H:
			if (currentBarH != null) {
				currentBarH.getModel().removeObs(modelObs);
			}
			currentBarH = (PScrollBar) child;
			if (currentBarH != null) {
				currentBarH.getModel().addObs(modelObs);
			}
			break;
		case BAR_V:
			if (currentBarV != null) {
				currentBarV.getModel().removeObs(modelObs);
			}
			currentBarV = (PScrollBar) child;
			if (currentBarV != null) {
				currentBarV.getModel().addObs(modelObs);
			}
			break;
		case BODY:
			refreshSize();
			break;
		default:
			break;
		}
	}
	
	private void refreshSize() {
		PComponent body = getLayoutInternal().getBody();
		int w;
		int h;
		if (body == null) {
			w = 0;
			h = 0;
		} else {
			PSize size = body.getClippedBounds();
			if (size == null) {
				w = 0;
				h = 0;
			} else {
				w = size.getWidth();
				h = size.getHeight();
			}
		}
		PScrollBar barH = getHorizontalScrollBar();
		if (barH != null && barH.getModel() != null) {
			barH.getModel().setSize(w);
		}
		PScrollBar barV = getVerticalScrollBar();
		if (barV != null && barV.getModel() != null) {
			barV.getModel().setSize(h);
		}
	}
	
	private void refreshPrefSize() {
		PComponent body = getLayoutInternal().getBody();
		int prefW;
		int prefH;
		if (body == null) {
			prefW = 0;
			prefH = 0;
		} else {
			PSize prefSize = body.getPreferredSize();
			prefW = prefSize.getWidth();
			prefH = prefSize.getHeight();
		}
		PScrollBar barH = getHorizontalScrollBar();
		if (barH != null && barH.getModel() != null) {
			barH.getModel().setPreferredSize(prefW);
		}
		PScrollBar barV = getVerticalScrollBar();
		if (barV != null && barV.getModel() != null) {
			barV.getModel().setPreferredSize(prefH);
		}
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(bnds);
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return getLayout().getPreferredSize();
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
}