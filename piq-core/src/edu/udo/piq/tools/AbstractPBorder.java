package edu.udo.piq.tools;

import java.util.Objects;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBorderObs;
import edu.udo.piq.PStyleBorder;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPBorder implements PBorder {
	
	protected final ObserverList<PBorderObs> obsList = PCompUtil.createDefaultObserverList();
	protected PStyleBorder style;
	private Object styleID = getClass();
	
	@Override
	public void setStyle(PStyleBorder style) {
		if (!Objects.equals(getStyle(), style)) {
			this.style = style;
			fireInsetsChangedEvent();
			fireReRenderEvent();
		}
	}
	
	@Override
	public PStyleBorder getStyle() {
		return style;
	}
	
	@Override
	public Object getStyleID() {
		return styleID;
	}
	
	@Override
	public void addObs(PBorderObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PBorderObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireInsetsChangedEvent() {
		obsList.fireEvent(obs -> obs.onInsetsChanged(this));
	}
	
	protected void fireReRenderEvent() {
		obsList.fireEvent(obs -> obs.onReRender(this));
	}
	
}