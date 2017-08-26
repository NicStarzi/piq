package edu.udo.piq.tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFocusTraversal;
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
import edu.udo.piq.PStyleSheet;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.containers.DefaultPRootOverlay;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PRootLayout;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPRoot implements PRoot {
	
	public static final int DEFAULT_MAX_LAYOUT_ITERATION_COUNT = 100;
	public static final Comparator<PComponent> COMPONENT_DEPTH_COMPARATOR = (o1, o2) -> {
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
	
	protected final ObserverList<PGlobalEventObs> globalObsList = PCompUtil.createDefaultObserverList();
	protected final ObserverList<PRootObs> rootObsList = PCompUtil.createDefaultObserverList();
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
	protected final ObserverList<PComponentObs> compObsList = PCompUtil.createDefaultObserverList();
	protected final ObserverList<PFocusObs> focusObsList = PCompUtil.createDefaultObserverList();
	protected final Set<PTimer> timerSet = new HashSet<>();
	protected final List<Runnable> timerSetWriteBuffer = new ArrayList<>();
	protected Set<PComponent> reLayOutCompsFront = new TreeSet<>(COMPONENT_DEPTH_COMPARATOR);
	protected Set<PComponent> reLayOutCompsBack = new TreeSet<>(COMPONENT_DEPTH_COMPARATOR);
	protected ReRenderSet reRenderSet;
	protected PFocusTraversal focusTrav = null;
	protected PFocusTraversal activeFocusTrav = null;
	protected PComponent focusOwner;
	protected String id;
	protected boolean needReLayout = true;
	protected boolean timerIterationInProgress;
	
	protected static boolean clipChildrenAtParentsBorder = true;
	
	public AbstractPRoot() {
		layout = new PRootLayout(this);
		reRenderSet = new ReRenderSet(this);
		getLayout().addObs(layoutObs);
		PPanel body = new PPanel();
		body.setLayout(new PBorderLayout(body));
		getLayout().addChild(body, Constraint.BODY);
		getLayout().addChild(new DefaultPRootOverlay(), Constraint.OVERLAY);
	}
	
	/*
	 * Updates
	 */
	
	protected void update(double deltaMilliSc) {
		tickAllTimers(deltaMilliSc);
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
	
	protected void tickAllTimers(double deltaMilliSc) {
		timerIterationInProgress = true;
		for (PTimer timer : timerSet) {
			timer.tick(deltaMilliSc);
		}
		timerIterationInProgress = false;
		timerSetWriteBuffer.forEach(r -> r.run());
		timerSetWriteBuffer.clear();
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
	
	protected void defaultRootRender(PRenderer renderer, int rootClipX, int rootClipY, int rootClipFx, int rootClipFy) {
		//		System.out.println("### defaultRootRender ###");
		Deque<RenderStackInfo> renderStack = AbstractPRoot.createRenderStack(this, reRenderSet, rootClipX, rootClipY, rootClipFx, rootClipFy);
		AbstractPRoot.defaultRootRender(this, renderer, renderStack);
		reRenderSet.clear();
		//		System.out.println("#######");
		//		System.out.println();
	}
	
	public static void defaultRootRender(PRoot root, PRenderer renderer, Deque<RenderStackInfo> renderStack) {
		while (!renderStack.isEmpty()) {
			RenderStackInfo info = renderStack.pollLast();
			PComponent comp = info.child;
			PBounds compBounds = comp.getBounds();
			// Calculate clipping area based on parent clip
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
			//			System.out.println("comp="+comp+", clipX="+clipX+", clipY="+clipY+", clipW="+clipW+", clipH="+clipH);
			// Cull components with an empty clipping area
			if (clipW < 0 || clipH < 0) {
				//				System.out.println("doNotRenderComp="+comp);
				continue;
			}
			AbstractPRoot.renderComponent(renderer, comp, clipX, clipY, clipW, clipH);
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				if (clipChildrenAtParentsBorder && comp.getBorder() != null) {
					PBounds compBoundsWithoutBorder = comp.getBoundsWithoutBorder();
					clipX = Math.max(compBoundsWithoutBorder.getX(), info.clipX);
					clipY = Math.max(compBoundsWithoutBorder.getY(), info.clipY);
					clipFx = Math.min(compBoundsWithoutBorder.getFinalX(), info.clipFx);
					clipFy = Math.min(compBoundsWithoutBorder.getFinalY(), info.clipFy);
				}
				for (PComponent child : layout.getChildren()) {
					/*
					 * We need to addLast to make sure children are rendered
					 * after their parents
					 * and before any siblings of the parent will be rendered.
					 * (we call pollLast)
					 * Do _NOT_ change to addFirst!
					 */
					renderStack.addLast(new RenderStackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
	}
	
	public static Deque<RenderStackInfo> createRenderStack(PRoot root, ReRenderSet reRenderSet, int rootClipX, int rootClipY, int rootClipFx, int rootClipFy) {
		Deque<RenderStackInfo> stack = new ArrayDeque<>();
		/*
		 * If the root is to be rendered we will re-render everything.
		 */
		if (reRenderSet.containsRoot()) {
			stack.addLast(new RenderStackInfo(root.getBody(), rootClipX, rootClipY, rootClipFx, rootClipFy));
		} else {
			/*
			 * Performance Improvement:
			 * Only one PBounds object is created for all components to cut down
			 * the total number of object creations.
			 * The bounds are filled with PCompUtil.fillClippedBounds(...)
			 */
			MutablePBounds tmpBnds = new MutablePBounds();
			
			for (PComponent child : reRenderSet) {
				// We check to see whether the component is still part of this
				// GUI tree (might have been removed by now)
				if (child.getRoot() == root) {
					PBounds clipBnds = PCompUtil.fillClippedBounds(tmpBnds, child);
					// If the clipped bounds are null the component is
					// completely
					// concealed and does not need to be rendered
					if (clipBnds == null) {
						continue;
					}
					int clipX = clipBnds.getX();
					int clipY = clipBnds.getY();
					int clipFx = clipBnds.getFinalX();
					int clipFy = clipBnds.getFinalY();
					// We do addFirst here for consistency with the while-loop
					// in defaultRootRender
					stack.addFirst(new RenderStackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		// The overlay must be rendered last whenever the rest of the GUI is
		// rendered
		PRootOverlay overlay = root.getOverlay();
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				stack.addFirst(new RenderStackInfo(overlayComp, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
		}
		return stack;
	}
	
	public static void renderComponent(PRenderer renderer, PComponent comp,
			int clipX, int clipY, int clipW, int clipH)
	{
		// Reset renderer state
		renderer.setClipBounds(clipX, clipY, clipW, clipH);
		renderer.setRenderMode(renderer.getRenderModeFill());
		renderer.setColor1(1, 1, 1, 1);
		
		// Render the border first
		PBorder border = comp.getBorder();
		if (border != null) {
			border.render(renderer, comp);
			// the render method of border might have changed this state
			renderer.setClipBounds(clipX, clipY, clipW, clipH);
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.setColor1(1, 1, 1, 1);
		}
		comp.render(renderer);
	}
	
	public static class RenderStackInfo {
		
		public final PComponent child;
		public final int clipX;
		public final int clipY;
		public final int clipFx;
		public final int clipFy;
		
		public RenderStackInfo(PComponent child, int clipX, int clipY, int clipFx, int clipFy) {
			this.child = child;
			this.clipX = clipX;
			this.clipY = clipY;
			this.clipFx = clipFx;
			this.clipFy = clipFy;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("{child=");
			sb.append(child);
			sb.append(", parentClipX=");
			sb.append(clipX);
			sb.append(", parentClipY=");
			sb.append(clipY);
			sb.append(", parentClipFx=");
			sb.append(clipFx);
			sb.append(", parentClipFy=");
			sb.append(clipFy);
			sb.append("}");
			return sb.toString();
//			return child.toString();
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
		if (component == oldOwner) {
			return;
		}
		focusOwner = component;
		if (oldOwner != null) {
			fireFocusLostEvent(oldOwner);
		}
		if (getFocusOwner() != null) {
			fireFocusGainedEvent(oldOwner);
		}
		onFocusOwnerChanged();
	}
	
	protected void onFocusOwnerChanged() {
		PComponent current = getFocusOwner();
		PFocusTraversal newActiveFocusTraversal = null;
		while (current != null) {
			PFocusTraversal focusTrav = current.getFocusTraversal();
			if (focusTrav != null) {
				newActiveFocusTraversal = focusTrav;
				break;
			}
			current = current.getParent();
		}
		PFocusTraversal oldActiveFocusTraversal = getActiveFocusTraversal();
		if (newActiveFocusTraversal != oldActiveFocusTraversal) {
			if (oldActiveFocusTraversal != null) {
				oldActiveFocusTraversal.uninstall(this);
			}
			activeFocusTrav = newActiveFocusTraversal;
			/*
			 * A subclass may decide to overwrite getActiveFocusTraversal() to have more control over
			 * focus traversal policy. We invoke the getter here to guarantee model integrity.
			 */
			newActiveFocusTraversal = getActiveFocusTraversal();
			if (newActiveFocusTraversal != null) {
				newActiveFocusTraversal.install(this);
			}
		}
	}
	
	public void setFocusTraversal(PFocusTraversal focusTraversal) {
		PFocusTraversal oldFocusTrav = getFocusTraversal();
		focusTrav = focusTraversal;
		if (oldFocusTrav == getActiveFocusTraversal()) {
			if (getActiveFocusTraversal() != null) {
				getActiveFocusTraversal().uninstall(this);
			}
			activeFocusTrav = focusTrav;
			if (getActiveFocusTraversal() != null) {
				getActiveFocusTraversal().install(this);
			}
		}
	}
	
	@Override
	public PFocusTraversal getFocusTraversal() {
		return focusTrav;
	}
	
	@Override
	public PFocusTraversal getActiveFocusTraversal() {
		return activeFocusTrav;
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
			throw new NullPointerException("timer=" + timer);
		}
		if (timerIterationInProgress) {
			timerSetWriteBuffer.add(() -> timerSet.add(timer));
		} else {
			timerSet.add(timer);
		}
	}
	
	@Override
	public void unregisterTimer(PTimer timer) throws NullPointerException {
		if (timer == null) {
			throw new NullPointerException("timer=" + timer);
		}
		if (timerIterationInProgress) {
			timerSetWriteBuffer.add(() -> timerSet.remove(timer));
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
		focusObsList.fireEvent((obs) -> obs.onFocusGained(oldFocusOwner, newOwner));
	}
	
	protected void fireFocusLostEvent(PComponent oldFocusOwner) {
		focusObsList.fireEvent((obs) -> obs.onFocusLost(oldFocusOwner));
	}
	
	@Override
	public void fireGlobalEvent(PComponent source, Object eventData) throws NullPointerException {
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
		rootObsList.fireEvent(obs -> obs.onComponentAddedToGui(addedComponent));
	}
	
	@Override
	public void fireComponentRemovedFromGui(PComponent parent, PComponent removedComponent) {
		rootObsList.fireEvent(obs -> obs.onComponentRemovedFromGui(parent, removedComponent));
	}
	
	@Override
	public void fireStyleSheetChanged(PStyleSheet oldStyleSheet) {
		rootObsList.fireEvent(obs -> obs.onStyleSheetChanged(AbstractPRoot.this, oldStyleSheet));
	}
	
	/*
	 * Utility Methods
	 */
	
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
	
	public static class FontInfo {
		
		protected final String name;
		protected final int size;
		protected final Style style;
		
		public FontInfo(String fontName, int pixelSize, Style fontStyle) {
			ThrowException.ifNull(fontName, "fontName == null");
			ThrowException.ifNull(fontStyle, "fontStyle == null");
			ThrowException.ifLess(1, pixelSize, "pixelSize < 1");
			name = fontName;
			size = pixelSize;
			style = fontStyle;
		}
		
		public String getName() {
			return name;
		}
		
		public int getPixelSize() {
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
			return name.equals(other.name) && size == other.size && style == other.style;
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