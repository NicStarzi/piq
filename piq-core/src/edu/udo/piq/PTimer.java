package edu.udo.piq;

public class PTimer {
	
	protected final PTimerCallback callback;
	protected PComponent owner;
	protected int delayInMillis;
	protected int timeCount;
	protected boolean repeating;
	protected boolean running;
	
	public PTimer(PTimerCallback callback) {
		this(null, callback);
	}
	
	public PTimer(PComponent owner, PTimerCallback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback=null");
		}
		this.owner = owner;
		this.callback = callback;
	}
	
	public void setOwner(PComponent component) {
		if (owner != component) {
			PComponent oldOwner = owner;
			owner = component;
			if (isRunning()) {
				PRoot oldRoot = oldOwner.getRoot();
				oldRoot.unregisterTimer(this);
				
				PRoot newRoot = owner.getRoot();
				if (newRoot == null) {
					throw new IllegalStateException("getOwner().getRoot()=null");
				}
				newRoot.registerTimer(this);
			}
		}
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
		if (!isRunning()) {
			PRoot root = getOwner().getRoot();
			if (root == null) {
				throw new IllegalStateException("getOwner().getRoot()=null");
			}
			root.registerTimer(this);
			timeCount = 0;
			running = true;
		}
	}
	
	public void stop() {
		if (isRunning()) {
			PRoot root = getOwner().getRoot();
			if (root == null) {
				throw new IllegalStateException("getOwner().getRoot()=null");
			}
			root.unregisterTimer(this);
			timeCount = 0;
			running = false;
		}
	}
	
	public void restart() {
		if (!isRunning()) {
			throw new IllegalStateException("isRunning()=false");
		}
		timeCount = 0;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void tick() {
		timeCount += 1;
		if (timeCount >= delayInMillis) {
			if (isRepeating()) {
				timeCount = 0;
			} else {
				stop();
			}
			callback.onTick();
		}
	}
	
}