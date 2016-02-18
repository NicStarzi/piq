package edu.udo.piq.components.defaults;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import edu.udo.piq.PDnDSupport;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;

import static edu.udo.piq.components.collections.PTreeIndex.ROOT;;

public class PTreePDnDSupport extends DefaultPDnDSupport implements PDnDSupport {
	
	public static final Object EXPORT_MODEL_ROOT_CONTENT = new Object() {
		public String toString() {
			return "PTreeDnDSupportExportRoot";
		};
	};
	
	protected void doNaiveImport(PModel dstModel, PModelIndex dstIndex, PModel importData) {
		if (importData instanceof PTreeModel) {
			PTreeModel treeImportData = (PTreeModel) importData;
			PTreeIndex treeDstIndex = (PTreeIndex) dstIndex;
			if (treeImportData.getRoot() == EXPORT_MODEL_ROOT_CONTENT) {
				
				Deque<PTreeIndex> stack = new ArrayDeque<>();
				for (int i = 0; i < treeImportData.getChildCount(ROOT); i++) {
					stack.addLast(ROOT.append(i));
					
					while (!stack.isEmpty()) {
						PTreeIndex exportIndex = stack.pop();
						PTreeIndex importIndex = treeDstIndex.append(exportIndex, 1);
						dstModel.add(importIndex, importData.get(exportIndex));
						
						for (int j = 0; j < treeImportData.getChildCount(exportIndex); j++) {
							stack.addLast(exportIndex.append(j));
						}
					}
				}
				return;
			}
		}
		super.doNaiveImport(dstModel, dstIndex, importData);
	}
	
	protected PModel buildNaiveExportModel(PModel srcModel, List<PModelIndex> srcIndices) {
		if (srcIndices.size() == 0) {
			return super.buildNaiveExportModel(srcModel, srcIndices);
		}
		
		// Filter out all descendants
		if (srcIndices.size() > 1) {
			PTreeIndex[] filterChildren = srcIndices.toArray(new PTreeIndex[srcIndices.size()]);
			Arrays.sort(filterChildren, PTreeIndex.TREE_INDEX_DEPTH_COMPARATOR);
			for (int i = 0; i < filterChildren.length - 1; i++) {
				PTreeIndex root = filterChildren[i];
				if (root == null) {
					continue;
				}
				for (int j = i + 1; j < filterChildren.length; j++) {
					PTreeIndex maybeDescendant = filterChildren[j];
					if (root.isAncestorOf(maybeDescendant)) {
						filterChildren[j] = null;
					}
				}
			}
			srcIndices = Arrays.asList(filterChildren);
		}
		
		PTreeModel exportModel = new DefaultPTreeModel();
		// We need a special root that can be detected when dropping
		exportModel.add(PTreeIndex.ROOT, EXPORT_MODEL_ROOT_CONTENT);
		
		PTreeModel treeModel = (PTreeModel) srcModel;
		Deque<StackElement> stack = new ArrayDeque<>();
		
		// This will be used to determine the index position behind the root
		int idx = 0;
		for (PModelIndex index : srcIndices) {
			PTreeIndex treeIndex = (PTreeIndex) index;
			// Index can be null if it was filtered out before
			if (treeIndex == null) {// || indices.contains(treeIndex)
				continue;
			}
			
			stack.addLast(new StackElement(treeIndex, new PTreeIndex(idx++)));
			while (!stack.isEmpty()) {
				StackElement current = stack.removeFirst();
				exportModel.add(current.expIndex, treeModel.get(current.srcIndex));
				for (int j = 0; j < treeModel.getChildCount(current.srcIndex); j++) {
					PTreeIndex childSrcIndex = current.srcIndex.append(j);
					PTreeIndex childExpIndex = current.expIndex.append(j);
					stack.addLast(new StackElement(childSrcIndex, childExpIndex));
				}
			}
		}
		return exportModel;
	}
	
	protected static class StackElement {
		PTreeIndex srcIndex;
		PTreeIndex expIndex;
		
		public StackElement(PTreeIndex sourceIndex, PTreeIndex exportIndex) {
			srcIndex = sourceIndex;
			expIndex = exportIndex;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(srcIndex);
			sb.append(" => ");
			sb.append(expIndex);
			return sb.toString();
		}
	}
	
}