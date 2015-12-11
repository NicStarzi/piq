package edu.udo.piq;

import edu.udo.piq.tools.DoNothingRenderer;

/**
 * This interface defines abstract platform independent image resources.<br>
 * The implementation of these image resources depends heavily on the used 
 * rendering techniques used by the {@link PRenderer}.<br>
 * Instances of image resources can be obtained from a {@link PRoot} 
 * implementation.<br>
 * 
 * @author Nic Starzi
 */
public interface PImageResource extends PDisposable {
	
	/**
	 * Returns the {@link PSize} of this image resource in width and height.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the size for this image
	 */
	public PSize getSize();
	
	/**
	 * Creates a new {@link PRenderer} that can be used to paint this 
	 * {@link PImageResource}.<br>
	 * The returned renderer is never null but might do nothing at all if 
	 * custom painting of images is not possible.<br>
	 * @return			a non-null instance of {@link PRenderer}
	 */
	public default PImageRenderer createRenderer() {
		return new DoNothingRenderer();
	}
	
}