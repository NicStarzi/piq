package edu.udo.piq.lwjgl3;

import edu.udo.piq.util.Throw;

public interface GlyphMap {
	
	public static final Glyph NO_SIZE_GLYPH = new Glyph(0, 0, 0, 0);
	
	public default void addRange(String letters, int firstX, int srcY, int glyphW, int glyphH) {
		for (int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			add(c, firstX + i * glyphW, srcY, glyphW, glyphH);
		}
	}
	
	public default void add(char c, int srcX, int srcY, int srcWidth, int srcHeight) {
		add(c, new Glyph(srcX, srcY, srcWidth, srcHeight));
	}
	
	public default void add(char c, int srcX, int srcY, int srcWidth, int srcHeight, int advance) {
		add(c, new Glyph(srcX, srcY, srcWidth, srcHeight, advance));
	}
	
	public default void add(char c, int srcX, int srcY, int srcWidth, int srcHeight, int translateX, int translateY) {
		add(c, new Glyph(srcX, srcY, srcWidth, srcHeight, translateX, translateY));
	}
	
	public default void add(char c, int srcX, int srcY, int srcWidth, int srcHeight, int translateX, int translateY, int advance) {
		add(c, new Glyph(srcX, srcY, srcWidth, srcHeight, translateX, translateY, advance));
	}
	
	public void add(char c, Glyph glyph);
	
	public Glyph getGlyphFor(char character);
	
	public static final class Glyph {
		
		private final int srcX, srcY, srcW, srcH, tx, ty, adv;
		
		public Glyph(int x, int y, int width, int height) {
			this(x, y, width, height, 0, 0, 0);
		}
		
		public Glyph(int x, int y, int width, int height, int advance) {
			this(x, y, width, height, 0, 0, advance);
		}
		
		public Glyph(int x, int y, int width, int height, int translateX, int translateY) {
			this(x, y, width, height, translateX, translateY, 0);
		}
		
		public Glyph(int x, int y, int width, int height, int translateX, int translateY, int advance) {
			Throw.ifLess(0, x, () -> "x < 0");
			Throw.ifLess(0, y, () -> "y < 0");
			Throw.ifLess(0, width, () -> "width < 0");
			Throw.ifLess(0, height, () -> "height < 0");
			srcX = x;
			srcY = y;
			srcW = width;
			srcH = height;
			tx = translateX;
			ty = translateY;
			adv = advance;
		}
		
		public final int getX() {
			return srcX;
		}
		
		public final int getY() {
			return srcY;
		}
		
		public final int getFx() {
			return srcX + srcW;
		}
		
		public final int getFy() {
			return srcY + srcH;
		}
		
		public final int getWidth() {
			return srcW;
		}
		
		public final int getHeight() {
			return srcH;
		}
		
		public final int getTranslateX() {
			return tx;
		}
		
		public final int getTranslateY() {
			return ty;
		}
		
		public int getAdvance() {
			return adv;
		}
		
		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + srcH;
			result = prime * result + srcW;
			result = prime * result + srcX;
			result = prime * result + srcY;
			return result;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Glyph)) {
				return false;
			}
			Glyph other = (Glyph) obj;
			return srcX == other.srcX && srcY == other.srcY && srcW == other.srcW && srcH == other.srcH;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Glyph [srcX=");
			builder.append(srcX);
			builder.append(", srcY=");
			builder.append(srcY);
			builder.append(", srcW=");
			builder.append(srcW);
			builder.append(", srcH=");
			builder.append(srcH);
			builder.append(", tx=");
			builder.append(tx);
			builder.append(", ty=");
			builder.append(ty);
			builder.append(", adv=");
			builder.append(adv);
			builder.append("]");
			return builder.toString();
		}
	}
	
}