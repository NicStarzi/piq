package edu.udo.piq;

import java.util.List;

import edu.udo.piq.components.collections.PModelIndex;

public interface PDnDTransfer {
	
	public PComponent getSource();
	
	public int getDragStartX();
	
	public int getDragStartY();
	
	public List<IndexAndContentTuple> getData();
	
	public PComponent getVisibleRepresentation();
	
	public static class IndexAndContentTuple {
		
		private final Object content;
		private final PModelIndex index;
		
		public IndexAndContentTuple(Object content, PModelIndex index) {
			this.content = content;
			this.index = index;
		}
		
		public Object getContent() {
			return content;
		}
		
		public PModelIndex getIndex() {
			return index;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getContent());
			sb.append(" at ");
			sb.append(getIndex());
			return sb.toString();
		}
		
	}
	
}