package edu.udo.piq.tools;

import edu.udo.piq.PColor;

public class ImmutablePColor extends AbstractPColor implements PColor {
	
	protected final int r255;
	protected final int g255;
	protected final int b255;
	protected final int a255;
	protected final float r1;
	protected final float g1;
	protected final float b1;
	protected final float a1;
	
	public ImmutablePColor(PColor color) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), 255);
	}
	
	public ImmutablePColor(PColor color, int a) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), a);
	}
	
	public ImmutablePColor(int r, int g, int b) {
		this(r, g, b, 255);
	}
	
	public ImmutablePColor(int r, int g, int b, int a) {
		throwExceptionIfValueIllegal255(r);
		throwExceptionIfValueIllegal255(g);
		throwExceptionIfValueIllegal255(b);
		throwExceptionIfValueIllegal255(a);
		
		r255 = r;
		g255 = g;
		b255 = b;
		a255 = a;
		
		r1 = r / 255.0f;
		g1 = g / 255.0f;
		b1 = b / 255.0f;
		a1 = a / 255.0f;
	}
	
	public ImmutablePColor(PColor color, float a) {
		this(color.getRed1(), color.getGreen1(), color.getBlue1(), a);
	}
	
	public ImmutablePColor(double r, double g, double b) {
		this((float) r, (float) g, (float) b);
	}
	
	public ImmutablePColor(float r, float g, float b) {
		this(r, g, b, 1);
	}
	
	public ImmutablePColor(double r, double g, double b, double a) {
		this((float) r, (float) g, (float) b, (float) a);
	}
	
	public ImmutablePColor(float r, float g, float b, float a) {
		throwExceptionIfValueIllegal1(r);
		throwExceptionIfValueIllegal1(g);
		throwExceptionIfValueIllegal1(b);
		throwExceptionIfValueIllegal1(a);
		
		r1 = r;
		g1 = g;
		b1 = b;
		a1 = a;
		
		r255 = Math.round(r * 255);
		g255 = Math.round(g * 255);
		b255 = Math.round(b * 255);
		a255 = Math.round(a * 255);
	}
	
	@Override
	public int getRed255() {
		return r255;
	}
	
	@Override
	public float getRed1() {
		return r1;
	}
	
	@Override
	public int getGreen255() {
		return g255;
	}
	
	@Override
	public float getGreen1() {
		return g1;
	}
	
	@Override
	public int getBlue255() {
		return b255;
	}
	
	@Override
	public float getBlue1() {
		return b1;
	}
	
	@Override
	public int getAlpha255() {
		return a255;
	}
	
	@Override
	public float getAlpha1() {
		return a1;
	}
	
}