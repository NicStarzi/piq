package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.tools.AbstractPPictureModel;

public class DefaultPPictureModel extends AbstractPPictureModel implements PPictureModel {
	
	private Object imgId = null;
	
	public void setValue(Object obj) {
		if (imgId == null ? imgId != obj : !imgId.equals(obj)) {
			imgId = obj;
			fireImageIDChanged();
		}
	}
	
	public Object getValue() {
		return imgId;
	}
	
}