package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;

public abstract class AbstractPLayoutObs implements PLayoutObs {
	public void childAdded(PLayout layout, PComponent child, Object constraint) {
	}
	public void childRemoved(PLayout layout, PComponent child, Object constraint) {
	}
	public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
	}
	public void layoutInvalidated(PLayout layout) {
	}
}