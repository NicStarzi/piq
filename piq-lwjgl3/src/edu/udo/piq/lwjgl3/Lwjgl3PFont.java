package edu.udo.piq.lwjgl3;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class Lwjgl3PFont implements PFontResource {
	
	public void create(String name, double pointSize, Style style) {
		
	}
	
	public void bind() {
	}
	
	public String getName() {
		return "Arial";
	}
	
	public double getPointSize() {
		return 12;
	}
	
	public PSize getSize(String text) {
		return new ImmutablePSize(64, 14);
	}
	
	public Style getStyle() {
		return Style.PLAIN;
	}
	
}