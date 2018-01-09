package edu.udo.piq.style;

public abstract class ImmutablePStyle implements PStyle {
	
	@Override
	public void addObs(PStyleObs obs) { 
		// intentionally left blank
		// an immutable style does not need to be observed since it does not change
	}
	
	@Override
	public void removeObs(PStyleObs obs) { 
		// intentionally left blank
		// an immutable style does not need to be observed since it does not change
	}
	
	protected void fireReRenderEvent() {
		// intentionally left blank
	}
	
	protected void fireSizeChangedEvent() {
		// intentionally left blank
	}
	
}