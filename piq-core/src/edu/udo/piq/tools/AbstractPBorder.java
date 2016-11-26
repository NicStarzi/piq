package edu.udo.piq.tools;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBorderObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPBorder implements PBorder {
	
	protected final ObserverList<PBorderObs> obsList = PCompUtil.createDefaultObserverList();
	
	public void addObs(PBorderObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PBorderObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireInsetsChangedEvent() {
		obsList.fireEvent(obs -> obs.onInsetsChanged(this));
	}
	
	protected void fireReRenderEvent() {
		obsList.fireEvent(obs -> obs.onReRender(this));
	}
//extends AbstractPLayoutOwner implements PBorder {
//	
//	public AbstractPBorder() {
//		super();
//		setLayout(new PAnchorLayout(this, 
//				AlignmentX.FILL, AlignmentY.FILL));
//	}
//	
//	public AbstractPBorder(PComponent content) {
//		this();
//		setContent(content);
//	}
//	
//	public PAnchorLayout getLayout() {
//		return (PAnchorLayout) super.getLayout();
//	}
//	
//	public void setContent(PComponent content) {
//		getLayout().setContent(content);
//	}
//	
//	public PComponent getContent() {
//		return getLayout().getContent();
//	}
//	
//	public boolean defaultFillsAllPixels() {
//		PComponent content = getContent();
//		return content != null && isLayoutAlignmentFill() 
//				&& content.defaultFillsAllPixels();
//	}
//	
//	protected boolean isLayoutAlignmentFill() {
//		return getLayout().getAlignmentX() == AlignmentX.FILL 
//				&& getLayout().getAlignmentY() == AlignmentY.FILL;
//	}
	
}