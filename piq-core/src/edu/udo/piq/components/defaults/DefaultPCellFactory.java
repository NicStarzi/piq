package edu.udo.piq.components.defaults;

import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.util.ObjToStr;

public class DefaultPCellFactory implements PCellFactory {
	
	protected ObjToStr encoder;
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
		DefaultPCellComponent cell = new DefaultPCellComponent(encoder);
		cell.setElement(model, index);
		return cell;
	}
	
	public void setOutputEncoder(ObjToStr outputEncoder) {
		encoder = outputEncoder;
	}
	
	public ObjToStr getOutputEncoder() {
		return encoder;
	}
	
}