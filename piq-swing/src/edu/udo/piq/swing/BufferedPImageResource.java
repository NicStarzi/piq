package edu.udo.piq.swing;

import java.awt.image.BufferedImage;

import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class BufferedPImageResource implements PImageResource {
	
	private final BufferedImage bImg;
	private final PSize size;
	
	public BufferedPImageResource(BufferedImage img) {
		bImg = img;
		if (bImg == null) {
			size = PSize.NULL_SIZE;
		} else {
			size = new ImmutablePSize(bImg.getWidth(), bImg.getHeight());
		}
	}
	
	public BufferedImage getBufferedImage() {
		return bImg;
	}
	
	public PSize getSize() {
		return size;
	}
}