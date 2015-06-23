package edu.udo.piq.comps.selectcomps;

public class RemoveImpossible extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public RemoveImpossible(PModel model, PModelIndex index) {
		super("\""+index+"\" can not be removed from \""+model+"\".");
	}
}