package edu.udo.piq.components.defaults;

import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.collections.DefaultPModelExport;
import edu.udo.piq.components.collections.PDropComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelExport;
import edu.udo.piq.components.collections.PModelImport;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.tools.ImmutablePDnDTransfer;
import edu.udo.piq.util.ThrowException;

public class DefaultPDnDSupport implements PDnDSupport {
	
	private PModelImport modelImport = null;
	private PModelExport modelExport = new DefaultPModelExport();
	private PDnDTransfer activeTransfer;
	private boolean dragAllowed = true;
	private boolean dropAllowed = true;
	private boolean removeOnDrag = true;
	
	public void setModelExport(PModelExport value) {
		modelExport = value;
	}
	
	public PModelExport getModelExport() {
		return modelExport;
	}
	
	public void setModelImport(PModelImport value) {
		modelImport = value;
	}
	
	public PModelImport getModelImport() {
		return modelImport;
	}
	
	public void setRemoveOnDrag(boolean isRemoveOnDrag) {
		removeOnDrag = isRemoveOnDrag;
	}
	
	public boolean isRemoveOnDrag() {
		return removeOnDrag;
	}
	
	public void setDragAllowed(boolean isAllowed) {
		dragAllowed = isAllowed;
	}
	
	public boolean isDragAllowed() {
		return dragAllowed;
	}
	
	public void setDropAllowed(boolean isAllowed) {
		dropAllowed = isAllowed;
	}
	
	public boolean isDropAllowed() {
		return dropAllowed;
	}
	
	public PDnDTransfer getActiveTransfer() {
		return activeTransfer;
	}
	
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) 
			throws NullPointerException 
	{
		// Throw exceptions for illegal input
		ThrowException.ifNull(target, "target == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dstComp = ThrowException.ifTypeCastFails(target, 
				PDropComponent.class, "target instanceof PDropComponent == false");
		// If dropping is disabled for this DnD support
		if (!isDropAllowed()) {
			return false;
		}
		// If there is no destination model we can not drop
		PModel dstModel = dstComp.getModel();
		if (dstModel == null) {
			return false;
		}
		// If there is no destination index we can not drop
		PModelIndex dstIndex = dstComp.getDropIndex(x, y);
		if (dstIndex == null) {
			return false;
		}
		// If the source is the destination we don't need to export & import the data,
		// instead we do a naive data transfer 
		if (dstComp == transfer.getSource()) {
			List<PModelIndex> dragIndices = dstComp.getDragIndices();
			if (dragIndices.size() == 1 && dragIndices.contains(dstIndex)) {
				return false;
			}
		}
//		List<IndexAndContentTuple> dataList = transfer.getData();
//		for (IndexAndContentTuple tuple : dataList) {
//			if (!model.canAdd(dropIndex, tuple.getContent())) {
//				return false;
//			}
//		}
		// Check if there is a PModelImport strategy for the importData
		PModel importData = transfer.getData();
		PModelImport dataImport = getModelImport();
		if (dataImport == null) {
			// If there is no known strategy we do a naive import
			for (PModelIndex index : importData) {
				Object element = importData.get(index);
				if (!dstModel.canAdd(dstIndex, element)) {
					return false;
				}
			}
			return true;
		} else {
			// If a PModelImport was defined for the importData we delegate
			return dataImport.canImportData(dstModel, dstIndex, importData);
		}
	}
	
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		// Check if drop is allowed
		ThrowException.ifFalse(canDrop(target, transfer, x, y), "canDrop(target, transfer, x, y) == false");
		// Get all necessary data
		PDropComponent dstComp = (PDropComponent) target;
		PModel dstModel = dstComp.getModel();
		PModelIndex dstIndex = dstComp.getDropIndex(x, y);
		
		PModel importData = transfer.getData();
		PModelImport dataImport = getModelImport();
		// Check if there is a PModelImport strategy defined for the given importData
		if (dataImport == null) {
			// Do a naive import for unknown data
			for (PModelIndex index : importData) {
				Object element = importData.get(index);
				dstModel.add(dstIndex, element);
			}
		} else {
			// Delegate import to PModelImport strategy
			dataImport.importData(dstModel, dstIndex, importData);
		}
