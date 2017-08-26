package edu.udo.piq.components.defaults;

import java.util.function.Function;

import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;

public class DefaultPCellFactory implements PCellFactory {
	
	protected Function<Object, String> encoder;
	
	@Override
	public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
		DefaultPCellComponent cell = new DefaultPCellComponent(encoder);
		cell.setElement(model, index);
		return cell;
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
}