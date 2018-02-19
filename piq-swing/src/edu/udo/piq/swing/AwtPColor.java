package edu.udo.piq.swing;

import java.awt.Color;

import edu.udo.piq.PColor;
import edu.udo.piq.tools.AbstractPColor;
import edu.udo.piq.util.Throw;

public class AwtPColor extends AbstractPColor implements PColor {
	
	protected final Color awtColor;
	
	public AwtPColor(Color awtColor) {
		Throw.ifNull(awtColor, "awtColor == null");
		this.awtColor = awtColor;
	}
	
	@Override
	public int getRed255() {
		return awtColor.getRed();
	}
	
	@Override
	public float getRed1() {
		return getRed255() / 255f;
	}
	
	@Override
	public int getGreen255() {
		return awtColor.getGreen();
	}
	
	@Override
	public float getGreen1() {
		return getGreen255() / 255f;
	}
	
	@Override
	public int getBlue255() {
		return awtColor.getBlue();
	}
	
	@Override
	public float getBlue1() {
		return getBlue255() / 255f;
	}
	
	@Override
	public int getAlpha255() {
		return awtColor.getAlpha();
	}
	
	@Override
	public float getAlpha1() {
		return getAlpha255() / 255f;
	}
	
}