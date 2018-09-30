package edu.udo.piq;

public interface PRenderSubRoot extends PComponent {
	
	public void renderThisAndChildren(PRenderer renderer, int clipX, int clipY, int clipW, int clipH);
	
}