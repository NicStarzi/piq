package edu.udo.piq;

public interface PStyleComponent {
	
	public boolean getFillsAllPixels(PComponent component);
	
	public void render(PRenderer renderer, PComponent component);
	
	public PSize getPreferredSize(PComponent component);
	
	public PStyleBorder getBorderStyle(PComponent component, PBorder border);
	
}