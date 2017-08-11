package edu.udo.piq.lwjgl3;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import edu.udo.piq.PDisposable;
import edu.udo.piq.util.ThrowException;

public class Fbo implements PDisposable {
	
	protected final int glNameFbo;
	protected int glNameTex;
	protected int w, h;
	protected boolean disposed = false;
	
	public Fbo(int width, int height) {
		w = width;
		h = height;
		glNameFbo = glGenFramebuffers();
		bind();
		
		glNameTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, glNameTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, glNameTex, 0);
		glDrawBuffers(GL_COLOR_ATTACHMENT0);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error when creating FBO");
		}
	}
	
	public int getGlName() {
		return glNameFbo;
	}
	
	public int getTextureGlName() {
		return glNameTex;
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	public void bind() {
		ThrowException.ifTrue(disposed, "isDisposed() == true");
		glBindFramebuffer(GL_FRAMEBUFFER, glNameFbo);
	}
	
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void bindTexture() {
		ThrowException.ifTrue(disposed, "isDisposed() == true");
		glBindTexture(GL_TEXTURE_2D, glNameTex);
	}
	
	public void unbindTexture() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void resize(int newWidth, int newHeight) {
		ThrowException.ifTrue(disposed, "isDisposed() == true");
		if (w == newWidth && h == newHeight) {
			return;
		}
		glDeleteTextures(glNameTex);
		bind();
		
		w = newWidth;
		h = newHeight;
		glNameTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, glNameTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, glNameTex, 0);
		glDrawBuffers(GL_COLOR_ATTACHMENT0);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error when creating FBO");
		}
	}
	
	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		glDeleteFramebuffers(glNameFbo);
		glDeleteTextures(glNameTex);
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
}