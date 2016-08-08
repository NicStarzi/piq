package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;

public class DelegatePRoot extends AbstractPRoot implements PRoot {
	
	protected final MutablePBounds bounds = new MutablePBounds(0, 0, 0, 0);
	protected PRoot delegateRoot;
	protected PComponent reprComp;
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
	
	public DelegatePRoot() {
		mouse = new DelegatePMouse(this);
	}
	
	public void setDelegateRoot(PRoot value) {
		delegateRoot = value;
		if (getDelegateRoot() != null) {
			getMouse().setDelegate(delegateRoot.getMouse());
		} else {
			getMouse().setDelegate(null);
		}
	}
	
	public PRoot getDelegateRoot() {
		return delegateRoot;
	}
	
	public void setRepresentative(PComponent component) {
		reprComp = component;
		if (getDelegateRoot() != null && getRepresentative() != null) {
			getDelegateRoot().reRender(getRepresentative());
		}
	}
	
	public PComponent getRepresentative() {
		return reprComp;
	}
	
	public void setBounds(PBounds value) {
		bounds.set(value);
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
		getMouse().setDelegate(mouse);
	}
	
	public DelegatePMouse getMouse() {
		return (DelegatePMouse) super.getMouse();
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
	
	public void reLayOutAll() {
		super.reLayOutAll();
	}
	
	public void reRender(PComponent component) {
		reRenderSet.add(component);
		if (getDelegateRoot() != null && getRepresentative() != null) {
			getDelegateRoot().reRender(getRepresentative());
		}
	}
	
	public void renderAll(PRenderer renderer) {
		PBounds bnds = getBounds();
		int rootX = bnds.getX();
		int rootY = bnds.getY();
		int rootFx = bnds.getFinalX();
		int rootFy = bnds.getFinalY();
		
		defaultRootRender(renderer, rootX, rootY, rootFx, rootFy);
	}
	
}