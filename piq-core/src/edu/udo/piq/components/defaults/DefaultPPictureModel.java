package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.tools.AbstractPPictureModel;

public class DefaultPPictureModel extends AbstractPPictureModel implements PPictureModel {
	
	private Object imgKey = null;
	
	public DefaultPPictureModel() {}
	
	public DefaultPPictureModel(Object initialValue) {
		this();
		setValue(initialValue);
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		imgKey = newValue;
	}
	
	@Override
	public Object getValue() {
		return imgKey;
	}
	
}