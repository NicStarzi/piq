package edu.udo.piq.components.util;

import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.util.ThrowException;

public class StandardFontKey {
	
	protected final String name;
	protected final double size;
	protected final Style style;
	
	public StandardFontKey(String fontName, double pixelSize, Style fontStyle) {
		ThrowException.ifNull(fontName, "fontName == null");
		ThrowException.ifNull(fontStyle, "fontStyle == null");
		ThrowException.ifLess(1, pixelSize, "pixelSize < 1");
		name = fontName;
		size = pixelSize;
		style = fontStyle;
	}
	
	public String getName() {
		return name;
	}
	
	public double getPixelSize() {
		return size;
	}
	
	public Style getStyle() {
		return style;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + style.ordinal();
		result = prime * result + name.hashCode();
		result = prime * result + Double.hashCode(size);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StandardFontKey)) {
			return false;
		}
		StandardFontKey other = (StandardFontKey) obj;
		return name.equals(other.name) && size == other.size && style == other.style;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StandardFontKey [name=");
		builder.append(name);
		builder.append(", size=");
		builder.append(size);
		builder.append(", style=");
		builder.append(style);
		builder.append("]");
		return builder.toString();
	}
}