package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDTransfer;

public class ImmutablePDnDTransfer implements PDnDTransfer {
	
	private final PComponent src;
	private final int x;
	private final int y;
	private final Object obj;
	private final PComponent visPres;
	
	public ImmutablePDnDTransfer(PComponent source, int fromX, int fromY, Object data, PComponent visibleRepresentation) {
		src = source;
		x = fromX;
		y = fromY;
		obj = data;
		visPres = visibleRepresentation;
	}
	
	public PComponent getSource() {
		return src;
	}
	
	public int getDragStartX() {
		return x;
	}
	
	public int getDragStartY() {
		return y;
	}
	
	public Object getElement() {
		return obj;
	}
	
	public PComponent getVisibleRepresentation() {
		return visPres;
	}
	
}