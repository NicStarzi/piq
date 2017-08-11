package edu.udo.piq.swing;

import java.awt.image.BufferedImage;

import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class AwtPImageResource implements PImageResource {
	
	private final BufferedImage bImg;
	private final PSize size;
	
	public AwtPImageResource(BufferedImage img) {
		bImg = img;
		if (bImg == null) {
			size = PSize.ZERO_SIZE;
		} else {
			size = new ImmutablePSize(bImg.getWidth(), bImg.getHeight());
		}
	}
	
	public BufferedImage getBufferedImage() {
		return bImg;
	}
	
	@Override
	public PSize getSize() {
		return size;
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("type=");
		sb.append(getBufferedImage().getType());
		sb.append("; alpha=");
		sb.append(getBufferedImage().getTransparency());
		sb.append("; size=");
		sb.append(getSize().getWidth());
		sb.append("x");
		sb.append(getSize().getHeight());
		return sb.toString();
	}
}