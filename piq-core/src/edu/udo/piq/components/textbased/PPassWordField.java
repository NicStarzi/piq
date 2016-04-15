package edu.udo.piq.components.textbased;

import java.util.Arrays;

public class PPassWordField extends PTextField {
	
	public PPassWordField(PTextModel model) {
		super(model);
	}
	
	public PPassWordField(Object defaultModelValue) {
		super(defaultModelValue);
	}
	
	public PPassWordField() {
		super();
	}
	
	public String getPassword() {
		return super.getText();
	}
	
	public String getText() {
		String originalText = super.getText();
		if (originalText.isEmpty()) {
			return originalText;
		}
		char[] stars = new char[originalText.length()];
		Arrays.fill(stars, '*');
		return new String(stars);
	}
}