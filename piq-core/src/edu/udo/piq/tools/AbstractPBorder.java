package edu.udo.piq.tools;

import edu.udo.piq.PBorder;
import edu.udo.piq.PComponent;
import edu.udo.piq.layouts.PCentricLayout;

public abstract class AbstractPBorder extends AbstractPLayoutOwner implements PBorder {
	
	public AbstractPBorder() {
		super();
		setLayout(new PCentricLayout(this));
	}
	
	public AbstractPBorder(PComponent content) {
		this();
		setContent(content);
	}
	
	public PCentricLayout getLayout() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void setContent(PComponent content) {
		if (content == null) {
			getLayout().removeChild(content);
		} else {
			getLayout().addChild(content, null);
		}
	}
	
	public PComponent getContent() {
		return getLayout().getContent();
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}