package edu.udo.piq.swing;

import edu.udo.piq.util.Throw;

public interface GlyphMap {
	
	public static final Glyph NO_SIZE_GLYPH = new Glyph(0, 0, 0, 0);
	
	public void add(char c, Glyph glyph);
	
	public Glyph getGlyphFor(char character);
	
	public static final class Glyph {
		
		private final int srcX, srcY, srcW, srcH, tx, ty;
		
		public Glyph(int x, int y, int width, int height) {
			this(x, y, width, height, 0, 0);
		}
		
		public Glyph(int x, int y, int width, int height, int translateX, int translateY) {
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
		}
		
		public final int getX() {
			return srcX;
		}
		
		public final int getY() {
			return srcY;
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
	}
	
}