package edu.udo.piq.components;

import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;

public class PSpacer extends AbstractPComponent {
	
	public PSpacer(int width, int height) {
		prefSize.set(width, height);
	}

	public PSpacer(PSize size) {
		this(size.getWidth(), size.getHeight());
	}
	
	@Override
	public PSize getPreferredSize() {
		return prefSize;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return prefSize;
	}
	
	@Override
	protected PSize getNoLayoutDefaultPreferredSize() {
		return prefSize;
	}
	
}