//		Iterator<PModelIndex> iterator = data.insertIterator();
//		while (iterator.hasNext()) {
//			PModelIndex index = iterator.next();
//			Object content = data.get(index);
//			model.add(index.withOffset(dropIndex), content);
//		}
//		List<IndexAndContentTuple> dataList = transfer.getData();
//		for (IndexAndContentTuple tuple : dataList) {
//			model.add(dropIndex, tuple.getContent());
//		}
	}
	
	public boolean canDrag(PComponent source, int x, int y)
			throws NullPointerException 
	{
		ThrowException.ifNull(source, "source == null");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		if (!isDragAllowed()) {
			return false;
		}
		// If we have no model export we don't know how to export our data
		if (getModelExport() == null) {
			return false;
		}
		// If the root does not support drag and drop or if there is no root we can not start a drag
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		PModel srcModel = srcComp.getModel();
		if (srcModel == null) {
			return false;
		}
		List<PModelIndex> srcIndices = srcComp.getDragIndices();
		if (srcIndices == null || srcIndices.size() == 0) {
			return false;
		}
		PModelExport modelExport = getModelExport();
		if (modelExport == null) {
			return false;
		}
		return modelExport.canExport(srcModel, srcIndices);
//		if (isRemoveOnDrag()) {
//			for (PModelIndex index : srcIndices) {
//				if (!srcModel.canRemove(index)) {
//					return false;
//				}
//			}
//		}
//		return true;
	}
	
	public void startDrag(PComponent source, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		ThrowException.ifFalse(canDrag(source, x, y), "canDrag(source, x, y) == false");
		ThrowException.ifNotNull(activeTransfer, "activeTransfer != null");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		
		PModel srcModel = srcComp.getModel();
		List<PModelIndex> srcIndices = srcComp.getDragIndices();
		
		PModel importData = getModelExport().createExportModel(srcModel, srcIndices);
		PComponent dragPres = createVisibleRepresentation(importData);
		
//		List<IndexAndContentTuple> data = new ArrayList<>();
//		for (PModelIndex dragIndex : dragIndices) {
//			data.add(new IndexAndContentTuple(model.get(dragIndex), dragIndex));
////			data.add(model.get(dragIndex));
//		}
		activeTransfer = new ImmutablePDnDTransfer(source, x, y, importData, dragPres);
		
		source.getDragAndDropManager().startDrag(activeTransfer);
	}
	
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) 
			throws NullPointerException, IllegalArgumentException 
	{
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(target, "target == null");
		ThrowException.ifNull(transfer, "transfer == null");
		ThrowException.ifNotEqual(transfer, activeTransfer, "transfer != activeTransfer");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		
		activeTransfer = null;
		
		PModel srcModel = srcComp.getModel();
		List<PModelIndex> srcIndices = srcComp.getDragIndices();
		getModelExport().finishExport(srcModel, srcIndices);
//		if (isRemoveOnDrag()) {
			//FIXME: Needs a better implementation. The drag indices are not necessarily in a good order for removal.
//			PModel srcModel = srcComp.getModel();
//			List<PModelIndex> srcIndices = srcComp.getDragIndices();
//			while (!srcIndices.isEmpty()) {
//				PModelIndex dragIndex = srcIndices.remove(srcIndices.size() - 1);
//				srcModel.remove(dragIndex);
//			}
//		}
	}
	
	public void abortDrag(PComponent source, PDnDTransfer transfer)
			throws NullPointerException 
	{
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(transfer, "transfer == null");
		ThrowException.ifNotEqual(transfer, activeTransfer, "transfer != activeTransfer");
		ThrowException.ifTypeCastFails(source, PDropComponent.class, 
				"(source instanceof PDropComponent) == false");
		activeTransfer = null;
	}
	
	public void showDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dropComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		dropComp.setDropHighlight(dropIndex);
	}
	
	public void hideDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dropComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		dropComp.setDropHighlight(null);
	}
	
	protected PComponent createVisibleRepresentation(PModel data) {
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("DragAndDrop.png");
		pic.setStretchToSize(true);
		pic.setElusive(true);
		return pic;
	}
	
}