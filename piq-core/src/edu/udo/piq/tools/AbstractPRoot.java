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

import edu.udo.piq.CallSuper;
import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderSubRoot;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootObs;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PTimer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.containers.DefaultPRootOverlay;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PLayoutObs;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.layouts.PRootLayout;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.style.PStyleSheet;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.Throw;

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
	
	protected final ObserverList<PRootObs> rootObsList = PiqUtil.createDefaultObserverList();
	protected final PRootLayout layout;
	protected PStyleSheet styleSheet;
	protected PMouse mouse;
	protected PKeyboard keyboard;
	protected PClipboard clipboard;
	protected PDnDManager dndManager;
	
	protected final PLayoutObs layoutObs = new PLayoutObs() {
		@Override
		public void onLayoutInvalidated(PReadOnlyLayout layout) {
			scheduleLayout(AbstractPRoot.this);
		}
		@Override
		public void onChildRemoved(PReadOnlyLayout layout, PComponentLayoutData data) {
			scheduleLayout(AbstractPRoot.this);
		}
		@Override
		public void onChildAdded(PReadOnlyLayout layout, PComponentLayoutData data) {
			scheduleLayout(AbstractPRoot.this);
			scheduleLayout(data.getComponent());
		}
	};
	protected final ObserverList<PComponentObs> compObsList = PiqUtil.createDefaultObserverList();
	protected final ObserverList<PFocusObs> focusObsList = PiqUtil.createDefaultObserverList();
	protected final Set<PTimer> timerSet = new HashSet<>();
	protected final List<Runnable> timerSetWriteBuffer = new ArrayList<>();
	protected Set<PComponent> reLayOutCompsFront = new TreeSet<>(AbstractPRoot.COMPONENT_DEPTH_COMPARATOR);
	protected Set<PComponent> reLayOutCompsBack = new TreeSet<>(AbstractPRoot.COMPONENT_DEPTH_COMPARATOR);
	protected ReRenderSet reRenderSet;
	protected PFocusTraversal focusTrav = null;
	protected PFocusTraversal activeFocusTrav = null;
	protected PComponent focusOwner;
	protected PComponent strongFocusOwner;
	protected String id;
	protected boolean needReLayout = true;
	protected boolean timerIterationInProgress;
	
	protected static boolean clipChildrenAtParentsBorder = true;
	
	public AbstractPRoot() {
		layout = new PRootLayout(this);
		reRenderSet = new ReRenderSet(this);
		getLayout().addObs(layoutObs);
//		setStyleSheet(new AbstractPStyleSheet() {
//			@Override
//			public PStyleComponent getStyleFor(PComponent component) {
//				return PStyleComponent.DEFAULT_COMPONENT_STYLE;
//			}
//		});
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
		reLayOutAll(AbstractPRoot.DEFAULT_MAX_LAYOUT_ITERATION_COUNT);
	}
	
	@Override
	public void redoLayOut() {
		if (needReLayout) {
			getLayout().invalidate();
			getLayout().layOut();
			needReLayout = false;
		}
	}
	
	@Override
	public void scheduleLayout(PComponent component) {
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
//			System.out.println("AbstractPRoot.reLayOutAll()");
			
			Set<PComponent> temp = reLayOutCompsBack;
			reLayOutCompsBack = reLayOutCompsFront;
			reLayOutCompsFront = temp;
			for (PComponent comp : reLayOutCompsBack) {
				//				System.out.println("LayOut="+comp);
				if (comp.getRoot() == this) {
					comp.redoLayOut();
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
	public void scheduleReRender(PComponent component) {
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
//		System.out.println();
//		System.out.println(root+".defaultRootRender()");
		while (!renderStack.isEmpty()) {
			RenderStackInfo info = renderStack.pollLast();
			try {
				PComponent comp = info.child;
				PBounds compBounds = comp.getBounds();
				// Calculate clipping area based on parent clip
				int clipX = Math.max(compBounds.getX(), info.clipX);
				int clipY = Math.max(compBounds.getY(), info.clipY);
				int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
				int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
				int clipW = clipFx - clipX;
				int clipH = clipFy - clipY;
//				System.out.println("comp="+comp+", clipX="+clipX+", clipY="+clipY+", clipW="+clipW+", clipH="+clipH);
				// Cull components with an empty clipping area
				if (clipW < 0 || clipH < 0) {
//					System.out.println("cullComponent="+comp);
					continue;
				}
				// comp may be a sub root for rendering such as an PAnimationPanel
				if (comp instanceof PRenderSubRoot) {
					AbstractPRoot.resetRenderState(renderer, clipX, clipY, clipW, clipH);
					
					PRenderSubRoot subRoot = (PRenderSubRoot) comp;
					subRoot.renderThisAndChildren(renderer, clipX, clipY, clipW, clipH);
					continue;
				}
				// comp is regular component and visible. Do rendering of comp and its children.
				AbstractPRoot.renderComponent(renderer, comp, clipX, clipY, clipW, clipH);
				
				PReadOnlyLayout layout = comp.getLayout();
				if (layout != null) {
					if (AbstractPRoot.clipChildrenAtParentsBorder && comp.getBorder() != null) {
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
			} catch (Exception e) {
				System.err.println("comp="+info.child.getDebugInfo()+", clipX="+info.clipX
						+", clipY="+info.clipY+", clipFx="+info.clipFx+", clipFy="+info.clipFy);
				e.printStackTrace();
			}
		}
	}
	
	public static Deque<RenderStackInfo> createRenderStack(PRoot root, ReRenderSet reRenderSet, int rootClipX, int rootClipY, int rootClipFx, int rootClipFy) {
		Deque<RenderStackInfo> stack = new ArrayDeque<>();
		/*
		 * If the root is to be rendered we will re-render everything.
		 */
		if (reRenderSet.containsRoot()) {
			PComponent body = root.getBody();
			if (body != null) {
				stack.addLast(new RenderStackInfo(body, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
			PComponent menuBar = root.getMenuBar();
			if (menuBar != null) {
				stack.addLast(new RenderStackInfo(menuBar, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
		} else {
			/*
			 * Performance Improvement:
			 * Only one PBounds object is created for all components to cut down
			 * the total number of object creations.
			 * The bounds are filled with PiqUtil.fillClippedBounds(...)
			 */
			MutablePBounds tmpBnds = new MutablePBounds();
			
			for (PComponent child : reRenderSet) {
				// We check to see whether the component is still part of this
				// GUI tree (might have been removed by now)
				// can this be too slow for components that don't cache the root?
				if (child.getRoot() == root) {
					// does not create a new instance of PBounds if tmpBnds != null
					PBounds clipBnds = PiqUtil.fillClippedBounds(tmpBnds, child);
					// If the clipped bounds are null the component is
					// completely concealed and does not need to be rendered
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
		// The overlay must be rendered last whenever the rest of the GUI is rendered
		PRootOverlay overlay = root.getOverlay();
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				// We do addFirst here for consistency with the while-loop
				// in defaultRootRender
				stack.addFirst(new RenderStackInfo(overlayComp, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
		}
		return stack;
	}
	
	public static void resetRenderState(PRenderer renderer,
			int clipX, int clipY, int clipW, int clipH)
	{
		// Reset renderer state
		renderer.setClipBounds(clipX, clipY, clipW, clipH);
		renderer.setRenderMode(renderer.getRenderModeFill());
		renderer.setColor1(1, 1, 1, 1);
	}
	
	public static void renderComponent(PRenderer renderer, PComponent comp,
			int clipX, int clipY, int clipW, int clipH)
	{
		AbstractPRoot.resetRenderState(renderer, clipX, clipY, clipW, clipH);
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
			Throw.ifNull(child, "child == null");
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
	public PComponent getLastStrongFocusOwner() {
		return strongFocusOwner;
	}
	
	@Override
	public void setFocusOwner(PComponent component) {
		PComponent oldOwner = getFocusOwner();
		if (component == oldOwner) {
			return;
		}
		if (component != null && component.getRoot() != this) {
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
		PComponent newOwner = getFocusOwner();
		if (newOwner == null) {
			if (getLastStrongFocusOwner() != null) {
				focusOwner = getLastStrongFocusOwner();
				fireFocusGainedEvent(focusOwner);
			}
		} else {
			if (newOwner.isStrongFocusOwner()) {
				strongFocusOwner = newOwner;
			}
			fireFocusGainedEvent(newOwner);
		}
		onFocusOwnerChanged();
	}
	
	@TemplateMethod
	protected void onComponentAddedToGui(PComponent addedComponent) {
	}
	
	@TemplateMethod
	@CallSuper
	protected void onComponentRemovedFromRoot(PComponent parent, PComponent removedComponent) {
		if (getLastStrongFocusOwner() == removedComponent) {
			strongFocusOwner = null;
		}
		if (getFocusOwner() == removedComponent) {
			setFocusOwner(null);
		}
	}
	
	@TemplateMethod
	@CallSuper
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
			oldStyleSheet.onRemovedFromRoot(this);
		}
		this.styleSheet = styleSheet;
		if (getStyleSheet() != null) {
			getStyleSheet().onAddedToRoot(this);
		}
		fireStyleSheetChanged(oldStyleSheet);
		scheduleLayout(this);
		scheduleReRender(this);
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
		scheduleReRender(this);
		scheduleLayout(this);
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
	public void addObs(PRootObs obs) {
		rootObsList.add(obs);
	}
	
	@Override
	public void removeObs(PRootObs obs) {
		rootObsList.remove(obs);
	}
	
	@Override
	public void fireComponentAddedToGui(PComponent addedComponent) {
		onComponentAddedToGui(addedComponent);
		rootObsList.fireEvent(obs -> obs.onComponentAddedToGui(addedComponent));
	}
	
	@Override
	public void fireComponentRemovedFromGui(PComponent parent, PComponent removedComponent) {
		onComponentRemovedFromRoot(parent, removedComponent);
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
	
}