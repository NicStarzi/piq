package edu.udo.piq.style;

public interface PStyleObs {
	
	public void onSizeChangedEvent(PStyleable<?> styleable);
	
	public void onReRenderEvent(PStyleable<?> styleable);
	
}