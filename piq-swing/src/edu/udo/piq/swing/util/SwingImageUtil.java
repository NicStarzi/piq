package edu.udo.piq.swing.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import edu.udo.piq.PColor;

public class SwingImageUtil {
	
	public static BufferedImage createAcceleratedImg(final int w, final int h, final int transparency) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage compImg = gc.createCompatibleImage(w, h, transparency);
		return compImg;
	}
	
	public static BufferedImage createAcceleratedImgCopy(final BufferedImage img) {
		BufferedImage compImg = SwingImageUtil.createAcceleratedImg(img.getWidth(),
				img.getHeight(), img.getTransparency());
		Graphics compG = compImg.createGraphics();
		compG.drawImage(img, 0, 0, null);
		compG.dispose();
		return compImg;
	}
	
	public static BufferedImage createTintedCopy(BufferedImage original, PColor color) {
		int w = original.getWidth();
		int h = original.getHeight();
		int trans = original.getTransparency();
		BufferedImage result = SwingImageUtil.createAcceleratedImg(w, h, trans);
		
		Graphics2D g = result.createGraphics();
		g.drawImage(original, 0, 0, null);
		g.setComposite(MultiplyComposite.INSTANCE);
		g.setColor(new Color(color.getRed255(), color.getGreen255(), color.getBlue255(), color.getAlpha255()));
		g.fillRect(0, 0, w, h);
		g.dispose();
		
		return result;
	}
	
}