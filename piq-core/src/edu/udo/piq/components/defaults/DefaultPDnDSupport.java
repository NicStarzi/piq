package edu.udo.piq.components.defaults;

import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.collections.PDropComponent;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelExport;
import edu.udo.piq.components.collections.PModelImport;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.util.SymbolicImageKey;
import edu.udo.piq.dnd.PDnDIndicator;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.dnd.PDnDTransfer;
import edu.udo.piq.tools.ImmutablePDnDTransfer;
import edu.udo.piq.util.ThrowException;

public class DefaultPDnDSupport implements PDnDSupport {
	
	public static final Object IMAGE_ID_DND_POSSIBLE =
			new SymbolicImageKey(DefaultPDnDSupport.class+"_possible");
	public static final Object IMAGE_ID_DND_IMPOSSIBLE =
			new SymbolicImageKey(DefaultPDnDSupport.class+"_impossible");
	
	protected PModelImport modelImport = null;
	protected PModelExport modelExport = null;
	protected PDnDTransfer activeTransfer;
	protected Object dndPosImageID = IMAGE_ID_DND_POSSIBLE;
	protected Object dndImpImageID = IMAGE_ID_DND_IMPOSSIBLE;
	protected boolean dragAllowed = true;
	protected boolean dropAllowed = true;
	protected boolean removeOnDrag = true;
	
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
	
	public void setDragPossibleImageID(Object imgID) {
		dndPosImageID = imgID;
	}
	
	public Object getDropPossibleImageID() {
		return dndPosImageID;
	}
	
	public void setDropImpossibleImageID(Object imgID) {
		dndImpImageID = imgID;
	}
	
	public Object getDropImpossibleImageID() {
		return dndImpImageID;
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
	
	@Override
	public PDnDTransfer getActiveTransfer() {
		return activeTransfer;
	}
	
	@Override
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
		// Check if there is a PModelImport strategy
		PModel importData = transfer.getData();
		PModelImport dataImport = getModelImport();
		if (dataImport == null) {
			// If there is no known strategy we do a naive import
			return naiveCanImport(dstModel, dstIndex, importData);
		} else {
			// If a PModelImport was defined we delegate
			return dataImport.canImportData(dstModel, dstIndex, importData);
		}
	}
	
