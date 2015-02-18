package edu.udo.piq.tools;

import edu.udo.piq.PColor;

public class ImmutablePColor extends AbstractPColor implements PColor {
	
	protected final int r255;
	protected final int g255;
	protected final int b255;
	protected final int a255;
	protected final double r1;
	protected final double g1;
	protected final double b1;
	protected final double a1;
	
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
		
		r1 = r / 255.0;
		g1 = g / 255.0;
		b1 = b / 255.0;
		a1 = a / 255.0;
	}
	
	public ImmutablePColor(double r, double g, double b) {
		this(r, g, b, 1);
	}
	
	public ImmutablePColor(double r, double g, double b, double a) {
		throwExceptionIfValueIllegal1(r);
		throwExceptionIfValueIllegal1(g);
		throwExceptionIfValueIllegal1(b);
		throwExceptionIfValueIllegal1(a);
		
		r1 = r;
		g1 = g;
		b1 = b;
		a1 = a;
		
		r255 = (int) Math.round(r * 255);
		g255 = (int) Math.round(g * 255);
		b255 = (int) Math.round(b * 255);
		a255 = (int) Math.round(a * 255);
	}
	
	public int getRed255() {
		return r255;
	}
	
	public double getRed1() {
		return r1;
	}
	
	public int getGreen255() {
		return g255;
	}
	
	public double getGreen1() {
		return g1;
	}
	
	public int getBlue255() {
		return b255;
	}
	
	public double getBlue1() {
		return b1;
	}
	
	public int getAlpha255() {
		return a255;
	}
	
	public double getAlpha1() {
		return a1;
	}
	
}