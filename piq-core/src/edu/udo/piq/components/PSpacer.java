package edu.udo.piq.components;

import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PSpacer extends AbstractPComponent {
	
	protected PSize size;
	
	public PSpacer(int width, int height) {
		size = new ImmutablePSize(width, height);
	}

	public PSpacer(PSize size) {
		this(size.getWidth(), size.getHeight());
	}
	
	@Override
	public PSize getPreferredSize() {
		return size;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return size;
	}
	
	@Override
	protected PSize getConstantDefaultPreferredSize() {
		return size;
	}
	
}