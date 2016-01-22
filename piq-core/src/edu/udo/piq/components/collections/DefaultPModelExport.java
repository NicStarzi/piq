package edu.udo.piq.components.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;

public class DefaultPModelExport implements PModelExport {
	
	public boolean canExport(PModel src, List<PModelIndex> indices) {
		return false;
	}
	
	public void finishExport(PModel src, List<PModelIndex> indices) {
	}
	
	public PModel createExportModel(PModel src, List<PModelIndex> indices) {
//		if (src instanceof PTreeModel) {
//			return buildTreeModel(src, indices);
//		} else { // If PListModel or else
			return buildListModel(src, indices);
//		}
	}
	
	protected PTreeModel buildTreeModel(PModel dragModel, List<PModelIndex> dragIndices) {
		PTreeModel treeModel = new DefaultPTreeModel();
		
		PTreeIndex rootIndex = getCommonAncestor(dragIndices);
		treeModel.add(new PTreeIndex(), dragModel.get(rootIndex));
		
		PTreeIndex[] treeIndices = new PTreeIndex[dragIndices.size()];
		treeIndices = dragIndices.toArray(treeIndices);
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