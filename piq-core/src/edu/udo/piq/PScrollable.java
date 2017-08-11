package edu.udo.piq;

public interface PScrollable {
	
	public default int getScrollStepSmall(Axis axis) {
		return 10;
	}
	
	public default int getScrollStepBig(Axis axis) {
		return getScrollStepSmall(axis) * 10;
	}
	
}