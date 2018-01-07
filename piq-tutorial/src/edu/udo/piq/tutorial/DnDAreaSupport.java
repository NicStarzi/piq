package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.dnd.PDnDIndicator;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.dnd.PDnDTransfer;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.ImmutablePDnDTransfer;
import edu.udo.piq.tools.SingletonPModel;

public class DnDAreaSupport implements PDnDSupport {
	
	private PDnDTransfer activeTransfer;
	
	@Override
	public PDnDTransfer getActiveTransfer() {
		return activeTransfer;
	}
	
	@Override
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) {
		if (target == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(target instanceof DnDArea) || !(transfer instanceof DnDAreaTransfer)) {
			return false;
		}
		if (!(transfer.getSource() instanceof DnDArea)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y) {
		if (!canDrop(target, transfer, x, y)) {
			throw new IllegalStateException("canDrop(target, transfer, x, y)=false");
		}
		DnDAreaTransfer areaTrans = (DnDAreaTransfer) transfer;
		DnDArea dstArea = (DnDArea) target;
		PComponent comp = getComponent(transfer);
		
		PBounds areaBnds = dstArea.getBounds();
		int dropX = (x - areaBnds.getX()) - areaTrans.compW / 2;
		int dropY = (y - areaBnds.getY()) - areaTrans.compH / 2;
		dstArea.getLayout().addChild(comp, new FreeConstraint(dropX, dropY));
	}
	
	@Override
	public boolean canDrag(PComponent source, int x, int y) {
		if (source == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof DnDArea)) {
			return false;
		}
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		DnDArea area = (DnDArea) source;
		return area.getLayout().getChildAt(x, y) != null;
	}
	
	@Override
	public void startDrag(PComponent source, int x, int y) {
		if (!canDrag(source, x, y)) {
			throw new IllegalArgumentException("canDrag(source, x, y)=false");
		}
		if (activeTransfer != null) {
			throw new IllegalStateException("previous transfer is still active");
		}
		DnDArea area = (DnDArea) source;
		PComponent comp = area.getLayout().getChildAt(x, y);
		
		PModel data = new SingletonPModel(comp);
		PDnDIndicator previewComp = new DnDCompPreview(comp);
		DnDAreaTransfer areaTrans = new DnDAreaTransfer(source, x, y, data, previewComp);
		areaTrans.compW = comp.getBounds().getWidth();
		areaTrans.compH = comp.getBounds().getHeight();
		activeTransfer = areaTrans;
		
		source.getDragAndDropManager().startDrag(activeTransfer);
	}
	
	@Override
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) {
		if (source == null || target == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof DnDArea)) {
			throw new IllegalArgumentException("source not instance of "+DnDArea.class.getName());
		}
		if (transfer != activeTransfer) {
			throw new IllegalStateException("transfer was not started by this DnDSupport");
		}
		activeTransfer = null;
		
		DnDArea area = (DnDArea) source;
		PComponent comp = getComponent(transfer);
		
		area.getLayout().removeChild(comp);
	}
	
	@Override
	public void abortDrag(PComponent source, PDnDTransfer transfer) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof DnDArea)) {
			throw new IllegalArgumentException("source not instance of "+DnDArea.class.getName());
		}
		if (transfer != activeTransfer) {
			throw new IllegalStateException("transfer was not started by this DnDSupport");
		}
		activeTransfer = null;
	}
	
	@Override
	public void showDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof DnDArea)) {
			throw new IllegalArgumentException("source not instance of "+DnDArea.class.getName());
		}
		DnDArea area = (DnDArea) source;
		PComponent comp = getComponent(transfer);
		PSize compSize = comp.getPreferredSize();
		
		int w = compSize.getWidth();
		int h = compSize.getHeight();
		area.showDropLoc(x, y, w, h);
	}
	
	@Override
	public void hideDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof DnDArea)) {
			throw new IllegalArgumentException("source not instance of "+DnDArea.class.getName());
		}
		DnDArea area = (DnDArea) source;
		area.hideDropLoc();
	}
	
	private PComponent getComponent(PDnDTransfer transfer) {
		return (PComponent) ((SingletonPModel) transfer.getData()).getSingleElement();
	}
	
	private static class DnDAreaTransfer extends ImmutablePDnDTransfer {
		
		public int compW;
		public int compH;
		
		public DnDAreaTransfer(PComponent source,
				int fromX, int fromY, PModel dataModel,
				PDnDIndicator visibleRepresentation)
		{
			super(source, fromX, fromY, dataModel, visibleRepresentation);
		}
		
	}
	
}