package edu.udo.piq;

import edu.udo.piq.layouts.PLayout;

/**
 * <p>An interface for elements which may need to be explicitly disposed after use. 
 * These elements may hold onto system resources that need to be freed or they may 
 * register observers and other data structures that need to be actively removed.</p>
 * 
 * <p>Examples of disposables are graphical resources like {@link PFontResource fonts}, 
 * {@link PImageResource images} and {@link PCursor cursors}; heavy weight components 
 * like {@link PDialog dialogs}; or non-reusable objects like {@link PLayout layouts}.</p>
 * 
 * @author Nic Starzi
 */
public interface PDisposable {
	
	/**
	 * <p>A clean up method which will release any system resources, unregister all 
	 * owned observers, remove timers and generally put this object into a safe state 
	 * for garbage collection.</p>
	 */
	public default void dispose() {}
	
}