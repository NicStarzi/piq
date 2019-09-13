package edu.udo.piq.lwjgl3;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import edu.udo.piq.PDisposable;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public class Fbo implements PDisposable {
	
	protected final int glNameFbo;
	protected int glNameTex;
	protected int w, h;
	protected boolean disposed = false;
	
	public Fbo(int width, int height) {
		Throw.ifLess(1, width, () -> "width == " + (width) + " < " + (1));
		Throw.ifLess(1, height, () -> "height == " + (height) + " < " + (1));
		w = width;
		h = height;
		glNameFbo = GL30.glGenFramebuffers();
		bind();
		
		GL11.glEnable(GL_TEXTURE_2D);
		glNameTex = GL11.glGenTextures();
		GL11.glBindTexture(GL_TEXTURE_2D, glNameTex);
		GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL11.GL_RGBA, GL_UNSIGNED_BYTE, 0);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		GL30.glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glNameTex, 0);
		GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);
		
		int fboStatus = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if(fboStatus != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error when creating FBO: "+getFrameBufferStatus(fboStatus));
		}
		unbind();
	}
	
	private String getFrameBufferStatus(int fboStatus) {
		switch (fboStatus) {
		case GL30.GL_FRAMEBUFFER_UNDEFINED : return "GL_FRAMEBUFFER_UNDEFINED";
		case GL30.GL_FRAMEBUFFER_COMPLETE : return "GL_FRAMEBUFFER_COMPLETE";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT : return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT : return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER : return "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER : return "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
		case GL30.GL_FRAMEBUFFER_UNSUPPORTED : return "GL_FRAMEBUFFER_UNSUPPORTED";
		case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE : return "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
		case GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS : return "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
		default: throw new IllegalStateException("fboStatus == "+fboStatus+"; unknown value");
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
		GL30.glBindFramebuffer(GL_FRAMEBUFFER, glNameFbo);
	}
	
	public void unbind() {
		GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void bindTexture() {
		ThrowException.ifTrue(disposed, "isDisposed() == true");
		GL11.glBindTexture(GL_TEXTURE_2D, glNameTex);
	}
	
	public void unbindTexture() {
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void resize(int newWidth, int newHeight) {
		ThrowException.ifTrue(disposed, "isDisposed() == true");
		if (w == newWidth && h == newHeight) {
			return;
		}
		GL11.glDeleteTextures(glNameTex);
		bind();
		
		w = newWidth;
		h = newHeight;
		glNameTex = GL11.glGenTextures();
		GL11.glBindTexture(GL_TEXTURE_2D, glNameTex);
		GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		GL32.glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, glNameTex, 0);
		GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);
		if(GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error when creating FBO");
		}
	}
	
	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		GL30.glDeleteFramebuffers(glNameFbo);
		GL11.glDeleteTextures(glNameTex);
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
}