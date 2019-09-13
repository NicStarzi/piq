package edu.udo.piq.swing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.swing.GlyphMap.Glyph;
import edu.udo.piq.swing.util.SwingImageUtil;
import edu.udo.piq.util.Throw;

public class AwtImagePFontBuilder {
	
	public static final int ARRAY_BACKED_GLYPH_MAP_SIZE_THRESHOLD = 128;
	
	private final Set<GlyphEntry> glyphs = new HashSet<>();
	private char minChar, maxChar;
	/** always use array based glyph map: true := yes */
	private boolean useArr = false;
	private BufferedImage glyphImg;
	private String fontName;
	private Style fontStyle;
	private int pixSize;
	
	public AwtImagePFontBuilder fontName(String value) {
		Throw.ifNull(value, "value == null");
		fontName = value;
		return this;
	}
	
	public AwtImagePFontBuilder fontStyle(Style value) {
		Throw.ifNull(value, "value == null");
		fontStyle = value;
		return this;
	}
	
	public AwtImagePFontBuilder glyphImage(InputStream imageInputStream) throws IOException {
		return glyphImage(ImageIO.read(imageInputStream));
	}
	
	public AwtImagePFontBuilder glyphImage(ImageInputStream imageInputStream) throws IOException {
		return glyphImage(ImageIO.read(imageInputStream));
	}
	
	public AwtImagePFontBuilder glyphImage(URL imageUrl) throws IOException {
		return glyphImage(ImageIO.read(imageUrl));
	}
	
	public AwtImagePFontBuilder glyphImage(String imageFilePath) throws IOException {
		return glyphImage(new File(imageFilePath));
	}
	
	public AwtImagePFontBuilder glyphImage(File imageFile) throws IOException {
		return glyphImage(ImageIO.read(imageFile));
	}
	
	public AwtImagePFontBuilder glyphImage(BufferedImage value) {
		Throw.ifNull(value, "value == null");
		glyphImg = SwingImageUtil.createAcceleratedImgCopy(value);
		return this;
	}
	
	public AwtImagePFontBuilder pixelSize(int value) {
		Throw.ifLess(1, value, () -> "value == " + (value) + " < " + (1));
		pixSize = value;
		return this;
	}
	
	public AwtImagePFontBuilder alwaysUseArrayBasedGlyphMap() {
		useArr = true;
		return this;
	}
	
	public AwtImagePFontBuilder addGlyph(
			char character,
			int srcX, int srcY,
			int srcWidth, int srcHeight)
	{
		return addGlyph(character, srcX, srcY, srcWidth, srcHeight, 0, 0);
	}
	
	public AwtImagePFontBuilder addGlyph(
			char character,
			int srcX, int srcY,
			int srcWidth, int srcHeight,
			int translateX, int translateY)
	{
		GlyphEntry entry = new GlyphEntry();
		entry.character = character;
		entry.glyph = new Glyph(srcX, srcY, srcWidth, srcHeight, translateX, translateY);
		if (!glyphs.add(entry)) {
			throw new IllegalArgumentException("Glyph for character '"+character+"' was already defined.");
		}
		if (character < minChar) {
			minChar = character;
		}
		if (character > maxChar) {
			maxChar = character;
		}
		return this;
	}
	
	public AwtImageFont build() {
		Throw.ifNull(fontName, "fontName == null");
		Throw.ifNull(fontStyle, "fontStyle == null");
		Throw.ifNull(glyphImg, "glyphImage == null");
		Throw.ifLess(1, pixSize, () -> "pixelSize == " + (pixSize) + " < " + (1));
		Throw.ifLess(1, glyphs.size(), () -> "glyphs.size() == " + (glyphs.size()) + " < " + (1));
		
		GlyphMap glyphMap;
		int glyphCount = maxChar - minChar + 1;
		if (useArr || glyphCount <= ARRAY_BACKED_GLYPH_MAP_SIZE_THRESHOLD) {
			glyphMap = new ArrayBasedGlyphMap(minChar, maxChar);
		} else {
			glyphMap = new HashBasedGlyphMap(glyphCount);
		}
		for (GlyphEntry entry : glyphs) {
			glyphMap.add(entry.character, entry.glyph);
		}
		return new AwtImageFont(fontName, fontStyle, pixSize, glyphImg, glyphMap);
	}
	
	private static class GlyphEntry {
		char character;
		Glyph glyph;
		@Override
		public int hashCode() {
			return character;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GlyphEntry)) {
				return false;
			}
			GlyphEntry other = (GlyphEntry) obj;
			return character == other.character;
		}
		
	}
	
}