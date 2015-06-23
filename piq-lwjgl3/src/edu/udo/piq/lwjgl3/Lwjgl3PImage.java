package edu.udo.piq.lwjgl3;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class Lwjgl3PImage implements PImageResource {
	
	private final int glName;
	private PSize size = new ImmutablePSize(0, 0);
	
	public Lwjgl3PImage() {
		glName = GL11.glGenTextures();
	}
	
	public void loadFromStream(InputStream in) throws IOException {
		BufferedImage img = ImageIO.read(in);
		int w = img.getWidth();
		int h = img.getHeight();
		int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
		//TODO transform pixel array to texture data
		if (pixels == null) {
			throw new RuntimeException();
		}
		size = new ImmutablePSize(w, h);
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glName);
	}
	
	public PSize getSize() {
		return size;
	}
	
}