package edu.udo.piq.components.defaults;

import java.util.List;

import edu.udo.piq.PDnDSupport;
import edu.udo.piq.components.collections.PDropComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.components.collections.PTableModel;

public class PTablePDnDSupport extends DefaultPDnDSupport implements PDnDSupport {
	
	// This special element can be detected by the PTablePDnDSupport when dropping
	public static final Object EXPORT_MODEL_EMPTY_CELL = new Object() {
		@Override
		public String toString() {
			return "PTableDnDSupportExportEmptyCell";
		}
	};
	
	@Override
	protected boolean naiveCanImport(PModel dstModel, PModelIndex dstIndex, PModel importData) {
		PTableCellIndex dstCellIdx = (PTableCellIndex) dstIndex;
		int dstCol = dstCellIdx.getColumn();
		int dstRow = dstCellIdx.getRow();
		
		PTableModel tableDstModel = (PTableModel) dstModel;
		PTableModel tableSrcModel = (PTableModel) importData;
		int countCols = tableSrcModel.getColumnCount();
		int countRows = tableSrcModel.getRowCount();
		for (int col = 0; col < countCols; col++) {
			for (int row = 0; row < countRows; row++) {
				Object element = tableSrcModel.get(col, row);
				if (element != EXPORT_MODEL_EMPTY_CELL
						&& !tableDstModel.canSet(dstCol + col, dstRow + row, element))
				{
					return false;
				}
			}
		}
		
		for (PModelIndex index : importData) {
			PTableCellIndex cellIdx = (PTableCellIndex) index;
			PTableCellIndex testSetIdx = new PTableCellIndex(
					dstCol + cellIdx.getColumn(), dstRow + cellIdx.getRow());
			
			Object element = importData.get(index);
			if (element != EXPORT_MODEL_EMPTY_CELL
					&& !dstModel.canSet(testSetIdx, element))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected void doNaiveImport(PDropComponent dstComp,
			PModel dstModel, PModelIndex dstIndex, PModel importData)
	{
		if (!(importData instanceof PTableModel)
				|| !(dstIndex instanceof PTableCellIndex))
		{
			super.doNaiveImport(dstComp, dstModel, dstIndex, importData);
		}
		PTableModel tableSrcModel = (PTableModel) importData;
		PTableModel tableDstModel = (PTableModel) dstModel;
		PTableCellIndex dstCellIdx = (PTableCellIndex) dstIndex;
		int dstCol = dstCellIdx.getColumn();
		int dstRow = dstCellIdx.getRow();
		
		PSelection selection = dstComp.getSelection();
		selection.clearSelection();
		
		int countCols = tableSrcModel.getColumnCount();
		int countRows = tableSrcModel.getRowCount();
		for (int col = 0; col < countCols; col++) {
			for (int row = 0; row < countRows; row++) {
				Object element = tableSrcModel.get(col, row);
				if (element == EXPORT_MODEL_EMPTY_CELL) {
					continue;
				}
				PModelIndex dstIdx = new PTableCellIndex(col + dstCol, row + dstRow);
				tableDstModel.set(dstIdx, element);
				selection.addSelection(dstIdx);
			}
		}
	}
	
	@Override
	protected boolean naiveCanExport(PModel srcModel, List<PModelIndex> srcIndices) {
		if (!isRemoveOnDrag()) {
			return true;
		}
		for (PModelIndex index : srcIndices) {
			if (!srcModel.canSet(index, null)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected PModel buildNaiveExportModel(PModel srcModel, List<PModelIndex> srcIndices) {
		if (srcIndices.size() == 0) {
			return super.buildNaiveExportModel(srcModel, srcIndices);
		}
		
		PTableModel tableModel = (PTableModel) srcModel;
		int minCol = Integer.MAX_VALUE;
		int maxCol = 0;
		int minRow = Integer.MAX_VALUE;
		int maxRow = 0;
		for (PModelIndex index : srcIndices) {
			PTableCellIndex cellIdx = (PTableCellIndex) index;
			int cellCol = cellIdx.getColumn();
			int cellRow = cellIdx.getRow();
			if (cellCol < minCol) {
				minCol = cellCol;
			}
			if (cellCol > maxCol) {
				maxCol = cellCol;
			}
			if (cellRow < minRow) {
				minRow = cellRow;
			}
			if (cellRow > maxRow) {
				maxRow = cellRow;
			}
		}
		int countCols = maxCol - minCol + 1;
		int countRows = maxRow - minRow + 1;
		
		PTableModel exportModel = new DefaultPTableModel(countCols, countRows);
		for (int col = 0; col < countCols; col++) {
			for (int row = 0; row < countRows; row++) {
				PTableCellIndex srcIdx = new PTableCellIndex(minCol + col, minRow + row);
				Object element;
				if (srcIndices.contains(srcIdx)) {
					element = tableModel.get(srcIdx);
				} else {
					// We use this object to specify a nonexistent index.
					element = EXPORT_MODEL_EMPTY_CELL;
				}
				exportModel.set(col, row, element);
			}
		}
		
		return exportModel;
	}
	
	@Override
	public void doNaiveRemoveAll(PModel srcModel, List<PModelIndex> srcIndices) {
		/*
		 * Instead of removing indices we set them to null in a PTable since
		 * only entire rows or columns can be removed.
		 */
		for (PModelIndex srcIndex : srcIndices) {
			srcModel.set(srcIndex, null);
		}
	}
	
}