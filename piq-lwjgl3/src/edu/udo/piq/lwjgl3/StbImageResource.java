package edu.udo.piq.lwjgl3;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPSize;

public class StbImageResource implements PImageResource {
	
	protected final int glName;
	protected final int imgW, imgH;
	protected final TexelFormat texelFormat;
	protected final int texelType;
	protected final PSize imgSize = new AbstractPSize() {
		@Override
		public int getWidth() {
			return imgW;
		}
		@Override
		public int getHeight() {
			return imgH;
		}
	};
	protected boolean disposed;
	
	public StbImageResource(int texGlName, int imageWidth, int imageHeight,
			TexelFormat internalFormat, int internalType)
	{
		glName = texGlName;
		imgW = imageWidth;
		imgH = imageHeight;
		texelFormat = internalFormat;
		texelType = internalType;
	}
	
	public StbImageResource(int imageWidth, int imageHeight) {
		imgW = imageWidth;
		imgH = imageHeight;
		texelFormat = TexelFormat.RGBA;
		texelType = GL11.GL_UNSIGNED_BYTE;
		glName = GL11.glGenTextures();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, texelFormat.glName,
				imgW, imgH, 0, GL11.GL_RGBA, texelType,
				(ByteBuffer) null);
	}
	
	public StbImageResource(File imageFile) throws IOException {
		int glName = 0;
		int imgW = -1;
		int imgH = -1;
		boolean hasAlpha = false;
		TexelFormat texelFormat;
		texelType = GL11.GL_UNSIGNED_BYTE;
		try (FileInputStream fis = new FileInputStream(imageFile);
				FileChannel fc = fis.getChannel();)
		{
			ByteBuffer imageFileDataBuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			
			IntBuffer bufWidth	= BufferUtils.createIntBuffer(1);
			IntBuffer bufHeight	= BufferUtils.createIntBuffer(1);
			IntBuffer bufComps	= BufferUtils.createIntBuffer(1);
			
			ByteBuffer stbiImageBuf = stbi_load_from_memory(imageFileDataBuf, bufWidth, bufHeight, bufComps, 0);
			if (stbiImageBuf == null) {
				throw new RuntimeException("Failed to load image: "+stbi_failure_reason());
			}
			imgW = bufWidth.get(0);
			imgH = bufHeight.get(0);
			int compCount = bufComps.get(0);
			hasAlpha = compCount == 4;
			
			glName = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glName);
			if (hasAlpha) {
				texelFormat = TexelFormat.RGBA;
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, texelFormat.glName,
						imgW, imgH, 0, GL11.GL_RGBA, texelType,
						stbiImageBuf);
			} else {
				if ((imgW & 3) != 0) {
					GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (imgW & 1));
				}
				texelFormat = TexelFormat.RGB;
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, texelFormat.glName,
						imgW, imgH, 0, GL11.GL_RGB, texelType,
						stbiImageBuf);
			}
			stbi_image_free(stbiImageBuf);
			
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		this.glName = glName;
		this.imgW = imgW;
		this.imgH = imgH;
		this.texelFormat = texelFormat;
	}
	
	public int getGlName() {
		return glName;
	}
	
	public TexelFormat getTexelFormat() {
		return texelFormat;
	}
	
	public int getTexelType() {
		return texelType;
	}
	
	public boolean hasAlpha() {
		return texelFormat.hasAlpha;
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glName);
	}
	
	public ByteBuffer getTexels() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(imgW * imgH * texelFormat.componentCount);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, texelFormat.glName, texelType, buffer);
		return buffer;
	}
	
	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		GL11.glDeleteTextures(glName);
	}
	
	@Override
	protected void finalize() {
		dispose();
	}
	
	@Override
	public PSize getSize() {
		return imgSize;
	}
	
	@Override
	public int getWidth() {
		return imgW;
	}
	
	@Override
	public int getHeight() {
		return imgH;
	}
	
	@Override
	public boolean fillsAllPixels() {
		return !hasAlpha();
	}
	
	public static enum TexelFormat {
		RGBA	(GL11.GL_RGBA, 4, true) {
			@Override
			public ByteBuffer transform(TexelFormat targetFormat, ByteBuffer nativeTexelData) {
				ByteBuffer resultBuf = null;
				if (targetFormat == RGBA) {
					resultBuf = nativeTexelData;
				} else if (targetFormat == RGB) {
					int texelCount = nativeTexelData.remaining() / componentCount;
					int targetByteSize = texelCount * targetFormat.componentCount;
					resultBuf = BufferUtils.createByteBuffer(targetByteSize);
					for (int i = 0; i < texelCount; i++) {
						resultBuf.put(nativeTexelData.get());
						resultBuf.put(nativeTexelData.get());
						resultBuf.put(nativeTexelData.get());
						nativeTexelData.get();
					}
					resultBuf.rewind();
				}
				return resultBuf;
			}
		},
		RGB		(GL11.GL_RGB, 3, false) {
			@Override
			public ByteBuffer transform(TexelFormat targetFormat, ByteBuffer nativeTexelData) {
				ByteBuffer resultBuf = null;
				if (targetFormat == RGB) {
					resultBuf = nativeTexelData;
				} else if (targetFormat == RGBA) {
					int texelCount = nativeTexelData.remaining() / componentCount;
					int targetByteSize = texelCount * targetFormat.componentCount;
					resultBuf = BufferUtils.createByteBuffer(targetByteSize);
					for (int i = 0; i < texelCount; i++) {
						resultBuf.put(nativeTexelData.get());
						resultBuf.put(nativeTexelData.get());
						resultBuf.put(nativeTexelData.get());
						resultBuf.put((byte) 0xff);
					}
					resultBuf.rewind();
				}
				return resultBuf;
			}
		},
		;
		
		public final int glName;
		public final int componentCount;
		public final boolean hasAlpha;
		
		private TexelFormat(int glName, int componentCount, boolean hasAlpha) {
			this.glName = glName;
			this.componentCount = componentCount;
			this.hasAlpha = hasAlpha;
		}
		
		public abstract ByteBuffer transform(TexelFormat targetFormat, ByteBuffer nativeTexelData);
		
	}
	
}