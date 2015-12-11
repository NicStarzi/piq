package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.collections.PDropComponent;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelImport;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;
import edu.udo.piq.tools.ImmutablePDnDTransfer;
import edu.udo.piq.util.ThrowException;

public class DefaultPDnDSupport implements PDnDSupport {
	
	private final Map<Class<? extends PModel>, PModelImport> importMap = new HashMap<>();
	private PDnDTransfer activeTransfer;
	private boolean dragAllowed = true;
	private boolean dropAllowed = true;
	private boolean removeOnDrag = true;
	
	public void setModelImport(
			Class<? extends PModel> modelClass, 
			PModelImport modelImport) 
	{
		ThrowException.ifNull(modelClass, "modelClass == null");
		ThrowException.ifNull(modelImport, "modelImport == null");
		importMap.put(modelClass, modelImport);
	}
	
	@SuppressWarnings("unchecked")
	public PModelImport getModelImport(Class<? extends PModel> modelClass) {
		ThrowException.ifNull(modelClass, "modelClass == null");
		PModelImport modelImport = importMap.get(modelClass);
		while (modelImport == null) {
			if (modelClass == PModel.class) {
				return null;
			}
			modelClass = (Class<? extends PModel>) modelClass.getSuperclass();
			modelImport = importMap.get(modelClass);
		}
		return modelImport;
	}
	
	public PModelImport getModelImport(PModel model) {
		ThrowException.ifNull(model, "model == null");
		return getModelImport(model.getClass());
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
		ThrowException.ifNull(target, "target == null");
		ThrowException.ifNull(transfer, "transfer == null");
		PDropComponent dropComp = ThrowException.ifTypeCastFails(target, 
				PDropComponent.class, "target instanceof PDropComponent == false");
		if (!isDropAllowed()) {
			return false;
		}
		PModel data = transfer.getData();
		PModelImport dataImport = getModelImport(data);
		if (dataImport == null) {
			return false;
		}
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
//		for (PModelIndex index : data) {
//			Object content = data.get(index);
//			if (!model.canAdd(dropIndex, content)) {
//				return false;
//			}
//		}
		
//		List<IndexAndContentTuple> dataList = transfer.getData();
//		for (IndexAndContentTuple tuple : dataList) {
//			if (!model.canAdd(dropIndex, tuple.getContent())) {
//				return false;
//			}
//		}
		return dataImport.canImportData(model, dropIndex, data);
	}
	
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		ThrowException.ifFalse(canDrop(target, transfer, x, y), "canDrop(target, transfer, x, y) == false");
		PDropComponent dropComp = (PDropComponent) target;
		PModel model = dropComp.getModel();
		PModelIndex dropIndex = dropComp.getDropIndex(x, y);
		
		PModel data = transfer.getData();
		PModelImport dataImport = getModelImport(data);
		dataImport.importData(model, dropIndex, data);
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
		PDropComponent dragComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		if (!isDragAllowed()) {
			return false;
		}
		// If the root does not support drag and drop or if there is no root to begin with
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		PModel model = dragComp.getModel();
		if (model == null) {
			return false;
		}
		List<PModelIndex> dragIndices = dragComp.getDragIndices();
		if (dragIndices == null || dragIndices.size() == 0) {
			return false;
		}
		if (isRemoveOnDrag()) {
			for (PModelIndex dragIndex : dragIndices) {
				if (!dragIndex.canRemove(model)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void startDrag(PComponent source, int x, int y)
			throws NullPointerException, IllegalArgumentException 
	{
		ThrowException.ifFalse(canDrag(source, x, y), "canDrag(source, x, y) == false");
		ThrowException.ifNotNull(activeTransfer, "activeTransfer != null");
		
		PDropComponent dragComp = (PDropComponent) source;
		PModel dragModel = dragComp.getModel();
		List<PModelIndex> dragIndices = dragComp.getDragIndices();
		
		PModel data = createDragDataModel(dragModel, dragIndices);
		PComponent dragPres = createVisibleRepresentation(data);
		
//		List<IndexAndContentTuple> data = new ArrayList<>();
//		for (PModelIndex dragIndex : dragIndices) {
//			data.add(new IndexAndContentTuple(model.get(dragIndex), dragIndex));
////			data.add(model.get(dragIndex));
//		}
		activeTransfer = new ImmutablePDnDTransfer(source, x, y, data, dragPres);
		
		source.getDragAndDropManager().startDrag(activeTransfer);
	}
	
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) 
			throws NullPointerException, IllegalArgumentException 
	{
		ThrowException.ifNull(source, "source == null");
		ThrowException.ifNull(target, "target == null");
		ThrowException.ifNull(transfer, "transfer == null");
		ThrowException.ifNotEqual(transfer, activeTransfer, "transfer != activeTransfer");
		PDropComponent dragComp = ThrowException.ifTypeCastFails(source, 
				PDropComponent.class, "(source instanceof PDropComponent) == false");
		
//		PModel dragData = activeTransfer.getData();
		activeTransfer = null;
		
		if (isRemoveOnDrag()) {
			PModel model = dragComp.getModel();
//			Iterator<PModelIndex> iterator = dragData.removeIterator();
//			while (iterator.hasNext()) {
//				PModelIndex dragIndex = iterator.next();
//				model.remove(dragIndex.withOffset(offset));
//			}
			List<PModelIndex> dragIndices = dragComp.getDragIndices();
			while (!dragIndices.isEmpty()) {
				PModelIndex dragIndex = dragIndices.remove(0);
				model.remove(dragIndex);
			}
		}
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
	
	//List<IndexAndContentTuple> data
	protected PComponent createVisibleRepresentation(PModel data) {
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("DragAndDrop.png");
		pic.setStretchToSize(true);
		pic.setElusive(true);
		return pic;
	}
	
	protected PModel createDragDataModel(PModel dragModel, List<PModelIndex> dragIndices) {
		if (dragModel instanceof PTreeModel) {
			return buildTreeModel(dragModel, dragIndices);
		} else { // If PListModel or else
			return buildListModel(dragModel, dragIndices);
		}
	}
	
	protected PTreeModel buildTreeModel(PModel dragModel, List<PModelIndex> dragIndices) {
		PTreeModel treeModel = new DefaultPTreeModel();
		
		PTreeIndex rootIndex = getCommonAncestor(dragIndices);
		treeModel.add(new PTreeIndex(), dragModel.get(rootIndex));
		
		PTreeIndex[] treeIndices = new PTreeIndex[dragIndices.size()];
		dragIndices.toArray(treeIndices);
		Arrays.sort(treeIndices, 
				(t1, t2) -> Integer.compare(t1.getDepth(), t2.getDepth())
		);
		for (PTreeIndex treeIndex : treeIndices) {
			if (treeIndex != rootIndex) {
				
			}
		}
		return treeModel;
	}
	
	protected PTreeIndex getCommonAncestor(List<PModelIndex> dragIndices) {
		Iterator<PModelIndex> iter = dragIndices.iterator();
		PTreeIndex commonAncestor = (PTreeIndex) iter.next();
		while (iter.hasNext()) {
			PTreeIndex current = (PTreeIndex) iter.next();
			commonAncestor = commonAncestor.createCommonAncestorIndex(current);
		}
		return commonAncestor;
	}
	
	protected PListModel buildListModel(PModel dragModel, List<PModelIndex> dragIndices) {
		PListModel listModel = new DefaultPListModel();
		int listIndex = 0;
		for (PModelIndex dragIndex : dragIndices) {
			Object content = dragModel.get(dragIndex);
			listModel.add(listIndex++, content);
		}
		return listModel;
	}
	
}