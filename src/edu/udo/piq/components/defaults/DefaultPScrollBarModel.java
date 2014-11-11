package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PScrollBarModel;
import edu.udo.piq.components.PScrollBarModelObs;
import edu.udo.piq.util.PCompUtil;

public class DefaultPScrollBarModel implements PScrollBarModel {
	
	private final List<PScrollBarModelObs> obsList = new CopyOnWriteArrayList<>();
	private final PLayoutObs viewObs = new PLayoutObs() {
		public void layoutInvalidated(PLayout layout) {
		}
		public void childRemoved(PLayout layout, PComponent child, Object constraint) {
			if (child == view) {
				setView(null);
			}
		}
		public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			if (child == view) {
				fireSizeChangeEvent();
				checkScroll();
			}
		}
		public void childAdded(PLayout layout, PComponent child, Object constraint) {
		}
	};
	private PComponent view;
	private boolean horizontal;
	private int scroll;
	
	public void setAlignment(boolean isHorizontal) {
		horizontal = isHorizontal;
		fireSizeChangeEvent();
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}
	
	public boolean isVertical() {
		return !isHorizontal();
	}
	
	public void setView(PComponent component) {
		if (getView() != null) {
			getView().getParent().getLayout().removeObs(viewObs);
		}
		view = component;
		if (getView() != null) {
			getView().getParent().getLayout().addObs(viewObs);
		}
		fireSizeChangeEvent();
	}
	
	public PComponent getView() {
		return view;
	}
	
	public int getContentSize() {
		if (getView() == null) {
			return 0;
		}
		PSize viewSize = PCompUtil.getPreferredSizeOf(view);
		if (isHorizontal()) {
			return viewSize.getWidth();
		}
		return viewSize.getHeight();
	}
	
	public int getViewportSize() {
		if (getView() == null) {
			return 0;
		}
		PSize viewSize = PCompUtil.getBoundsOf(view);
		if (isHorizontal()) {
			return viewSize.getWidth();
		}
		return viewSize.getHeight();
	}
	
	private void checkScroll() {
		int maxScroll = getContentSize() - getViewportSize();
		if (scroll > maxScroll) {
			setScroll(scroll);
		}
	}
	
	public void setScroll(int value) {
		if (getView() == null) {
			scroll = 0;
			return;
		}
		int maxScroll = getContentSize() - getViewportSize();
		if (value > maxScroll) {
			value = maxScroll;
		}
		if (value < 0) {
			value = 0;
		}
		scroll = value;
		fireScrollChangeEvent();
	}
	
	public int getScroll() {
		if (getView() == null) {
			return 0;
		}
		return scroll;
	}
	
	public void addObs(PScrollBarModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PScrollBarModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSizeChangeEvent() {
		for (PScrollBarModelObs obs : obsList) {
			obs.sizeChanged(this);
		}
	}
	
	protected void fireScrollChangeEvent() {
		for (PScrollBarModelObs obs : obsList) {
			obs.scrollChanged(this);
		}
	}
}