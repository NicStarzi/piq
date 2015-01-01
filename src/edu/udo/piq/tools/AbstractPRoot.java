package edu.udo.piq.tools;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;

public abstract class AbstractPRoot implements PRoot {
	
	protected PLayout layout;
	protected PDesignSheet designSheet = new AbstractPDesignSheet();
	protected PMouse mouse;
	protected PKeyboard keyboard;
	
	private final PComponentObs childObs = new AbstractPComponentObs() {
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
		}
	};
	private final PLayoutObs layoutObs = new AbstractPLayoutObs() {
		public void layoutInvalidated(PLayout layout) {
			needReLayout = true;
		}
		public void childRemoved(PLayout layout, PComponent child, Object constraint) {
			child.removeObs(childObs);
			needReLayout = true;
		}
		public void childAdded(PLayout layout, PComponent child, Object constraint) {
			child.addObs(childObs);
			needReLayout = true;
		}
	};
	private final Set<PTimer> timerSet = new HashSet<>();
	private final List<PComponentObs> compObsList = new CopyOnWriteArrayList<>();
	private final List<PFocusObs> focusObsList = new CopyOnWriteArrayList<>();
	private PComponent focusOwner;
	private String id;
	private boolean needReLayout = true;
	
	public AbstractPRoot() {
		setLayout(new PBorderLayout(this));
	}
	
	/*
	 * Updates
	 */
	
	public void update() {
		tickAllTimers();
		updateRootLayout();
		updateComponents();
	}
	
	protected void updateRootLayout() {
		if (needReLayout) {
			getLayout().layOut();
			needReLayout = false;
		}
	}
	
	protected void tickAllTimers() {
		for (PTimer timer : timerSet) {
			timer.tick();
		}
	}
	
	protected void updateComponents() {
		Deque<PComponent> stack = new LinkedList<>();
		stack.addAll(getLayout().getChildren());
		while (!stack.isEmpty()) {
			PComponent comp = stack.pop();
			comp.update();
			
			PLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					stack.addFirst(child);
				}
			}
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
		if (oldOwner != null) {
			fireFocusLostEvent(oldOwner);
		}
		focusOwner = component;
		if (getFocusOwner() != null) {
			fireFocusGainedEvent(oldOwner);
		}
	}
	
	public void setLayout(PLayout layout) {
		if (getLayout() != null) {
			getLayout().removeObs(layoutObs);
		}
		this.layout = layout;
		if (getLayout() != null) {
			getLayout().addObs(layoutObs);
		}
		needReLayout = true;
		reRender(this);
	}
	
	public PLayout getLayout() {
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
	
	/*
	 * Timers
	 */
	
	public void registerTimer(PTimer timer) throws NullPointerException {
		if (timer == null) {
			throw new NullPointerException("timer="+timer);
		}
		if (!timerSet.add(timer)) {
			throw new IllegalArgumentException(timer+" was already registered.");
		}
	}
	
	public void unregisterTimer(PTimer timer) throws NullPointerException {
		if (timer == null) {
			throw new NullPointerException("timer="+timer);
		}
		if (!timerSet.remove(timer)) {
			throw new IllegalArgumentException(timer+" was not registered.");
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
	
	protected void fireSizeChanged() {
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
	
	public PComponent getParent() {
		return null;
	}
	
	public PSize getDefaultPreferredSize() {
		return getBounds();
	}
	
	public boolean isDefaultOpaque() {
		return true;
	}
	
	public boolean isFocusable() {
		return false;
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
		builder.append(PCompUtil.getBoundsOf(this));
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