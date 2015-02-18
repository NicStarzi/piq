package edu.udo.piq;

/**
 * This interface defines abstract platform independent image resources.<br>
 * The implementation of these image resources depends heavily on the used 
 * rendering techniques used by the {@link PRenderer}.<br>
 * Instances of image resources can be obtained from a {@link PRoot} 
 * implementation.<br>
 * 
 * @author Nic Starzi
 */
public interface PImageResource {
	
	/**
	 * Returns the {@link PSize} of this image resource in width and height.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the size for this image
	 */
	public PSize getSize();
	
}