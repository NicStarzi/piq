package edu.udo.piq.components;

public interface PPictureModel {
	
	public default void setValue(Object obj) {
		if (obj instanceof String) {
			setImagePath((String) obj);
		}
	}
	
	public default Object getValue() {
		return getImagePath();
	}
	
	public void setImagePath(String path);
	
	public String getImagePath();
	
	public void addObs(PPictureModelObs obs);
	
	public void removeObs(PPictureModelObs obs);
	
}