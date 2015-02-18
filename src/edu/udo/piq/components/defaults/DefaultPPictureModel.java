package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.tools.AbstractPPictureModel;

public class DefaultPPictureModel extends AbstractPPictureModel implements PPictureModel {
	
	private String imgPath = null;
	
	public void setImagePath(String path) {
		imgPath = path;
		fireImagePathChanged();
	}
	
	public String getImagePath() {
		return imgPath;
	}
	
}