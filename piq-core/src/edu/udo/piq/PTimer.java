package edu.udo.piq;

import edu.udo.piq.util.Throw;
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
	
	public static final double DEFAULT_DELAY = 1;
	
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
	 * When {@link #timeCount} reaches this value the timer will expire
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
	protected boolean running;
	/**
	 * True if time should not be counted even if we are started and not
	 * disabled.
	 * A paused timer is not unregistered from the root, instead it will ignore
	 * any calls to the {@link #tick(double)} method.
	 */
	protected boolean paused;
	
	/**
	 * Creates a new, {@link #isRepeating() non-repeating} timer with a {@link #getDelay() delay} of
	 * {@value #DEFAULT_DELAY} milliseconds. The timer will
	 * {@link PTimerCallback#onTimerEvent(double) trigger the callback} when {@link #tick(double) expired}.
	 * <p>
	 * The constructed timer will initially be {@link #isDisabled() disabled} because it will have no
	 * {@link #getOwner() owner}. You can {@link #setOwner(PComponent) set the owner} of the timer at a
	 * later time to enable the timer. Without an owner a timer will not count time.
	 * <p>
	 * @param callback		the user code that is to be executed when this timer expires. Must not be {@code null}.
	 * @see #setOwner(PComponent)
	 * @see #setDelay(double)
	 * @see #setDelay(TimeUnit, double)
	 */
	public PTimer(PTimerCallback callback) {
		this(null, DEFAULT_DELAY, callback);
	}
	
	/**
	 * Creates a new, {@link #isRepeating() non-repeating} timer with a {@link #getDelay() delay} of
	 * {@value #DEFAULT_DELAY} milliseconds. The timer will
	 * {@link PTimerCallback#onTimerEvent(double) trigger the callback} when {@link #tick(double) expired}.
	 * <p>
	 * The timer will be {@link #getOwner() owned} by the given {@code owner}. The owner can be
	 * {@code null} in which case the timer will be {@link #isDisabled() disabled}.
	 * <p>
	 * @param owner			the {@link PComponent component} that {@link #getOwner() owns} this timer or {@code null}.
	 * @param callback		the user code that is to be executed when this timer expires. Must not be {@code null}.
	 * @see #setDelay(double)
	 * @see #setDelay(TimeUnit, double)
	 */
	public PTimer(PComponent owner, PTimerCallback callback) {
		this(owner, DEFAULT_DELAY, callback);
	}
	
	/**
	 * Creates a new, {@link #isRepeating() non-repeating} timer with a {@link #getDelay() delay} of
	 * {@code delay} in the given {@link TimeUnit timeUnit}. The timer will
	 * {@link PTimerCallback#onTimerEvent(double) trigger the callback} when {@link #tick(double) expired}.
	 * <p>
	 * The timer will be {@link #getOwner() owned} by the given {@code owner}. The owner can be
	 * {@code null} in which case the timer will be {@link #isDisabled() disabled}.
	 * <p>
	 * The delay must be greater than 0.0, otherwise an exception will be thrown.
	 * <p>
	 * @param owner			the {@link PComponent component} that {@link #getOwner() owns} this timer or {@code null}.
	 * @param timeUnit		the {@link TimeUnit} used for the delay. Must not be {@code null}.
	 * @param delay			the {@link #getDelay() delay} of this timer. Must be greater than 0.0.
	 * @param callback		the user code that is to be executed when this timer expires. Must not be {@code null}.
	 */
	public PTimer(PComponent owner, TimeUnit timeUnit, double delay, PTimerCallback callback) {
		this(owner, timeUnit.convertTo(TimeUnit.MILLI_SECONDS, delay), callback);
	}
	
	/**
	 * Creates a new, {@link #isRepeating() non-repeating} timer with a {@link #getDelay() delay} of
	 * {@code delayInMs} milliseconds. The timer will
	 * {@link PTimerCallback#onTimerEvent(double) trigger the callback} when {@link #tick(double) expired}.
	 * <p>
	 * The timer will be {@link #getOwner() owned} by the given {@code owner}. The owner can be
	 * {@code null} in which case the timer will be {@link #isDisabled() disabled}.
	 * <p>
	 * The delay must be greater than 0.0, otherwise an exception will be thrown.
	 * <p>
	 * @param owner			the {@link PComponent component} that {@link #getOwner() owns} this timer or {@code null}.
	 * @param delayInMs		the {@link #getDelay() delay} of this timer in milliseconds. Must be greater than 0.0.
	 * @param callback		the user code that is to be executed when this timer expires. Must not be {@code null}.
	 */
	public PTimer(PComponent owner, double delayInMs, PTimerCallback callback) {
		ThrowException.ifNull(callback, "callback");
		this.callback = callback;
		setDelay(delayInMs);
		setOwner(owner);
	}
	
	/**
	 * Changes the owner of this timer. The owner can be set to null in which
	 * case the timer will be disabled. If the timer is disabled it will not
	 * count time even if it is {@link #isRunning() running}.<p>
	 * When the owner of a timer is changed while the timer is already
	 * {@link #isRunning() running} it will continue to be running without its
	 * time being reset.<p>
	 * 
	 * @param component		the new owner of this timer or null
	 * @see #getOwner()
	 * @see #isDisabled()
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
	 * Returns the current owner of this timer. Might be {@code null}.<p>
	 * If the owner of a timer is {@code null} the timer is
	 * {@link #isDisabled() disabled}.<p>
	 * 
	 * @return the owner of this timer or {@code null}
	 * @see #setOwner(PComponent)
	 * @see #isDisabled()
	 */
	public PComponent getOwner() {
		return owner;
	}
	
	/**
	 * If a timer is disabled it will not be actively counting down even if it is
	 * {@link #isRunning() running}.<p>
	 * A timer is disabled if it has no {@link #getOwner() owner} or if the owner
	 * is {@link PComponent#getRoot() not part of a GUI}.<p>
	 * 
	 * @return true	if this timer is currently disabled. Do not confuse this method with {@link #isRunning()}.
	 * @see #isRunning()
	 * @see #isPaused()
	 * @see #isTicking()
	 * @see #getOwner()
	 */
	public boolean isDisabled() {
		return getOwner() == null || getOwner().getRoot() == null;
	}
	
	/**
	 * Sets whether or not this timer should start again after expiring.<p>
	 * Changing this value has no effect on whether or not the timer is
	 * currently {@link #isRunning() running} or the {@link #getDelay() delay}
	 * until it expires.<p>
	 * 
	 * @param value			true if the timer should repeat
	 * @see #isRepeating()
	 */
	public void setRepeating(boolean value) {
		repeating = value;
	}
	
	/**
	 * If the timer is repeating it will {@link #start() start} again after it expired.<p>
	 * 
	 * @return true if the timer is repeating
	 * @see #setRepeating(boolean)
	 */
	public boolean isRepeating() {
		return repeating;
	}
	
	/**
	 * Pauses this timer in its current state. A paused timer is still
	 * {@link #isRunning() running} but it is no longer counting time until it is
	 * unpaused. A timer which is not running <b>can not be paused</b>. A disabled
	 * timer can be paused. If a timer is {@link #stop() stopped} or
	 * {@link #restart() restarted} it will automatically be unpaused.<p>
	 * 
	 * If a timer is unpaused it will resume counting time from the state it was
	 * in before it was paused. If the timer is {@link #restart() restarted} its
	 * internal state will be reset and it will begin counting time from the start
	 * of its {@link #getDelay() delay}.
	 * 
	 * @param value			true if the timer should be paused
	 * @see #isPaused()
	 * @see #isRunning()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public void setPaused(boolean value) {
		paused = value & isRunning();
	}
	
	/**
	 * If the timer is paused it will not continue to count time. Only a
	 * {@link #isRunning() running} timer can be paused. If a timer is
	 * {@link #stop() stopped} or {@link #restart() restarted} it will
	 * automatically be unpaused.<p>
	 * 
	 * @return true if the timer is paused
	 * @see #setPaused(boolean)
	 * @see #isRunning()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Sets the delay of this timer to {@code value} using the given
	 * {@link TimeUnit}. The {@link TimeUnit#convertTo(TimeUnit, double)}
	 * method is used for time conversion.<p>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being {@link #start() started}.<p>
	 * <b>The delay must be a positive value.</b>
	 * {@link Double#NaN} is not a valid argument.<p>
	 * 
	 * @param value			must be a positive value below {@link Double#POSITIVE_INFINITY}
	 * @throws IllegalArgumentException		if {@code value} is <= 0,
	 * 										{@link Double#POSITIVE_INFINITY} or {@link Double#NaN}
	 * @see #setDelay(double)
	 * @see #getDelay(TimeUnit)
	 * @see #getDelay()
	 */
	public void setDelay(TimeUnit timeUnit, double value) {
		setDelay(timeUnit.convertTo(TimeUnit.MILLI_SECONDS, value));
	}
	
	/**
	 * Sets the delay of this timer to {@code value} (in milliseconds).<p>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being {@link #start() started}.<p>
	 * <b>The delay must be a positive value.</b>
	 * {@link Double#NaN} is not a valid argument.<p>
	 * 
	 * @param value			must be a positive value below {@link Double#POSITIVE_INFINITY}
	 * @throws IllegalArgumentException		if {@code value} is <= 0,
	 * 										{@link Double#POSITIVE_INFINITY} or {@link Double#NaN}
	 * @see #setDelay()
	 * @see #getDelay(TimeUnit)
	 * @see #getDelay()
	 */
	public void setDelay(double value) {
		ThrowException.ifLessOrEqual(0, value, "delay must be a positive number");
		Throw.ifTrue(Double.isNaN(value), () -> "Double.isNaN(value) == true");
		Throw.ifTrue(Double.isInfinite(value), () -> "Double.isInfinite(value) == true");
		delayInMillis = value;
	}
	
	/**
	 * Returns the current delay of the timer for the given
	 * {@link TimeUnit}. The {@link TimeUnit#convertTo(TimeUnit, double)}
	 * method is used for time conversion.<p>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being {@link #start() started}.<p>
	 * The delay is always a positive value.<p>
	 * 
	 * @return a positive value
	 * @see #setDelay(double)
	 * @see #setDelay(TimeUnit, double)
	 * @see #getDelay()
	 */
	public double getDelay(TimeUnit timeUnit) {
		return TimeUnit.MILLI_SECONDS.convertTo(timeUnit, getDelay());
	}
	
	/**
	 * Returns the current delay of the timer (in milliseconds).<p>
	 * The delay of a timer is the time it takes the timer to expire after
	 * being {@link #start() started}.<p>
	 * The delay is always a positive value.<p>
	 * 
	 * @return a positive value
	 * @see #setDelay(double)
	 * @see #setDelay(TimeUnit, double)
	 * @see #getDelay(TimeUnit)
	 */
	public double getDelay() {
		return delayInMillis;
	}
	
	/**
	 * {@link #start() Starts} of {@link #stop() stops} this timer depending on whether
	 * or not it is currently {@link #isRunning() running} and the value of the argument.<p>
	 * 
	 * If this timer is already running and {@code isRunning} is {@code true} this
	 * method does nothing. This method will also do nothing if it is not running and
	 * {@code isRunning} is {@code false}.<p>
	 * 
	 * @param isRunning		true if this timer should be {@link #isRunning() running}, otherwise false.
	 * @see #start()
	 * @see #stop()
	 * @see #isRunning()
	 */
	public void setRunning(boolean isRunning) {
		if (isRunning) {
			start();
		} else {
			stop();
		}
	}
	
	/**
	 * If the timer is already {@link #isRunning() running} this method does nothing.
	 * If the timer is not running it will be started. A started timer will count down
	 * the {@link #getDelay() delay} (in milliseconds) before expiring unless it is
	 * {@link #isDisabled() disabled}.<p>
	 * 
	 * After a timer has been started it is {@link #isRunning() running} and <b>not</b>
	 * {@link #isPaused() paused}. Being started has no effect on whether or not the
	 * timer is {@link #isDisabled()}.<p>
	 * 
	 * When a timer expires it will run the
	 * {@link PTimerCallback#onTimerEvent(double)} method of its
	 * {@link PTimerCallback}.<p>
	 * 
	 * @see #stop()
	 * @see #restart()
	 * @see #isRunning()
	 * @see #isPaused()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public void start() {
		if (!isRunning()) {
			currentRoot = getOwner().getRoot();
			if (currentRoot != null) {
				currentRoot.registerTimer(this);
			}
			timeCount = 0;
			running = true;
			// a subclass of PTimer may not set 'paused' to false when the timer is stopped.
			paused = false;
		}
	}
	
	/**
	 * If the timer is not currently {@link #isRunning() running} this method does nothing.
	 * If the timer is running it will be stopped. A stopped timer will no longer count down
	 * time. Any time it has counted down so far will be lost.<p>
	 * 
	 * After a timer has been started it is {@link #isRunning() running} and <b>not</b>
	 * {@link #isPaused() paused}. Being started has no effect on whether or not the
	 * timer is {@link #isDisabled()}.<p>
	 * 
	 * When a timer expires it will run the
	 * {@link PTimerCallback#onTimerEvent(double)} method of its
	 * {@link PTimerCallback}.<p>
	 * 
	 * @see #stop()
	 * @see #restart()
	 * @see #isRunning()
	 * @see #isPaused()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public void stop() {
		if (isRunning()) {
			if (currentRoot != null) {
				currentRoot.unregisterTimer(this);
				currentRoot = null;
			}
			timeCount = 0;
			running = false;
			// a stopped timer is never paused.
			paused = false;
		}
	}
	
	/**
	 * If the timer is not currently {@link #isRunning() running} this method does nothing.
	 * If the timer is running its internal state will be reset and it will begin counting
	 * from the start of the {@link #getDelay() delay} again.<p>
	 * 
	 * After a timer has been restarted it is {@link #isRunning() running} and <b>not</b>
	 * {@link #isPaused() paused}. Being restarted has no effect on whether or not the
	 * timer is {@link #isDisabled()}.<p>
	 * 
	 * The state of the timer is otherwise unchanged.<p>
	 * 
	 * @see #start()
	 * @see #stop()
	 * @see #isRunning()
	 * @see #isPaused()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public void restart() {
		if (isRunning()) {
			timeCount = 0;
			paused = false;
		}
	}
	
	/**
	 * Returns {@code true} if this timer is currently running. A timer is running if
	 * it was {@link #start() started} before. When a timer is {@link #stop() stopped}
	 * or when it {@link #tick(double) expires} it is no longer running.
	 * <p>
	 * A running timer may be {@link #isPaused() paused} or {@link #isDisabled() disabled}.
	 * To check whether a timer is actually counting down time use the {@link #isTicking()}
	 * method instead.
	 * <p>
	 * @return true				if this timer was {@link #start() started} and has not yet
	 * 							been {@link #stop() stopped} or {@link #tick(double) expired}.
	 * @see #start()
	 * @see #stop()
	 * @see #restart()
	 * @see #isPaused()
	 * @see #isDisabled()
	 * @see #isTicking()
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Returns true if this timer is currently ticking. A ticking
	 * timer is actively counting down time until it expires.
	 * (And if it is repeating it will start again)<br>
	 * A timer is ticking only if it is started, is not paused and
	 * is not disabled.<br>
	 * 
	 * @return true if this timer is started, is not paused and is not disabled
	 * @see #isRunning()
	 * @see #isPaused()
	 * @see #isDisabled()
	 */
	public boolean isTicking() {
		return isRunning() && !isPaused() && currentRoot != null;
	}
	
	/**
	 * Advances the time of this timer by {@code deltaMilliSc} many milliseconds if
	 * it is not {@link #isPaused() paused}. If this timer is paused this method does
	 * nothing.<p>
	 * 
	 * If the timer has counted down its {@link #getDelay() delay} it will expire
	 * and call the {@link PTimerCallback#onTimerEvent(double) onTimerEvent(double)}
	 * method of its {@link PTimerCallback} with the time between now and the previous
	 * expiration as the argument. If the timer has never expired before, the time
	 * since the timer was started will be used instead. If this timer is
	 * {@link #isRepeating() repeating} it will be {@link #restart() restarted}
	 * afterwards. Otherwise it will be stopped. In either case the callback will
	 * be called last.<p>
	 * 
	 * This method <b>assumes</b> that this timer is both {@link #isRunning() running}
	 * and not {@link #isDisabled() disabled} when this method is called. It will not
	 * check whether these pre-conditions are actually true or not. A user may call
	 * this method while the timer is not supposed to be ticked if so desired.<p>
	 * 
	 * @param deltaMilliSc		the time between now and the previous call to this method in milliseconds
	 * @see #start()
	 * @see #stop()
	 * @see #restart()
	 * @see #isRunning()
	 * @see #isDisabled()
	 * @see #isPaused()
	 * @see #isTicking()
	 */
	public void tick(double deltaMilliSc) {
		if (isPaused()) {
			return;
		}
		timeCount += deltaMilliSc;
		if (timeCount >= delayInMillis) {
			if (isRepeating()) {
				timeCount = timeCount % delayInMillis;
			} else {
				stop();
			}
			callback.onTimerEvent(deltaMilliSc);
		}
	}
	
	/**
	 * Called any timer the {@link #getOwner() owner} of this timer has changed or if the
	 * {@link PComponent#getRoot() root} of the owner has changed. This method handles
	 * the {@link PRoot#registerTimer(PTimer) registration} of this timer and the caching
	 * of the current root. Changes to this method should only be taken with care.
	 */
	protected void onRootChange() {
		if (isRunning()) {
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