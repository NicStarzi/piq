package edu.udo.piq.components.collections;

import java.util.ConcurrentModificationException;

import edu.udo.piq.components.util.PModelHistory;

public interface PModel extends Iterable<PModelIndex> {
	
	public void set(PModelIndex index, Object content);
	
	/**
	 * Returns the object that is stored within this model at the 
	 * given {@link PModelIndex index}. This value can be null if 
	 * this model allows for null entries.<br>
	 * If the given index is not valid an exception will be thrown.<br>
	 * 
	 * @param index				an index into this model, must be of the right type
	 * @return					the content of this model at the given index
	 * @throws WrongIndexType	if the index is not of the right type
	 * @throws IllegalIndex		if this model does not contain the index
	 * @throws NullPointerException		if index is null
	 * @see #contains(PModelIndex)
	 */
	public Object get(PModelIndex index) 
			throws WrongIndexType, NullPointerException, IllegalIndex;
	
	/**
	 * Returns true if this model has content stored at the given 
	 * {@link PModelIndex index}. Otherwise false is returned.<br>
	 * The index must still be of a valid type or an exception is 
	 * thrown.<br>
	 * 
	 * @param index				the queried index
	 * @return					true if there is data at this index
	 * @throws WrongIndexType	if the index is not of the right type
	 * @throws NullPointerException		if index is null
	 */
	public boolean contains(PModelIndex index) 
			throws WrongIndexType, NullPointerException;
	
	/**
	 * Returns an instance of {@link PModelIndex} at which the given 
	 * content is stored within this model. The returned index is 
	 * always a valid index for this model.<br>
	 * If the content is not contained within this model null is 
	 * returned.<br>
	 * 
	 * @param content			an object or null that may or may not be stored within this model
	 * @return					an instance of {@link PModelIndex} or null
	 */
	public PModelIndex getIndexOf(Object content);
	
	/**
	 * Returns true if content can be added to this model at index, 
	 * otherwise false is returned. The reasons for why content can 
	 * not be added may included but is not limited to:<br> 
	 *  - the index being out of bounds<br>
	 *  - content being null and the model not allowing null entries<br>
	 *  - content being of a wrong type<br>
	 * <br>
	 * If this method returns true then a call to 
	 * {@link #add(PModelIndex, Object)} with the same arguments will 
	 * succeed.<br>
	 * 
	 * @param index				the index where content should be added to
	 * @param content			an object or null that is to be added
	 * @return					true if adding content at index is possible
	 * @throws WrongIndexType	if index has the wrong type
	 * @throws NullPointerException		if index is null
	 * @see #add(PModelIndex, Object)
	 */
	public boolean canAdd(PModelIndex index, Object content) 
			throws WrongIndexType, NullPointerException;
	
	/**
	 * Tries to add content to this model at the given index. If 
	 * adding is possible content will be stored within this model 
	 * at index. Other contents within this model might have their 
	 * index changed as a result of this call, for example, any 
	 * contents previously stored at index will now be moved to a 
	 * different index.<br>
	 * If adding content at index is not possible for some reason 
	 * an {@link AddImpossible} exception is being thrown. Use the 
	 * {@link #canAdd(PModelIndex, Object)} method to check whether 
	 * adding is possible before calling this method.<br>
	 * 
	 * @param index				the index where content should be added
	 * @param content			the content that is to be added
	 * @throws WrongIndexType	if index has the wrong type
	 * @throws NullPointerException		if index is null
	 * @throws AddImpossible	if adding is not possible for any reason
	 * @see #canAdd(PModelIndex, Object)
	 */
	public void add(PModelIndex index, Object content) 
			throws WrongIndexType, NullPointerException, AddImpossible;
	
	/**
	 * Returns true if any content can be removed from this model at index, 
	 * otherwise false is returned. The reason for why content can not be 
	 * removed may vary but might be that index is out of bounds.<br> 
	 * <br>
	 * If this method returns true then a call to 
	 * {@link #remove(PModelIndex)} with the same argument will succeed.<br>
	 * 
	 * @param index				the index at which content should be removed
	 * @return					true if removing at index is possible
	 * @throws WrongIndexType	if index has the wrong type
	 * @throws NullPointerException		if index is null
	 * @see #remove(PModelIndex)
	 */
	public boolean canRemove(PModelIndex index) 
			throws WrongIndexType, NullPointerException;
	
	public default boolean canRemove(Iterable<PModelIndex> indices) {
		for (PModelIndex index : indices) {
			if (!canRemove(index)) {
				return false;
			}
		}
		return true;
	}
	
	public default boolean canRemove(PModelIndex ... indices) {
		for (int i = 0; i < indices.length; i++) {
			PModelIndex index = indices[i];
			if (!canRemove(index)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tries to remove content from this model at the given index. If 
	 * removing is possible the content at index will be removed and 
	 * other content within this model might have their index changed 
	 * as a result of this call.<br>
	 * If removing content at index is not possible for some reason 
	 * a {@link RemoveImpossible} exception is being thrown. Use the 
	 * {@link #canRemove(PModelIndex)} method to check whether 
	 * removing is possible before calling this method.<br>
	 * 
	 * @param index						the index at which content should be removed
	 * @throws WrongIndexType			if index has the wrong type
	 * @throws NullPointerException		if index is null
	 * @throws RemoveImpossible			if removing is not possible for any reason
	 * @see #canRemove(PModelIndex)
	 */
	public void remove(PModelIndex index) 
			throws WrongIndexType, NullPointerException, RemoveImpossible;
	
	public default void removeAll(Iterable<PModelIndex> indices) {
		for (PModelIndex index : indices) {
			remove(index);
		}
	}
	
	public default void removeAll(PModelIndex ... indices) {
		for (int i = 0; i < indices.length; i++) {
			PModelIndex index = indices[i];
			remove(index);
		}
	}
	
	/**
	 * Adds the given {@link PModelObs observer} to this model.<br> 
	 * Observers are notified of any changes to the model, these 
	 * include:<br>
	 *  - when content is added<br>
	 *  - when content is removed<br>
	 *  - when content has changed in some way<br>
	 * An observer is usually used by the 
	 * {@link PSelectionComponent component} that represents this 
	 * model visually.<br>
	 * 
	 * @param obs						a non-null observer
	 * @throws NullPointerException		if obs is null
	 */
	public void addObs(PModelObs obs) 
			throws NullPointerException;
	
	/**
	 * Removes the {@link PModelObs observer} from this model if it 
	 * was added previously. If the observer was not added before 
	 * this method will do nothing.<br>
	 * After a call to this method the observer will no longer 
	 * receive any notifications. If the observer is removed during 
	 * a notification there will be <b>no</b> 
	 * {@link ConcurrentModificationException}.<br>
	 * 
	 * @param obs						a non-null observer
	 * @throws NullPointerException		if obs is null
	 */
	public void removeObs(PModelObs obs) 
			throws NullPointerException;
	
	/**
	 * Returns the {@link PModelHistory} for this model or null if 
	 * this model does not support undo- and redo functionality.<br>
	 * @return		a model history or null
	 */
	public default PModelHistory getHistory() {
		return null;
	}
	
}