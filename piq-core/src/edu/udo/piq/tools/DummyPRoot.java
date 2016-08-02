package edu.udo.piq.tools;

import java.util.ArrayDeque;
import java.util.Deque;

import edu.udo.piq.PBounds;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.util.PCompUtil;

public class DummyPRoot extends AbstractPRoot implements PRoot {
	
	protected final MutablePBounds bounds = new MutablePBounds(0, 0, 0, 0);
	protected final ReRenderSet reRenderSet = new ReRenderSet(this);
	protected PRoot delegateRoot;
	protected boolean inheritDesignSheet = true;
	protected boolean inheritKeyboard = true;
	protected boolean inheritMouse = true;
	protected boolean inheritClipBoard = true;
	protected boolean inheritDnDManager = true;
	protected boolean enableMouseOverCursor = true;
	protected boolean enableCreateDialog = true;
	protected boolean enableFetchFont = true;
	protected boolean enableFetchImage = true;
	protected boolean enableCreateImage = true;
	protected boolean enableCreateCursor = true;
	
	public void setDelegateRoot(PRoot delegateRoot) {
		this.delegateRoot = delegateRoot;
	}
	
	public void setBounds(PSize size) {
		bounds.setWidth(size.getWidth());
		bounds.setHeight(size.getHeight());
		fireSizeChanged();
	}
	
	public PBounds getBounds() {
		return bounds;
	}
	
	public void setDesignSheet(PDesignSheet designSheet) {
		super.setDesignSheet(designSheet);
	}
	
	public PDesignSheet getDesignSheet() {
		if (delegateRoot != null && inheritDesignSheet) {
			return delegateRoot.getDesignSheet();
		}
		return super.getDesignSheet();
	}
	
	public void setClipboard(PClipboard clipboard) {
		this.clipboard = clipboard;
	}
	
	public PClipboard getClipboard() {
		if (delegateRoot != null && inheritClipBoard) {
			return delegateRoot.getClipboard();
		}
		return super.getClipboard();
	}
	
	public void setDragAndDropManager(PDnDManager dndManager) {
		this.dndManager = dndManager;
	}
	
	public PDnDManager getDragAndDropManager() {
		if (delegateRoot != null && inheritDnDManager) {
			return delegateRoot.getDragAndDropManager();
		}
		return null;
	}
	
	public void setMouse(PMouse mouse) {
		this.mouse = mouse;
	}
	
	public PMouse getMouse() {
		if (delegateRoot != null && inheritMouse) {
			return delegateRoot.getMouse();
		}
		return mouse;
	}
	
	public void setKeyboard(PKeyboard keyboard) {
		this.keyboard = keyboard;
	}
	
	public PKeyboard getKeyboard() {
		if (delegateRoot != null && inheritKeyboard) {
			return delegateRoot.getKeyboard();
		}
		return keyboard;
	}
	
	public void onMouseOverCursorChanged(PComponent component) {
		if (delegateRoot != null && enableMouseOverCursor) {
			delegateRoot.onMouseOverCursorChanged(component);
		}
	}
	
	public PDialog createDialog() {
		if (delegateRoot == null || !enableCreateDialog) {
			return null;
		}
		return delegateRoot.createDialog();
	}
	
	public boolean isFontSupported(PFontResource font) {
		if (delegateRoot != null) {
			return delegateRoot.isFontSupported(font);
		}
		return false;
	}
	
	public PFontResource fetchFontResource(String fontName, double pointSize, Style style) {
		if (delegateRoot == null || !enableFetchFont) {
			return null;
		}
		return delegateRoot.fetchFontResource(fontName, pointSize, style);
	}
	
	public PImageResource fetchImageResource(Object imgID) {
		if (delegateRoot == null || !enableFetchImage) {
			return null;
		}
		return delegateRoot.fetchImageResource(imgID);
	}
	
	public PImageResource createImageResource(int width, int height, PImageMeta metaInfo) {
		if (delegateRoot == null || !enableCreateImage) {
			return null;
		}
		return delegateRoot.createImageResource(width, height, metaInfo);
	}
	
	public PCursor createCustomCursor(PImageResource image, int offsetX, int offsetY) {
		if (delegateRoot == null || !enableCreateCursor) {
			return null;
		}
		return delegateRoot.createCustomCursor(image, offsetX, offsetY);
	}

	public boolean isElusive() {
		return false;
	}
	
	public void update(int milliSeconds) {
		super.update(milliSeconds);
	}
	
