package edu.udo.piq.tools;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PGlobalEventObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootObs;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.PStyleSheet;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.containers.DefaultPRootOverlay;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.util.DefaultPFocusTraversal;
import edu.udo.piq.components.util.PFocusTraversal;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PRootLayout;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPRoot implements PRoot {
	
	public static final int DEFAULT_MAX_LAYOUT_ITERATION_COUNT = 100;
	protected static final Comparator<PComponent> COMPONENT_COMPARATOR = (o1, o2) -> {
		if (o1.equals(o2)) {
			return 0;
		}
		int depth1 = o1.getDepth();
		int depth2 = o2.getDepth();
		if (depth1 == depth2) {
			return -1;
		}
		return depth1 - depth2;
	};
	
	protected final ObserverList<PGlobalEventObs> globalObsList
			= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PRootObs> rootObsList
		= PCompUtil.createDefaultObserverList();
	protected final PRootLayout layout;
	protected PStyleSheet styleSheet = new AbstractPStyleSheet();
	protected PMouse mouse;
	protected PKeyboard keyboard;
	protected PClipboard clipboard;
	protected PDnDManager dndManager;
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		@Override
		public void onLayoutInvalidated(PReadOnlyLayout layout) {
			reLayOut(AbstractPRoot.this);
		}
		@Override
		public void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
			reLayOut(AbstractPRoot.this);
		}
		@Override
		public void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
			reLayOut(AbstractPRoot.this);
			reLayOut(child);
		}
	};
	protected final ObserverList<PComponentObs> compObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PFocusObs> focusObsList
		= PCompUtil.createDefaultObserverList();
	protected final Set<PTimer> timerSet = new HashSet<>();
	protected final Set<PTimer> timersToAdd = new HashSet<>();
	protected final Set<PTimer> timersToRemove = new HashSet<>();
	protected Set<PComponent> reLayOutCompsFront = new TreeSet<>(COMPONENT_COMPARATOR);
	protected Set<PComponent> reLayOutCompsBack = new TreeSet<>(COMPONENT_COMPARATOR);
	protected final ReRenderSet reRenderSet = new ReRenderSet(this);
	protected PFocusTraversal focusTrav = new DefaultPFocusTraversal(this);
	protected PComponent focusOwner;
	protected String id;
	protected boolean needReLayout = true;
	protected boolean timerIterationInProgress;
	
	public AbstractPRoot() {
		layout = new PRootLayout(this);
		getLayout().addObs(layoutObs);
		PPanel body = new PPanel();
		body.setLayout(new PBorderLayout(body));
		getLayout().addChild(body, Constraint.BODY);
		getLayout().addChild(new DefaultPRootOverlay(), Constraint.OVERLAY);
	}
	
	/*
	 * Updates
	 */
	
	protected void update(double deltaTime) {
		tickAllTimers(deltaTime);
		reLayOutAll(DEFAULT_MAX_LAYOUT_ITERATION_COUNT);
	}
	
	@Override
	public void reLayOut() {
		if (needReLayout) {
			getLayout().invalidate();
			getLayout().layOut();
			needReLayout = false;
		}
	}
	
	@Override
	public void reLayOut(PComponent component) {
//		System.out.println("reLayOut "+component);
		reLayOutCompsFront.add(component);
		if (component == this) {
			needReLayout = true;
		}
	}
	
	protected void tickAllTimers(double deltaTime) {
		timerIterationInProgress = true;
		for (PTimer timer : timerSet) {
			timer.tick(deltaTime);
		}
		timerIterationInProgress = false;
		timerSet.removeAll(timersToRemove);
		timersToRemove.clear();
		timerSet.addAll(timersToAdd);
		timersToAdd.clear();
	}
	
	protected void reLayOutAll(int maxIterationCount) {
		int count = 0;
		while (!reLayOutCompsFront.isEmpty() && count++ < maxIterationCount) {
//			System.out.println();
//			System.out.println("reLayOutAll");
			
			Set<PComponent> temp = reLayOutCompsBack;
			reLayOutCompsBack = reLayOutCompsFront;
			reLayOutCompsFront = temp;
			for (PComponent comp : reLayOutCompsBack) {
//				System.out.println("LayOut="+comp);
				if (comp.getRoot() == this) {
					comp.reLayOut();
				}
			}
			reLayOutCompsBack.clear();
//			System.out.println("##############################");
//			System.out.println();
		}
//		if (count > 0) {
//			System.out.println("AbstractPRoot.reLayOutAll iterationCount="+count);
//		}
	}
	
	@Override
	public void reRender(PComponent component) {
		reRenderSet.add(component);
	}
	
	protected void defaultRootRender(PRenderer renderer,
			int rootClipX, int rootClipY, int rootClipFx, int rootClipFy)
	{
//		System.out.println("### defaultRootRender ###");
		Deque<RenderStackInfo> stack = createRenderStack(rootClipX, rootClipY, rootClipFx, rootClipFy);
		
		while (!stack.isEmpty()) {
			RenderStackInfo info = stack.pollLast();
			PComponent comp = info.child;
			PBounds compBounds = comp.getBounds();
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
//			System.out.println("comp="+comp+", clipX="+clipX+", clipY="+clipY+", clipW="+clipW+", clipH="+clipH);
			if (clipW < 0 || clipH < 0) {
//				System.out.println("doNotRenderComp="+comp);
				continue;
			}
			renderComponent(renderer, comp, clipX, clipY, clipW, clipH);
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					/*
					 * We need to addLast to make sure children are rendered after their parents
					 * and before any siblings of the parent will be rendered. (we call pollLast)
					 * Do NOT change to addFirst!
					 */
					stack.addLast(new RenderStackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		reRenderSet.clear();
//		System.out.println("#######");
//		System.out.println();
	}
	
	private Deque<RenderStackInfo> createRenderStack(int rootClipX, int rootClipY, int rootClipFx, int rootClipFy) {
		Deque<RenderStackInfo> stack = new ArrayDeque<>();
		/*
		 * If the root is to be rendered we will re-render everything.
		 */
		if (reRenderSet.containsRoot()) {
			stack.addLast(new RenderStackInfo(getBody(), rootClipX, rootClipY, rootClipFx, rootClipFy));
		} else {
			// these are filled by PCompUtil.fillClippedBounds(...)
			// This is used to cut down on the number of objects created
			MutablePBounds tmpBnds = new MutablePBounds();
			
			for (PComponent child : reRenderSet) {
				// We check to see whether the component is still part of this GUI tree (might have been removed by now)
				if (child.getRoot() == this) {
					PBounds clipBnds = PCompUtil.fillClippedBounds(tmpBnds, child);
					// If the clipped bounds are null the component is completely
					// concealed and does not need to be rendered
					if (clipBnds == null) {
						continue;
					}
					int clipX = clipBnds.getX();
					int clipY = clipBnds.getY();
					int clipFx = clipBnds.getFinalX();
					int clipFy = clipBnds.getFinalY();
					// We do addFirst here for consistency with the while-loop
					stack.addFirst(new RenderStackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		// The overlay must be rendered last whenever the rest of the GUI is rendered
		PRootOverlay overlay = getOverlay();
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				stack.addFirst(new RenderStackInfo(overlayComp, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
		}
		return stack;
	}
	
	private void renderComponent(PRenderer renderer, PComponent comp,
			int clipX, int clipY, int clipW, int clipH)
	{
		renderer.setClipBounds(clipX, clipY, clipW, clipH);
//		System.out.println("clip="+clipX+", "+clipY+", "+clipW+", "+clipH);
		
		PBorder border = comp.getBorder();
		if (border != null) {
			border.render(renderer, comp);
		}
		
		renderer.setRenderMode(renderer.getRenderModeFill());
		renderer.setColor1(1, 1, 1, 1);
		comp.render(renderer);
	}
	
	protected static class RenderStackInfo {
		public final PComponent child;
		public final int clipX;
		public final int clipY;
		public final int clipFx;
		public final int clipFy;
		
		public RenderStackInfo(PComponent child, int clipX,
				int clipY, int clipFx, int clipFy)
		{
			this.child = child;
			this.clipX = clipX;
			this.clipY = clipY;
			this.clipFx = clipFx;
			this.clipFy = clipFy;
		}
		
		@Override
		public String toString() {
			return child.toString();
		}
	}
	
	/*
	 * Focus, Layout and Input
	 */
	
	@Override
	public PComponent getFocusOwner() {
		return focusOwner;
	}
	
	@Override
	public void setFocusOwner(PComponent component) {
		PComponent oldOwner = getFocusOwner();
		if (component == oldOwner) {
			return;
		}
		while (component != null && !component.isFocusable()) {
			component = component.getParent();
		}
		focusOwner = component;
		if (oldOwner != null) {
			fireFocusLostEvent(oldOwner);
		}
		if (getFocusOwner() != null) {
			fireFocusGainedEvent(oldOwner);
		}
	}
	
	@Override
	public PRootLayout getLayout() {
		return layout;
	}
	
	protected void setStyleSheet(PStyleSheet styleSheet) {
		PStyleSheet oldStyleSheet = getStyleSheet();
		if (Objects.equals(oldStyleSheet, styleSheet)) {
			return;
		}
		if (oldStyleSheet != null) {
			oldStyleSheet.setRoot(null);
		}
		this.styleSheet = styleSheet;
		if (getStyleSheet() != null) {
			getStyleSheet().setRoot(this);
		}
		fireStyleSheetChanged(oldStyleSheet);
		reLayOut(this);
		reRender(this);
	}
	
	@Override
	public PStyleSheet getStyleSheet() {
		return styleSheet;
	}
	
	@Override
	public PMouse getMouse() {
		return mouse;
	}
	
	@Override
	public PKeyboard getKeyboard() {
		return keyboard;
	}
	
	@Override
	public PClipboard getClipboard() {
		return clipboard;
	}
	
	@Override
	public PDnDManager getDragAndDropManager() {
		return dndManager;
	}
	
	/*
	 * Timers
	 */
	
	@Override
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
	
	@Override
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
	
	@Override
	public void addObs(PComponentObs obs) throws NullPointerException {
		compObsList.add(obs);
	}
	
	@Override
	public void removeObs(PComponentObs obs) throws NullPointerException {
		compObsList.remove(obs);
	}
	
	@Override
	public void addObs(PFocusObs obs) throws NullPointerException {
		focusObsList.add(obs);
	}
	
	@Override
	public void removeObs(PFocusObs obs) throws NullPointerException {
		focusObsList.remove(obs);
	}
	
	@Override
	public void addObs(PMouseObs obs) {
		if (getMouse() != null) {
			getMouse().addObs(obs);
		}
	}
	
	@Override
	public void removeObs(PMouseObs obs) {
		if (getMouse() != null) {
			getMouse().removeObs(obs);
		}
	}
	
	@Override
	public void addObs(PKeyboardObs obs) {
		if (getKeyboard() != null) {
			getKeyboard().addObs(obs);
		}
	}
	
	@Override
	public void removeObs(PKeyboardObs obs) {
		if (getKeyboard() != null) {
			getKeyboard().removeObs(obs);
		}
	}
	
	protected void fireSizeChanged() {
		reRender(this);
		reLayOut(this);
		compObsList.fireEvent((obs) -> obs.onPreferredSizeChanged(this));
	}
	
	protected void fireFocusGainedEvent(PComponent oldFocusOwner) {
		PComponent newOwner = getFocusOwner();
		focusObsList.fireEvent((obs) -> obs.onFocusGained(
				oldFocusOwner, newOwner));
	}
	
	protected void fireFocusLostEvent(PComponent oldFocusOwner) {
		focusObsList.fireEvent((obs) -> obs.onFocusLost(
				oldFocusOwner));
	}
	
	@Override
	public void fireGlobalEvent(PComponent source, Object eventData)
			throws NullPointerException
	{
		globalObsList.fireEvent((obs) -> obs.onGlobalEvent(source, eventData));
	}
	
	@Override
	public void addObs(PGlobalEventObs obs) {
		globalObsList.add(obs);
	}
	
	@Override
	public void removeObs(PGlobalEventObs obs) {
		globalObsList.remove(obs);
	}
	
	@Override
	public void addObs(PRootObs obs) {
		rootObsList.add(obs);
	}
	
	@Override
	public void removeObs(PRootObs obs) {
		rootObsList.remove(obs);
	}
	
	@Override
	public void fireComponentAddedToGui(PComponent addedComponent) {
		rootObsList.fireEvent(obs ->
			obs.onComponentAddedToGui(addedComponent));
	}
	
	@Override
	public void fireComponentRemovedFromGui(PComponent parent,
			PComponent removedComponent)
	{
		rootObsList.fireEvent(obs ->
			obs.onComponentRemovedFromGui(parent, removedComponent));
	}
	
	@Override
	public void fireStyleSheetChanged(PStyleSheet oldStyleSheet) {
		rootObsList.fireEvent(obs ->
			obs.onStyleSheetChanged(AbstractPRoot.this, oldStyleSheet));
	}
	
	/*
	 * Uninteresting methods from component
	 */
	
	@Override
	public PRoot getRoot() {
		return this;
	}
	
	public void setFocusTraversal(PFocusTraversal focusTraversal) {
		focusTrav = focusTraversal;
	}
	
	@Override
	public PFocusTraversal getFocusTraversal() {
		return focusTrav;
	}
	
	/**
	 * Always returns null by default.<br>
	 */
	@Override
	public PDnDSupport getDragAndDropSupport() {
		return null;
	}
	
	/**
	 * Always returns zero by default.
	 */
	@Override
	public int getDepth() {
		return 0;
	}
	
	/**
	 * Always returns null by default.
	 */
	@Override
	public PComponent getParent() {
		return null;
	}
	
	/**
	 * Always returns the {@link PBounds bounds} of this root.
	 * @see #getBounds()
	 */
	@Override
	public PSize getDefaultPreferredSize() {
		return getBounds();
	}
	
	/**
	 * Always returns true by default.
	 */
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	/**
	 * Returns false by default.
	 */
	@Override
	public boolean isFocusable() {
		return false;
	}
	
	/*
	 * Unsupported inherited methods
	 */
	
	@Override
	public void setParent(PComponent parent) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PRoot");
	}
	
	@Override
	public void defaultRender(PRenderer renderer) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PRoot");
	}
	
	@Override
	public void setMouseOverCursor(PCursor cursor) {
		throw new UnsupportedOperationException("PRoot");
	}
	
	@Override
	public void setID(String value) {
		id = value;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
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
	
	@Override
	public String getDebugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("[class=");
		sb.append(getClass().getSimpleName());
		sb.append(", id=");
		sb.append(getID());
		sb.append(", bounds=");
		sb.append(getBounds());
		sb.append(", layout=");
		sb.append(getLayout());
		sb.append("]");
		return sb.toString();
	}
	
	/*
	 * Utility class
	 */
	
	protected static class FontInfo {
		
		protected final String name;
		protected final double size;
		protected final Style style;
		
		public FontInfo(String fontName, double pointSize, Style fontStyle) {
			ThrowException.ifNull(fontName, "fontName == null");
			ThrowException.ifNull(fontStyle, "fontStyle == null");
			ThrowException.ifLess(1, pointSize, "pointSize < 1");
			name = fontName;
			size = pointSize;
			style = fontStyle;
		}
		
		public String getName() {
			return name;
		}
		
		public double getSize() {
			return size;
		}
		
		public Style getStyle() {
			return style;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + style.ordinal();
			result = prime * result + name.hashCode();
			result = prime * result + Double.hashCode(size);
			return result;
		}
		
		@Override
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
		
		@Override
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