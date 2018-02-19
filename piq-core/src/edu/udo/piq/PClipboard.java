package edu.udo.piq;

/**
 * <p>An abstraction of the systems clipboard. The clipboard can be used 
 * to temporarily store some kind of content for later use. The most 
 * common application for the clipboard is support for copy, cut and paste 
 * operations.</p>
 * 
 * <p>The implementation need not necessarily be a binding for the OS 
 * clipboard. A pure java implementation is possible. The implementation 
 * may also impose further limits on the types of contents it can store. 
 * In any case the implementation is supposed to take care of illegal 
 * inputs gracefully.</p>
 * 
 * @author Nic Starzi
 */
public interface PClipboard {
	
	/**
	 * <p>Attempts to store the given object in this clipboard. If the 
	 * clipboard can not store the object, this method will return {@code false}. 
	 * If storing the object is possible this method will return {@code true}.</p>
	 * <p>This method will throw a {@link NullPointerException} if {@code null} is 
	 * passed as an argument. No other exception will be thrown for illegal 
	 * inputs.</p>
	 * @param obj						the object that is supposed to be stored
	 * @throws NullPointerException		if obj is {@code null}
	 * @return							{@code true} if the object was successfully stored, otherwise {@code false}
	 */
	public boolean put(Object obj);
	
	/**
	 * <p>Attempts to load an object from the clipboard and return it. If 
	 * loading the object fails for whatever reason, or if no object was 
	 * stored previously, this method will return {@code null}.</p>
	 * <p>This method should never throw any exceptions because of illegal 
	 * contents or clipboard state. It should handle these situations 
	 * gracefully.</p>
	 * @return							the previously stored object or {@code null} if none can be loaded
	 */
	public Object get();
	
}