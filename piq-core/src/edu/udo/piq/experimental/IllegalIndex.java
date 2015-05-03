package edu.udo.piq.experimental;

public class IllegalIndex extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public IllegalIndex(PModelIndex givenIndex) {
		super("Index \""+givenIndex+"\" is illegal.");
	}
	
}