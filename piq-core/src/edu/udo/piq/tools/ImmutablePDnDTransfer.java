package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.dnd.PDnDIndicator;
import edu.udo.piq.dnd.PDnDTransfer;

public class ImmutablePDnDTransfer implements PDnDTransfer {
	
	private final PComponent src;
	private final int x;
	private final int y;
	private final PModel data;
	private final PDnDIndicator indic;
	
	public ImmutablePDnDTransfer(PComponent source, 
			int fromX, int fromY,
			PModel dataModel,
			PDnDIndicator indicator) 
	{
		src = source;
		x = fromX;
		y = fromY;
		data = dataModel;
		indic = indicator;
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
	
	public PModel getData() {
		return data;
	}
	
	public PDnDIndicator getIndicator() {
		return indic;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [source=");
		builder.append(getSource());
		builder.append(", data=");
		builder.append(getData());
		builder.append(", x=");
		builder.append(getDragStartX());
		builder.append(", y=");
		builder.append(getDragStartY());
		builder.append("]");
		return builder.toString();
	}
	
}