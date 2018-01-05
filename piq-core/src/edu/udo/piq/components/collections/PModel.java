package edu.udo.piq.components.collections;

public interface PModel extends PReadOnlyModel {
	
	public boolean canSet(PModelIndex index, Object content)
			throws WrongIndexType, NullPointerException;
	
	public void set(PModelIndex index, Object content);
	
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
	
}