package edu.udo.piq.tools;

import edu.udo.piq.PColor;

public class MutablePColor extends AbstractPColor implements PColor {
	
	protected int r255;
	protected int g255;
	protected int b255;
	protected int a255;
	protected float r1;
	protected float g1;
	protected float b1;
	protected float a1;
	
	public MutablePColor(PColor color) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), 255);
	}
	
	public MutablePColor(PColor color, int a) {
		this(color.getRed255(), color.getGreen255(), color.getBlue255(), a);
	}
	
	public MutablePColor(int r, int g, int b) {
		this(r, g, b, 255);
	}
	
	public MutablePColor(int r, int g, int b, int a) {
		set255(r, g, b, a);
	}
	
	public MutablePColor(PColor color, float a) {
		this(color.getRed1(), color.getGreen1(), color.getBlue1(), a);
	}
	
	public MutablePColor(float r, float g, float b) {
		this(r, g, b, 1);
	}
	
	public MutablePColor(float r, float g, float b, float a) {
		set1(r, g, b, a);
	}
	
	public void set255(int r, int g, int b, int a) {
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
	
	public void set(PColor other) {
		set255(other.getRed255(), other.getGreen255(), other.getBlue255(), other.getAlpha255());
	}
	
	public void set1(float r, float g, float b, float a) {
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
	
	public void setRed255(int value) {
		throwExceptionIfValueIllegal255(value);
		r255 = value;
		r1 = r255 / 255.0f;
	}
	
	public void setGreen255(int value) {
		throwExceptionIfValueIllegal255(value);
		g255 = value;
		g1 = g255 / 255.0f;
	}
	
	public void setBlue255(int value) {
		throwExceptionIfValueIllegal255(value);
		b255 = value;
		b1 = b255 / 255.0f;
	}
	
	public void setAlpha255(int value) {
		throwExceptionIfValueIllegal255(value);
		a255 = value;
		a1 = a255 / 255.0f;
	}
	
	public void setRed1(float value) {
		throwExceptionIfValueIllegal1(value);
		r1 = value;
		r255 = Math.round(r1 * 255);
	}
	
	public void setGreen1(float value) {
		throwExceptionIfValueIllegal1(value);
		g1 = value;
		g255 = Math.round(g1 * 255);
	}
	
	public void setBlue1(float value) {
		throwExceptionIfValueIllegal1(value);
		b1 = value;
		b255 = Math.round(b1 * 255);
	}
	
	public void setAlpha1(float value) {
		throwExceptionIfValueIllegal1(value);
		a1 = value;
		a255 = Math.round(a1 * 255);
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