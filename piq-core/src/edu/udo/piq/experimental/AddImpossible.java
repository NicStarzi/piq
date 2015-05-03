package edu.udo.piq.experimental;

public class AddImpossible extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public AddImpossible(PModel model, PModelIndex index, 
			Object content) 
	{
		super("\""+content+"\" can not be added to \""+model+"\" at \""+index+"\".");
	}
}