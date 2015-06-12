package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.tools.ImmutablePDnDTransfer;

public class DefaultPDnDSupport implements PDnDSupport {
	
//	public static final DefaultPDnDSupport FLYWEIGHT_INSTANCE = new DefaultPDnDSupport();
	private PDnDTransfer activeTransfer;
	
	public PDnDTransfer getActiveTransfer() {
		return activeTransfer;
	}
	
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) 
			throws NullPointerException 
	{
		if (target == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(target instanceof PDropComponent)) {
			throw new IllegalArgumentException("target not instance of "+PDropComponent.class.getName());
		}
		PDropComponent dropComp = (PDropComponent) target;
		PModel model = dropComp.getModel();
		if (model == null) {
			return false;
		}
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		if (dropIndex == null) {
			return false;
		}
		if (target == transfer.getSource()) {
			List<PModelIndex> dragIndices = dropComp.getDragIndices();
			if (dragIndices.size() == 1 && dragIndices.contains(dropIndex)) {
				return false;
			}
		}
		@SuppressWarnings("unchecked")
		List<Object> dataList = (List<Object>) transfer.getData();
		for (Object data : dataList) {
			if (!model.canAdd(dropIndex, data)) {
				return false;
			}
		}
		return true;
	}
	
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		if (!canDrop(target, transfer, x, y)) {
			throw new IllegalStateException("canDrop(target, transfer, x, y)=false");
		}
		PDropComponent dropComp = (PDropComponent) target;
		PModel model = dropComp.getModel();
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		
		@SuppressWarnings("unchecked")
		List<Object> dataList = (List<Object>) transfer.getData();
		for (Object data : dataList) {
			model.add(dropIndex, data);
		}
	}
	
	public boolean canDrag(PComponent source, int x, int y)
			throws NullPointerException 
	{
		if (source == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof PDropComponent)) {
			throw new IllegalArgumentException("source not instance of "+PDropComponent.class.getName());
		}
		// If the root does not support drag and drop or if there is no root to begin with
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		PDropComponent dragComp = (PDropComponent) source;
		PModel model = dragComp.getModel();
		if (model == null) {
			return false;
		}
		List<PModelIndex> dragIndices = dragComp.getDragIndices();
		if (dragIndices == null) {
			return false;
		}
		for (PModelIndex dragIndex : dragIndices) {
			if (!dragIndex.canRemove(model)) {
				return false;
			}
		}
		return true;
	}
	
	public void startDrag(PComponent source, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		if (!canDrag(source, x, y)) {
			throw new IllegalArgumentException("canDrag(source, x, y)=false");
		}
		if (activeTransfer != null) {
			throw new IllegalStateException("previous transfer is still active");
		}
		PDropComponent dragComp = (PDropComponent) source;
		PModel model = dragComp.getModel();
		List<PModelIndex> dragIndices = dragComp.getDragIndices();
		List<Object> data = new ArrayList<>();
		for (PModelIndex dragIndex : dragIndices) {
			data.add(model.get(dragIndex));
		}
		activeTransfer = new ImmutablePDnDTransfer(source, x, y, data, 
				createVisibleRepresentation(data));
		
		source.getDragAndDropManager().startDrag(activeTransfer);
	}
	
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) 
			throws NullPointerException, IllegalArgumentException 
	{
		if (source == null || target == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof PDropComponent)) {
			throw new IllegalArgumentException("source not instance of "+PDropComponent.class.getName());
		}
		if (transfer != activeTransfer) {
			throw new IllegalStateException("transfer was not started by this DnDSupport");
		}
		activeTransfer = null;
		
		PDropComponent dragComp = (PDropComponent) source;
		PModel model = dragComp.getModel();
		List<PModelIndex> dragIndices = dragComp.getDragIndices();
		while (!dragIndices.isEmpty()) {
			PModelIndex dragIndex = dragIndices.remove(0);
			model.remove(dragIndex);
		}
	}
	
	public void abortDrag(PComponent source, PDnDTransfer transfer)
			throws NullPointerException 
	{
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof PDropComponent)) {
			throw new IllegalArgumentException("source not instance of "+PDropComponent.class.getName());
		}
		if (transfer != activeTransfer) {
			throw new IllegalStateException("transfer was not started by this DnDSupport");
		}
		activeTransfer = null;
	}
	
	public void showDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof PDropComponent)) {
			throw new IllegalArgumentException("source not instance of "+PDropComponent.class.getName());
		}
		PDropComponent dropComp = (PDropComponent) source;
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		dropComp.setDropHighlight(dropIndex);
	}
	
	public void hideDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		if (!(source instanceof PDropComponent)) {
			throw new IllegalArgumentException("source not instance of "+PDropComponent.class.getName());
		}
		PDropComponent dropComp = (PDropComponent) source;
		dropComp.setDropHighlight(null);
	}
	
	protected PComponent createVisibleRepresentation(List<Object> data) {
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("DragAndDrop.png");
		pic.setStretchToSize(true);
		pic.setElusive(true);
		return pic;
	}
	
}