package edu.udo.piq.components.collections;

public class WrongIndexType extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public WrongIndexType(PModelIndex index, String expectedType) {
		super("\""+index+"\" is not of type \""+expectedType+"\".");
	}
	
	public WrongIndexType(PModelIndex index, Class<? extends PModelIndex> expectedIndexClass) {
		super("\""+index+"\" is not of type \""+expectedIndexClass.getName()+"\".");
	}
	
}