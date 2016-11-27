package edu.udo.piq;

/**
 * This interface is used by {@link PTimer PTimers} to define a timed event.<br>
 * 
 * @author NicStarzi
 */
public interface PTimerCallback {
	
	/**
	 * When the {@link PTimer}, that this callback is registered at, expires
	 * this method will be called.<br>
	 * @param deltaTime
	 * @see PTimer#setRepeating(boolean)
	 * @see PTimer#setDelay(int)
	 */
	public void onTimerEvent(double deltaTime);
	
}