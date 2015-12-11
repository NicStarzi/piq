package edu.udo.piq.components.collections;

public class DefaultPModelImportListToTree implements PModelImport {
	
	public boolean canImportData(PModel dst, PModelIndex dstIndex, PModel src) {
		for (PModelIndex srcIndex : src) {
			if (!dst.canAdd(dstIndex, src.get(srcIndex))) {
				return false;
			}
		}
		return true;
	}
	
	public void importData(PModel dst, PModelIndex dstIndex, PModel src) {
		PTreeIndex dstTreeIndex = (PTreeIndex) dstIndex;
		for (PModelIndex srcIndex : src) {
			Object content = src.get(srcIndex);
			dst.add(dstTreeIndex, content);
			
			int depth = dstTreeIndex.getDepth();
			int newChildPos = dstTreeIndex.getLastIndex() + 1;
			dstTreeIndex = dstTreeIndex.replaceIndex(depth, newChildPos);
		}
	}
	
}