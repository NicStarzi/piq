package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PScrollBarModel;
import edu.udo.piq.components.PScrollBarModelObs;

public class DefaultPScrollBarModel implements PScrollBarModel {
	
	private final List<PScrollBarModelObs> obsList = new CopyOnWriteArrayList<>();
	private int contentSize;
	private int viewportSize;
	private int scroll;
	
	public void setContentSize(int value) {
		contentSize = value;
		checkScroll();
	}
	
	public int getContentSize() {
		return contentSize;
	}
	
	public void setViewportSize(int value) {
		viewportSize = value;
		checkScroll();
	}
	
	public int getViewportSize() {
		return viewportSize;
	}
	
	private void checkScroll() {
		setScroll(getScroll());
	}
	
	public void setScroll(int value) {
		if (value < getMinScroll()) {
			value = getMinScroll();
		} if (value > getMaxScroll()) {
			value = getMaxScroll();
		}
		if (value != scroll) {
			scroll = value;
			fireScrollChangeEvent();
		}
	}
	
	public int getScroll() {
		return scroll;
	}
	
	private int getMinScroll() {
		return 0;
	}
	
	public int getMaxScroll() {
		int max = getContentSize() - getViewportSize();
		if (max < 0) {
			max = 0;
		}
		return max;
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