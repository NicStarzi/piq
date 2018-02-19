package edu.udo.piq;

import edu.udo.piq.util.ThrowException;

/**
 * This class should be used in a piq GUI whenever some action needs to
 * happen after a certain amount of time, either once or repeatedly.<br>
 * A PTimer can be started, paused, resumed, reset and stopped. In order
 * to run a PTimer needs to be owned by a PComponent to which it belongs.
 * If a PTimer is not owned by a PComponent or if the owner of the timer
 * is not part of a GUI (the component has no root) the timer will not
 * continue counting time.<br>
 * <br>
 * A timer can either be used for a repeating event or for an event that
 * is only triggered once. By default the timer is not repeating. Use the
 * {@link #setRepeating(boolean)} method to change the timer into a
 * repeating timer if needed. The time (in milliseconds) that the timer is
 * counting can be set with the {@link #setDelay(double)} method.<br>
 * 
 * @author NicStarzi
 */
public class PTimer {
	
	/**
	 * Used to observe the owner for root changes.
	 * Must be unregistered and registered when the owner changes.
	 */
	private final PComponentObs ownerObs = new PComponentObs() {
		@Override
		public void onRootChanged(PComponent component, PRoot currentRoot, PRoot oldRoot) {
			onRootChange();
		}
	};
	/**
	 * The code that will be executed when this timer expires
	 */
	protected final PTimerCallback callback;
	/**
	 * The component that this timer belongs to. Might be null.
	 */
	protected PComponent owner;
	/**
	 * The root at which this timer is registered right now.
	 * If the timer is not registered (because it is not started) this is null.
	 */
	protected PRoot currentRoot;
	/**
	 * When {@link #timeCount} reaches this value the timer is expired
	 */
	protected double delayInMillis = 1;
	/**
	 * Starts at 0 and counts to {@link #delayInMillis} and then resets back to
	 * 0.
	 */
	protected double timeCount;
	/**
	 * True if timer is supposed to start again after expiring
	 */
	protected boolean repeating;
	/**
	 * True if timer is supposed to be counting time.
	 * If this is true but {@link #currentRoot} is null the timer is
	 * disabled and can not count time
	 */
	protected boolean started;
	/**
	 * True if time should not be counted even if we are started and not
	 * disabled.
	 * A paused timer is not unregistered from the root, instead it will ignore
	 * any calls to the {@link #tick(double)} method.
	 */
	protected boolean paused;
	
	/**
	 * Creates a new, non-repeating timer with a delay of 1 millisecond that
	 * will
	 * trigger the code of <code>callback</code> when expired.<br>
	 * Please note that a timer needs an owner in order to run. Without setting
	 * its owner the timer will be disabled.<br>
	 * 
	 * @param callback the user code that is to be executed when the timer
	 *            expires, must not be null
	 */
	public PTimer(PTimerCallback callback) {
		this(null, callback);
	}
	
	/**
	 * Creates a new, non-repeating timer with a delay of 1 millisecond that
	 * will
	 * trigger the code of <code>callback</code> when expired.<br>
	 * The timer will be owned by <code>owner</code>.
	 * 
	 * @param owner the component that owns this timer or null
	 * @param callback the user code that is to be executed when the timer
	 *            expires, must not be null
	 */
	public PTimer(PComponent owner, PTimerCallback callback) {
		ThrowException.ifNull(callback, "callback");
		this.callback = callback;
		setOwner(owner);
	}
	
	/**
	 * Changes the owner of this timer. The owner can be set to null in which
	 * case
	 * the timer will be disabled.<br>
	 * If the timer is already started it will still be started without its time
	 * being reset. If the new owner is null or not part of a GUI the timer will
	 * be disabled.
	 * 
	 * @param component the new owner of this timer or null
	 */
	public void setOwner(PComponent component) {
		if (owner != component) {
			if (owner != null) {
				owner.removeObs(ownerObs);
			}
			owner = component;
			if (owner != null) {
				owner.addObs(ownerObs);
			}
			onRootChange();
		}
	}
	
	/**
	 * Returns the current owner of this timer. Might be null.<br>
	 * If the owner of a timer is null the timer is disabled.<br>
	 * 
	 * @return the owner of this timer or null
	 */
	public PComponent getOwner() {
		return owner;
	}
	
	/**
	 * If a timer is disabled it will not be actively counting down
	 * even if it is started.<br>
	 * A timer is disabled if it has no owner or if the owner is not
	 * part of a GUI.<br>
	 * 
	 * @return true if this timer is disabled
	 */
	public boolean isDisabled() {
		return getOwner() == null || getOwner().getRoot() == null;
	}
	
	/**
	 * Sets whether or not this timer should start again after expiring.<br>
	 * Changing this value has no effect on whether or not the timer is
	 * currently started or the delay until it expires.<br>
	 * 
	 * @param value true if the timer should repeat
	 */
	public void setRepeating(boolean value) {
		repeating = value;
	}
	
	/**
	 * If the timer is repeating it will start again after it was expired.<br>
	 * 
	 * @return true if the timer is repeating
	 */
	public boolean isRepeating() {
		return repeating;
	}
	
	/**
	 * Sets the pause flag for the timer. A timer that is paused will not
	 * continue counting time even if it is started and not disabled.<br>
	 * The difference between a paused timer and a timer that is stopped is,
	 * that a paused timer will resume counting from the delay it had before
	 * being paused. A timer that is restarted after having been stopped
	 * will be reset.<br>
	 * 
	 * @param value true if the timer should be paused
	 */
	public void setPaused(boolean value) {
		paused = value & started;
	}
	