	protected boolean naiveCanImport(PModel dstModel, PModelIndex dstIndex, PModel importData) {
		for (PModelIndex index : importData) {
			Object element = importData.get(index);
			if (!dstModel.canAdd(dstIndex, element)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
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
		// Check if there is a PModelImport strategy defined
		if (dataImport == null) {
			// Do a naive import for unknown data
			doNaiveImport(dstComp, dstModel, dstIndex, importData);
		} else {
			// Delegate import to PModelImport strategy
			dataImport.importData(dstModel, dstIndex, importData);
		}
	}
	
	protected void doNaiveImport(PDropComponent dstComp,
			PModel dstModel, PModelIndex dstIndex, PModel importData)
	{
		// Do a naive import for unknown data
		for (PModelIndex index : importData) {
			Object element = importData.get(index);
			if (element != PTreePDnDSupport.EXPORT_MODEL_ROOT_CONTENT) {
				dstModel.add(dstIndex, element);
			}
		}
	}
	
	@Override
	public boolean canDrag(PComponent source, int x, int y)
			throws NullPointerException
	{
		ThrowException.ifNull(source, "source == null");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source,
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		if (!isDragAllowed()) {
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
		// Check if there is a PModelExport strategy defined
		PModelExport modelExport = getModelExport();
		if (modelExport == null) {
			// Do a naive check
			return naiveCanExport(srcModel, srcIndices);
		} else {
			// Delegate to the PModelExport
			return modelExport.canExport(srcModel, srcIndices);
		}
	}
	
	protected boolean naiveCanExport(PModel srcModel, List<PModelIndex> srcIndices) {
		return !isRemoveOnDrag() || srcModel.canRemove(srcIndices);
	}
	
	@Override
	public void startDrag(PComponent source, int x, int y)
			throws NullPointerException, IllegalArgumentException
	{
		ThrowException.ifFalse(canDrag(source, x, y), "canDrag(source, x, y) == false");
		ThrowException.ifNotNull(activeTransfer, "activeTransfer != null");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source,
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		
		PModel srcModel = srcComp.getModel();
		List<PModelIndex> srcIndices = srcComp.getDragIndices();
		
		PModelExport modelExport = getModelExport();
		PModel importData;
		// Check if there is a PModelExport strategy defined
		if (modelExport == null) {
			// Do a naive export as a PListModel with data in arbitrary order
			importData = buildNaiveExportModel(srcModel, srcIndices);
		} else {
			// Delegate export to PModelExport strategy
			importData = modelExport.createExportModel(srcModel, srcIndices);
		}
		PDnDIndicator dragIndicator = createIndicator(importData);
		
		activeTransfer = new ImmutablePDnDTransfer(source, x, y, importData, dragIndicator);
		
		source.getDragAndDropManager().startDrag(activeTransfer);
	}
	
	protected PModel buildNaiveExportModel(PModel srcModel, List<PModelIndex> srcIndices) {
		PModel exportModel = new DefaultPListModel();
		int importDataIdx = 0;
		for (PModelIndex index : srcIndices) {
			PListIndex importIndex = new PListIndex(importDataIdx++);
			exportModel.add(importIndex, srcModel.get(index));
		}
		return exportModel;
	}
	
	@Override
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
		
		// Check if there is a PModelExport strategy defined
		PModelExport modelExport = getModelExport();
		if (modelExport == null) {
			// Do a naive remove after drag
			if (isRemoveOnDrag()) {
				doNaiveRemoveAll(srcModel, srcIndices);
			}
		} else {
			// Delegate to PModelExport strategy
			getModelExport().finishExport(srcModel, transfer, srcIndices);
		}
	}
	
	protected void doNaiveRemoveAll(PModel srcModel, List<PModelIndex> srcIndices) {
		/*
		 * Iterating over all indices and removing them one by one will not always work
		 * since removing one index might change the validity of other indices. For this
		 * reason we let the model remove all indices as one atomic step.
		 */
		srcModel.removeAll(srcIndices);
	}
	
	@Override
	public void abortDrag(PComponent source, PDnDTransfer transfer)
			throws NullPointerException
	{
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(transfer, "transfer == null");
		ThrowException.ifNotEqual(transfer, activeTransfer, "transfer != activeTransfer");
		PDropComponent srcComp = ThrowException.ifTypeCastFails(source,
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		activeTransfer = null;
		
		// Check if there is a PModelExport strategy defined
		PModelExport modelExport = getModelExport();
		if (modelExport == null) {
			doNaiveAbortDrag(source, transfer);
		} else {
			// Delegate to PModelExport strategy
			PModel srcModel = srcComp.getModel();
			modelExport.abortExport(srcModel, transfer);
		}
	}
	
	protected void doNaiveAbortDrag(PComponent source, PDnDTransfer transfer) {
		// We have nothing to do here.
	}
	
	@Override
	public void showDropLocation(PComponent dropTarget, PDnDTransfer transfer, int x, int y) {
		ThrowException.ifNull(dropTarget, "dropTarget == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dropComp = ThrowException.ifTypeCastFails(dropTarget,
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		dropComp.setDropHighlight(dropIndex);
	}
	
	@Override
	public void hideDropLocation(PComponent dropTarget, PDnDTransfer transfer, int x, int y) {
		ThrowException.ifNull(dropTarget, "dropTarget == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dropComp = ThrowException.ifTypeCastFails(dropTarget,
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		dropComp.setDropHighlight(null);
	}
	
	protected PDnDIndicator createIndicator(PModel data) {
		Object imgIDAble = getDropPossibleImageID();
		Object imgIDUnable = getDropImpossibleImageID();
		return new DefaultPDndIndicator(imgIDAble, imgIDUnable);
	}
	
}