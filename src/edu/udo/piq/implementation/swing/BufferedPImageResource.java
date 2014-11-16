package edu.udo.piq.implementation.swing;

import java.awt.image.BufferedImage;

import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class BufferedPImageResource implements PImageResource {
	
	private final BufferedImage bImg;
	private final PSize size;
	
	public BufferedPImageResource(BufferedImage img) {
		if (img == null) {
			throw new NullPointerException("img="+img);
		}
		bImg = img;
		size = new ImmutablePSize(bImg.getWidth(), bImg.getHeight());
	}
	
	public BufferedImage getBufferedImage() {
		return bImg;
	}
	
	public PSize getSize() {
		return size;
	}
}