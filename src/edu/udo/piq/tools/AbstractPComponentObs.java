package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PRoot;

public abstract class AbstractPComponentObs implements PComponentObs {
	public void rootChanged(PComponent component, PRoot currentRoot) {
	}
	public void preferredSizeChanged(PComponent component) {
	}
	public void wasAdded(PComponent component) {
	}
	public void wasRemoved(PComponent component) {
	}
}