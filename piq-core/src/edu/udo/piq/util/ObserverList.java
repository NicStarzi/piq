package edu.udo.piq.util;

import java.util.function.Consumer;

public interface ObserverList<E> {
	
	/**
	 * Adds the new observer to this {@link ObserverList}. If an observer is
	 * added more then once it will be notified once for each time it is
	 * registered.<br>
	 * The newly added observer will be notified the next time a notify message
	 * is sent. Any messages that are being sent right now will <b>ignore</b>
	 * the new observer.<br>
	 * 
	 * @param obs						a non-null instance of E
	 * @throws NullPointerException		if obs is null
	 * @see #fireEvent(Message)
	 */
	public void add(E obs);
	
	/**
	 * Removes the observer obs from this {@link ObserverList} if it is present.<br>
	 * The observer will only be removed once from this list, if it was added more
	 * then once it might still be contained in this list after this method has
	 * returned.<br>
	 * A removed observer will no longer be notified when {@link #fireEvent(Message)}
	 * is called, but any messages that are being sent right now will still notify
	 * the removed observer.<br>
	 * 
	 * @param obs
	 * @throws NullPointerException		if obs is null
	 * @see #fireEvent(Message)
	 */
	public void remove(E obs);
	
	/**
	 * Returns the number of observers that are registered at this {@link ObserverList}.<br>
	 * @return	the size of this list
	 */
	public int getSize();
	
	/**
	 * Returns true if no observers are registered at this {@link ObserverList}. Otherwise
	 * false is returned.<br>
	 * @return	true if size is zero
	 */
	public default boolean isEmpty() {
		return getSize() == 0;
	}
	
	/**
	 * Notifies all observers that are contained within this {@link ObserverList} at the
	 * time when this method is called. Each observer will be notified as often as it was
	 * previously added to this list. <br>
	 * If the {@link ObserverList} is being modified before this method returns the
	 * changes will be ignored by the notify message and the old contents will be used
	 * instead.<br>
	 * No assumptions should be made about the order in which observers will be
	 * notified.<br>
	 * 
	 * @param msg						the message that will be sent to all observers
	 * @throws NullPointerException		if msg is null
	 */
	public void fireEvent(Message<E> msg);
	
	public default void forEach(Consumer<E> action) {
		fireEvent(obs -> action.accept(obs));
	}
	
	/**
	 * This functional interface represents the message that is to be sent to all
	 * observers of type E within an {@link ObserverList} of type E.<br>
	 * The method {@link #notifyObs(Object)} will be called exactly once for each
	 * element within the {@link ObserverList}.<br>
	 * No assumptions should be made about the order in which observers will be
	 * notified.<br>
	 * @param <E>						the type of the observer
	 */
	public static interface Message<E> {
		/**
		 * This method will be called exactly once for each element within the
		 * {@link ObserverList}.<br>
		 * @param obs	a non-null observer
		 */
		public void notifyObs(E obs);
	}
	
}