	/**
	 * If the timer is paused it will not continue counting time but will
	 * remember the time it has already counted before being paused.<br>
	 * 
	 * @return true if the timer is paused
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Sets the delay of this timer to <code>value</code> (in milliseconds).<br>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being started.<br>
	 * The delay must be a positive integer.<br>
	 * 
	 * @param value a positive integer
	 */
	public void setDelay(double value) {
		ThrowException.ifLessOrEqual(0, value, "delay must be a positive number");
		delayInMillis = value;
	}
	
	/**
	 * Returns the current delay of the timer (in milliseconds).<br>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being started.<br>
	 * The delay is always a positive integer.<br>
	 * 
	 * @return a positive integer
	 */
	public double getDelay() {
		return delayInMillis;
	}
	
	public void setStarted(boolean isStarted) {
		if (isStarted) {
			start();
		} else {
			stop();
		}
	}
	
	/**
	 * Sets the started flag of this timer to true.<br>
	 * A started timer will count down the {@link #getDelay() delay}
	 * (in milliseconds) before expiring unless it is either paused
	 * or disabled.<br>
	 * When a timer expires it will run the
	 * {@link PTimerCallback#onTimerEvent(double)} method of its
	 * {@link PTimerCallback}.<br>
	 * <br>
	 * If this timer is already started this method call will be ignored.<br>
	 * 
	 * @see #stop()
	 * @see #restart()
	 * @see #isPaused()
	 */
	public void start() {
		if (!started) {
			currentRoot = getOwner().getRoot();
			if (currentRoot != null) {
				currentRoot.registerTimer(this);
			}
			timeCount = 0;
			started = true;
		}
	}
	
	/**
	 * Stops the timer if it was started before.<br>
	 * <br>
	 * If this timer was not started this method call will be ignored.<br>
	 * 
	 * @see #start()
	 * @see #restart()
	 * @see #isPaused()
	 */
	public void stop() {
		if (started) {
			if (currentRoot != null) {
				currentRoot.unregisterTimer(this);
				currentRoot = null;
			}
			timeCount = 0;
			started = false;
		}
	}
	
	/**
	 * Restarts the timer if it was started before.<br>
	 * <br>
	 * If this timer was not started this method call will be ignored.<br>
	 * 
	 * @see #start()
	 * @see #stop()
	 * @see #isPaused()
	 */
	public void restart() {
		if (started) {
			timeCount = 0;
		}
	}
	
	/**
	 * Returns true if this timer was started before.<br>
	 * Keep in mind that a timer might be paused or disabled!
	 * A started timer is not the same as a ticking timer.<br>
	 * If you need to know whether this timer is currently
	 * ticking call the {@link #isTicking()} method.<br>
	 * 
	 * @return true if this timer was started and not stopped since
	 * @see #isPaused()
	 * @see #isTicking()
	 */
	public boolean isStarted() {
		return started;
	}
	
	/**
	 * Returns true if this timer is currently ticking. A ticking
	 * timer is actively counting down time until it expires.
	 * (And if it is repeating it will start again)<br>
	 * A timer is ticking only if it is started, is not paused and
	 * is not disabled.<br>
	 * 
	 * @return true if this timer is started, is not paused and is not disabled
	 * @see #isStarted()
	 * @see #isPaused()
	 * @see #isDisabled()
	 */
	public boolean isTicking() {
		return isStarted() && !isPaused() && currentRoot != null;
	}
	
	/**
	 * Advances the time of this timer by <code>deltaMilliSc</code>
	 * milliseconds if this timer is both started and not paused.<br>
	 * This method will advance the time even if this timer is
	 * disabled.<br>
	 * If the timer has counted down its delay in milliseconds and it
	 * is repeating it will be reset.<br>
	 * 
	 * If the timer has counted down its delay in milliseconds it
	 * will expire and run the {@link PTimerCallback#onTimerEvent(double)}
	 * method of its {@link PTimerCallback}. If the timer is repeating
	 * it will then start counting time again.<br>
	 * 
	 * @param deltaMilliSc
	 */
	public void tick(double deltaMilliSc) {
		ThrowException.ifLess(0.0, deltaMilliSc, "deltaTime < 0");
		if (isStarted() && !isPaused()) {
			timeCount += deltaMilliSc;
			
//			System.out.println(timeCount + " > " + delayInMillis);
			if (timeCount >= delayInMillis) {
				if (isRepeating()) {
					timeCount = timeCount % delayInMillis;
				} else {
					stop();
				}
				callback.onTimerEvent(deltaMilliSc);
			}
		}
	}
	
	protected void onRootChange() {
		if (started) {
			PRoot oldRoot = currentRoot;
			PRoot newRoot = getOwner() == null ? null : getOwner().getRoot();
			
			if (newRoot == oldRoot) {
				/*
				 * The new root is the old root.
				 * If we were registered before we will still be
				 * registered afterwards.
				 * => We don't need to do anything!
				 */
			} else {
				// Root has changed. Remove from old root, add to new root
				if (oldRoot != null) {
					// Only remove from oldRoot if oldRoot exists
					oldRoot.unregisterTimer(this);
				}
				// If newRoot does not exist we can't register => set currentRoot to null
				if (newRoot != null) {
					newRoot.registerTimer(this);
					currentRoot = newRoot;
				} else {
					currentRoot = null;
				}
			}
		} else {
			/*
			 * We are not started => we were not registered
			 * => we don't need to be unregistered
			 * => we don't need to be registered
			 * => we don't need to remember the newRoot
			 */
			currentRoot = null;
		}
	}
	
}