	public void tickAllTimers(int milliSeconds) {
		super.tickAllTimers(milliSeconds);
	}
	
	public void reLayOut() {
		System.out.println("DummyPRoot.reLayOut()");
		super.reLayOut();
	}
	
	public void reLayOut(PComponent component) {
		System.out.println("DummyPRoot.reLayOut="+component);
		super.reLayOut(component);
	}
	
	public void reLayOutAll() {
		super.reLayOutAll();
	}
	
	public void reRender(PComponent component) {
		System.out.println("DummyPRoot.reRender="+component);
		reRenderSet.add(component);
		if (delegateRoot != null) {
			delegateRoot.reRender(component);
		}
	}
	
	public void renderAll(PRenderer renderer) {
//		System.out.println("RENDER ALL");
//		System.out.println();
		
		PBounds bnds = getBounds();
		int rootFx = bnds.getWidth();
		int rootFy = bnds.getHeight();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
		/*
		 * If the root is to be rendered we will re-render everything.
		 */
		PRootOverlay overlay = getOverlay();
		if (reRenderSet.containsRoot()) {
			stack.addLast(new StackInfo(getBody(), true, 0, 0, rootFx, rootFy));
		} else {
			// these are filled by PCompUtil.fillClippedBounds(...)
			// This is used to cut down on the number of objects created
			MutablePBounds tmpBnds = new MutablePBounds();
			
			for (PComponent child : reRenderSet) {
				// We check to see whether the component is still part of this GUI tree
				if (child.getRoot() == this) {
					PBounds clipBnds = PCompUtil.fillClippedBounds(tmpBnds, child);
//					System.out.println("child="+child+", clipBnds="+clipBnds);
					// If the clipped bounds are null the component is completely 
					// concealed and does not need to be rendered
					if (clipBnds == null) {
//						System.out.println("JCompPRoot.render()::ComponentClipped");
						continue;
					}
					int clipX = clipBnds.getX();
					int clipY = clipBnds.getY();
					int clipFx = clipBnds.getFinalX();
					int clipFy = clipBnds.getFinalY();
					// We do addFirst here for consistency with the while-loop
					stack.addFirst(new StackInfo(child, true, clipX, clipY, clipFx, clipFy));
				}
			}
		}
//		System.out.println("STACK1="+stack);
		// The overlay must be rendered last whenever the rest of the GUI is rendered
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				stack.addFirst(new StackInfo(overlayComp, true, 0, 0, rootFx, rootFy));
			}
		}
//		System.out.println("STACK2="+stack);
		while (!stack.isEmpty()) {
			StackInfo info = stack.pollLast();
			PComponent comp = info.child;
			PBounds compBounds = comp.getBounds();
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
			System.out.println("comp="+comp+", clipX="+clipX+", clipY="+clipY+", clipW="+clipW+", clipH="+clipH);
			if (clipW < 0 || clipH < 0) {
				System.out.println("ignore="+comp);
				continue;
			}
			/*
			 * We might not need to render this component.
			 * Only render this component if this component fired a reRenderEvent 
			 * or if an ancestor of this component was rendered.
			 */
			boolean render = info.needRender || reRenderSet.contains(comp);
			if (render) {
				renderer.setClipBounds(clipX, clipY, clipW, clipH);
//				System.out.println("clip="+clipX+", "+clipY+", "+clipW+", "+clipH);
				
				renderer.setRenderMode(renderer.getRenderModeFill());
				renderer.setColor1(1, 1, 1, 1);
				PDesign design = comp.getDesign();
				design.render(renderer, comp);
//				System.out.println("render="+comp);
			}
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					/*
					 * We need to addFirst to make sure children are rendered after 
					 * their parents and before any siblings of the parent will be rendered.
					 * Do NOT change to addLast!
					 */
//					stack.addFirst(new StackInfo(child, render, clipX, clipY, clipFx, clipFy));
					stack.addLast(new StackInfo(child, render, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		reRenderSet.clear();
	}
	
	protected static class StackInfo {
		public final PComponent child;
		public final boolean needRender;
		public final int clipX;
		public final int clipY;
		public final int clipFx;
		public final int clipFy;
		
		public StackInfo(PComponent child, boolean needRender, 
				int clipX, int clipY, int clipFx, int clipFy) {
			this.child = child;
			this.needRender = needRender;
			this.clipX = clipX;
			this.clipY = clipY;
			this.clipFx = clipFx;
			this.clipFy = clipFy;
		}
		
		public String toString() {
			return child.toString();
		}
	}
	
}