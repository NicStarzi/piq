package edu.udo.piq.designs.standard;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PRoot;

public abstract class PStandardDesign implements PCompFilter, PDesign {
	
	private final PStandardDesignSheet sheet;
	
	public PStandardDesign(PStandardDesignSheet designSheet) {
		sheet = designSheet;
	}
	
	public PStandardDesignSheet getDesignSheet() {
		return sheet;
	}
	
	public void setReRenderNeeded() {
		getDesignSheet().addReRenderFilter(this);
	}
	
	public void setReLayoutNeeded() {
		getDesignSheet().addReLayoutFilter(this);
	}
	
	public void refreshAsNeeded(PComponent from) {
		PRoot root = from.getRoot();
		getDesignSheet().reRenderAll(root);
		getDesignSheet().reLayOutAll(root);
	}
	
}