package edu.udo.piq.components.containers;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPContainer;
import edu.udo.piq.tools.ImmutablePSize;

public class PPanel extends AbstractPContainer {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(20, 20);
	
	public PPanel() {
		super();
	}
	
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBounds());
	}
	
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return DEFAULT_PREFERRED_SIZE;
	}
}