package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.components.collections.PModel;

public class ImmutablePDnDTransfer implements PDnDTransfer {
	
	private final PComponent src;
	private final int x;
	private final int y;
	private final PModel data;
//	private final List<IndexAndContentTuple> dataList;
	private final PComponent visPres;
	
	public ImmutablePDnDTransfer(PComponent source, 
			int fromX, int fromY,
			PModel dataModel,
//			List<IndexAndContentTuple> data, 
			PComponent visibleRepresentation) 
	{
		src = source;
		x = fromX;
		y = fromY;
		data = dataModel;
//		dataList = data;
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
	
	public PModel getData() {
		return data;
	}
	
//	public List<IndexAndContentTuple> getData() {
//		return dataList;
//	}
	
	public PComponent getVisibleRepresentation() {
		return visPres;
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