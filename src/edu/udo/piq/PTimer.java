package edu.udo.piq;

public class PTimer {
	
	protected final PTimerCallback callback;
	protected final PComponent owner;
	protected int delayInMillis;
	protected int timeCount;
	protected boolean repeating;
	protected boolean running;
	
	public PTimer(PComponent component, PTimerCallback callback) {
		if (component == null) {
			throw new IllegalArgumentException("component=null");
		} if (callback == null) {
			throw new IllegalArgumentException("callback=null");
		}
		owner = component;
		this.callback = callback;
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public void setRepeating(boolean value) {
		repeating = value;
	}
	
	public boolean isRepeating() {
		return repeating;
	}
	
	public void setDelay(int value) {
		if (value <= 0) {
			throw new IllegalArgumentException("value="+value);
		}
		delayInMillis = value;
	}
	
	public int getDelay() {
		return delayInMillis;
	}
	
	public void start() {
		if (!running) {
			PRoot root = getOwner().getRoot();
			if (root == null) {
				throw new IllegalStateException("getOwner().getRoot() == null");
			}
			root.registerTimer(this);
			running = true;
		}
	}
	
	public void stop() {
		if (running) {
			PRoot root = getOwner().getRoot();
			if (root == null) {
				throw new IllegalStateException("getOwner().getRoot() == null");
			}
			root.unregisterTimer(this);
			running = false;
		}
	}
	
	public void tick() {
		timeCount += 1;
		if (timeCount >= delayInMillis) {
			if (isRepeating()) {
				timeCount = 0;
			} else {
				stop();
			}
			callback.action();
		}
	}
	
}