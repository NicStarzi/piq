package edu.udo.piq.style;

public interface PStyleable<E extends PStyle> {
	
	public void setCustomStyle(E style);
	
	public E getCustomStyle();
	
	public void setInheritedStyle(E style);
	
	public E getInheritedStyle();
	
	public void setStyleID(Object value);
	
	public Object getStyleID();
	
	public default E getStyle() {
		E customStyle = getCustomStyle();
		if (customStyle != null) {
			return customStyle;
		}
		return getInheritedStyle();
	}
	
}