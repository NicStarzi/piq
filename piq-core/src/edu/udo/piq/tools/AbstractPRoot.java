package edu.udo.piq.tools;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.PGlassPanel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PRootLayout;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;

public abstract class AbstractPRoot implements PRoot {
	
	protected final PRootLayout layout;
	protected PDesignSheet designSheet = new AbstractPDesignSheet();
	protected PMouse mouse;
	protected PKeyboard keyboard;
	protected PClipboard clipboard;
	protected PDnDManager dndManager;
	
//	private final PComponentObs childObs = new PComponentObs() {
//		public void preferredSizeChanged(PComponent component) {
//			needReLayout = true;
//			reLayOut(AbstractPRoot.this);
//		}
//	};
	private final PLayoutObs layoutObs = new PLayoutObs() {
		public void layoutInvalidated(PReadOnlyLayout layout) {
			needReLayout = true;
			reLayOut(AbstractPRoot.this);
		}
		public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
//			child.removeObs(childObs);
			needReLayout = true;
			reLayOut(AbstractPRoot.this);
		}
		public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
//			child.addObs(childObs);
			needReLayout = true;
			reLayOut(AbstractPRoot.this);
		}
	};
	private final Comparator<PComponent> componentComparator = new Comparator<PComponent>() {
		public int compare(PComponent o1, PComponent o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			int depth1 = o1.getDepth();
			int depth2 = o2.getDepth();
			if (depth1 == depth2) {
				return -1;
			}
			return depth1 - depth2;
		}
	};
	private final Set<PTimer> timerSet = new HashSet<>();
	private final Set<PTimer> timersToAdd = new HashSet<>();
	private final Set<PTimer> timersToRemove = new HashSet<>();
	private Set<PComponent> reLayOutCompsFront = new TreeSet<>(componentComparator);
	private Set<PComponent> reLayOutCompsBack = new TreeSet<>(componentComparator);
	private final List<PComponentObs> compObsList = new CopyOnWriteArrayList<>();
	private final List<PFocusObs> focusObsList = new CopyOnWriteArrayList<>();
	private PComponent focusOwner;
	private String id;
	private boolean needReLayout = true;
	private boolean timerIterationInProgress;
	
	public AbstractPRoot() {
		layout = new PRootLayout(this);
		getLayout().addObs(layoutObs);
		PPanel body = new PPanel();
		body.setLayout(new PBorderLayout(body));
		getLayout().addChild(body, Constraint.BODY);
		getLayout().addChild(new PGlassPanel(), Constraint.OVERLAY);
	}
	
	/*
	 * Updates
	 */
	
	public void update() {
		tickAllTimers();
		reLayOutAll();
	}
	
	public void reLayOut() {
		if (needReLayout) {
			getLayout().layOut();
			needReLayout = false;
		}
	}
	
	public void reLayOut(PComponent component) {
//		System.out.println("reLayOut "+component);
		reLayOutCompsFront.add(component);
	}
	
	protected void tickAllTimers() {
		timerIterationInProgress = true;
		for (PTimer timer : timerSet) {
			timer.tick();
		}
		timerIterationInProgress = false;
		timerSet.removeAll(timersToRemove);
		timersToRemove.clear();
		timerSet.addAll(timersToAdd);
		timersToAdd.clear();
	}
	
	protected void reLayOutAll() {
		if (!reLayOutCompsFront.isEmpty()) {
//			System.out.println();
//			System.out.println("reLayOutAll");
			
			Set<PComponent> temp = reLayOutCompsBack;
			reLayOutCompsBack = reLayOutCompsFront;
			reLayOutCompsFront = temp;
			for (PComponent comp : reLayOutCompsBack) {
//				System.out.println("LayOut="+comp);
				comp.reLayOut();
			}
			reLayOutCompsBack.clear();
//			System.out.println("##############################");
//			System.out.println();
		}
	}
	
	/*
	 * Focus, Layout and Input
	 */
	
	public PComponent getFocusOwner() {
		return focusOwner;
	}
	
	public void setFocusOwner(PComponent component) {
		PComponent oldOwner = getFocusOwner();
		if (component == oldOwner) {
			return;
		}
		if (oldOwner != null) {
			fireFocusLostEvent(oldOwner);
		}
		focusOwner = component;
		if (getFocusOwner() != null) {
			fireFocusGainedEvent(oldOwner);
		}
	}
	
	public PRootLayout getLayout() {
		return layout;
	}
	
	protected void setDesignSheet(PDesignSheet designSheet) {
		this.designSheet = designSheet;
		needReLayout = true;
		reRender(this);
	}
	
	public PDesignSheet getDesignSheet() {
		return designSheet;
	}
	
	public PMouse getMouse() {
		return mouse;
	}
	
	public PKeyboard getKeyboard() {
		return keyboard;
	}
	
	public PClipboard getClipboard() {
		return clipboard;
	}
	
	public PRootOverlay getOverlay() {
		return getLayout().getOverlay();
	}
	
	public void setBody(PComponent component) {
		if (getBody() != null) {
			getLayout().removeChild(Constraint.BODY);
		}
		if (component != null) {
			getLayout().addChild(component, Constraint.BODY);
		}
	}
	
	public PComponent getBody() {
		return getLayout().getBody();
	}
	
	public PDnDManager getDragAndDropManager() {
		return dndManager;
	}
	
	/*
	 * Timers
	 */
	
	public void registerTimer(PTimer timer) throws NullPointerException {
		if (timer == null) {
			throw new NullPointerException("timer="+timer);
		}
		if (timerIterationInProgress) {
			timersToAdd.add(timer);
		} else {
			timerSet.add(timer);
		}
	}
	
	public void unregisterTimer(PTimer timer) throws NullPointerException {
		if (timer == null) {
			throw new NullPointerException("timer="+timer);
		}
		if (timerIterationInProgress) {
			timersToRemove.add(timer);
		} else {
			timerSet.remove(timer);
		}
	}
	
	/*
	 * Observers and Events
	 */
	
	public void addObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		compObsList.add(obs);
	}
	
	public void removeObs(PComponentObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		compObsList.remove(obs);
	}
	
	public void addObs(PFocusObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		focusObsList.add(obs);
	}
	
	public void removeObs(PFocusObs obs) throws NullPointerException {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		focusObsList.remove(obs);
	}
	
	public void addObs(PMouseObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		if (getMouse() != null) {
			getMouse().addObs(obs);
		}
	}
	
	public void removeObs(PMouseObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		if (getMouse() != null) {
			getMouse().removeObs(obs);
		}
	}
	
	public void addObs(PKeyboardObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		if (getKeyboard() != null) {
			getKeyboard().addObs(obs);
		}
	}
	
	public void removeObs(PKeyboardObs obs) {
		if (obs == null) {
			throw new NullPointerException("obs="+obs);
		}
		if (getKeyboard() != null) {
			getKeyboard().removeObs(obs);
		}
	}
	
	protected void fireSizeChanged() {
		reRender(this);
		needReLayout = true;
		reLayOut(this);
		for (PComponentObs obs : compObsList) {
			obs.preferredSizeChanged(this);
		}
	}
	
	protected void fireFocusGainedEvent(PComponent oldFocusOwner) {
		for (PFocusObs obs : focusObsList) {
			obs.focusGained(oldFocusOwner, getFocusOwner());
		}
	}
	
	protected void fireFocusLostEvent(PComponent oldFocusOwner) {
		for (PFocusObs obs : focusObsList) {
			obs.focusLost(oldFocusOwner);
		}
	}
	
	/*
	 * Uninteresting methods from component
	 */
	
	public PRoot getRoot() {
		return this;
	}
	
	public int getDepth() {
		return 0;
	}
	
	public PComponent getParent() {
		return null;
	}
	
	public PSize getDefaultPreferredSize() {
		return getBounds();
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
	/**
	 * Always returns null by default.<br>
	 */
	public PDnDSupport getDragAndDropSupport() {
		return null;
	}
	
	public void setID(String value) {
		id = value;
	}
	
	public String getID() {
		return id;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (id == null) {
			builder.append(getClass().getSimpleName());
		} else {
			builder.append(getID());
		}
		builder.append(" [bounds=");
		builder.append(getBounds());
		builder.append("]");
		return builder.toString();
	}
	
	/*
	 * Unsupported inherited methods
	 */
	
	public void setDesign(PDesign design) {
		throw new UnsupportedOperationException("PRoot");
	}
	
	public PDesign getDesign() {
		throw new UnsupportedOperationException("PRoot");
	}
	
	public void setParent(PComponent parent) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PRoot");
	}
	
	public void defaultRender(PRenderer renderer) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PRoot");
	}
	
	/*
	 * Utility class
	 */
	
	
	protected static class FontInfo {
		protected final String name;
		protected final int size;
		protected final Style style;
		
		public FontInfo(String fontName, int pointSize, Style fontStyle) {
			if (fontName == null || fontStyle == null) {
				throw new NullPointerException();
			}
			name = fontName;
			size = pointSize;
			style = fontStyle;
		}
		
		public String getName() {
			return name;
		}
		
		public int getSize() {
			return size;
		}
		
		public Style getStyle() {
			return style;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + style.ordinal();
			result = prime * result + name.hashCode();
			result = prime * result + size;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || !(obj instanceof FontInfo)) {
				return false;
			}
			FontInfo other = (FontInfo) obj;
			return name.equals(other.name) 
					&& size == other.size 
					&& style == other.style;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("FontInfo [name=");
			builder.append(name);
			builder.append(", size=");
			builder.append(size);
			builder.append(", style=");
			builder.append(style);
			builder.append("]");
			return builder.toString();
		}
	}
	
}