package edu.udo.piq.style;

public interface PStyleable<E> {
	
	public void setCustomStyle(E style);
	
	public E getCustomStyle();
	
	public void setStyleFromSheet(E style);
	
	public E getStyleFromSheet();
	
	public Object getStyleID();
	
	public default E getStyle() {
		E customStyle = getCustomStyle();
		if (customStyle != null) {
			return customStyle;
		}
		return getStyleFromSheet();
	}
	
}