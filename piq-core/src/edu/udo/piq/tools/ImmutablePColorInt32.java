package edu.udo.piq.tools;

import edu.udo.piq.PColor;

public class ImmutablePColorInt32 extends AbstractPColor implements PColor {
	
	protected final int argb;
	
	public ImmutablePColorInt32(PColor color) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), 255);
	}
	
	public ImmutablePColorInt32(PColor color, int a) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), a);
	}
	
	public ImmutablePColorInt32(int r, int g, int b) {
		this(r, g, b, 255);
	}
	
	public ImmutablePColorInt32(int r, int g, int b, int a) {
		throwExceptionIfValueIllegal255(r);
		throwExceptionIfValueIllegal255(g);
		throwExceptionIfValueIllegal255(b);
		throwExceptionIfValueIllegal255(a);
		
		argb = ImmutablePColorInt32.toInt32(r, g, b, a);
	}
	
	public ImmutablePColorInt32(PColor color, float a) {
		this(color.getRed1(), color.getGreen1(), color.getBlue1(), a);
	}
	
	public ImmutablePColorInt32(float r, float g, float b) {
		this(r, g, b, 1);
	}
	
	public ImmutablePColorInt32(float r, float g, float b, float a) {
		throwExceptionIfValueIllegal1(r);
		throwExceptionIfValueIllegal1(g);
		throwExceptionIfValueIllegal1(b);
		throwExceptionIfValueIllegal1(a);
		
		int ri = Math.round(r * 255);
		int gi = Math.round(g * 255);
		int bi = Math.round(b * 255);
		int ai = Math.round(a * 255);
		argb = ImmutablePColorInt32.toInt32(ri, gi, bi, ai);
	}
	
	@Override
	public int getRed255() {
		return ImmutablePColorInt32.getRedFromInt32(argb);
	}
	
	@Override
	public float getRed1() {
		return getRed255() / 255.0f;
	}
	
	@Override
	public int getGreen255() {
		return ImmutablePColorInt32.getGreenFromInt32(argb);
	}
	
	@Override
	public float getGreen1() {
		return getGreen255() / 255.0f;
	}
	
	@Override
	public int getBlue255() {
		return ImmutablePColorInt32.getBlueFromInt32(argb);
	}
	
	@Override
	public float getBlue1() {
		return getBlue255() / 255.0f;
	}
	
	@Override
	public int getAlpha255() {
		return ImmutablePColorInt32.getAlphaFromInt32(argb);
	}
	
	@Override
	public float getAlpha1() {
		return getAlpha255() / 255.0f;
	}
	
	public static int toInt32(int r, int g, int b, int a) {
		return	((a & 0xFF) << 24)	|
				((r & 0xFF) << 16)	|
				((g & 0xFF) << 8)	|
				((b & 0xFF) << 0);
	}
	
	public static int getRedFromInt32(int argb) {
		return (argb >> 16) & 0xFF;
	}
	
	public static int getGreenFromInt32(int argb) {
		return (argb >> 8) & 0xFF;
	}
	
	public static int getBlueFromInt32(int argb) {
		return (argb >> 0) & 0xFF;
	}
	
	public static int getAlphaFromInt32(int argb) {
		return (argb >> 24) & 0xFF;
	}
	
}