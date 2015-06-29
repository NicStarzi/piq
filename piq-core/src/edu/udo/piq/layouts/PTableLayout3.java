package edu.udo.piq.layouts;

import java.util.Arrays;

import edu.udo.cs.util.ResizableTable;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.comps.selectcomps.DefaultPCellComponent;
import edu.udo.piq.comps.selectcomps.PTableIndex;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PTableLayout3 extends AbstractPLayout {
	
	public static final int DEFAULT_COLUMN_WIDTH = 50;
	
	protected final MutablePSize prefSize = new MutablePSize();
	private final ResizableTable<PComponent> table = new ResizableTable<>(0, 0);
	private int[]		colWidths	= new int[0];
	private boolean[]	colWidthSet	= new boolean[0];
	private int cellGapW = 1;
	private int cellGapH = 1;
	
	public PTableLayout3(PComponent component) {
		super(component);
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, 
					PComponent child, Object constraint) 
			{
				DefaultPCellComponent cell = (DefaultPCellComponent) child;
				System.out.println("PTableLayout3.childAdded cnstr="+constraint+", child="+cell.getElement());
				PTableIndex index = (PTableIndex) constraint;
				int col = index.getColumn();
				if (!colWidthSet[col]) {
					int childW = getPreferredSizeOf(child).getWidth();
					System.out.println("check col width="+childW);
					if (colWidths[col] < childW) {
						System.out.println("set col width="+childW);
						colWidths[col] = childW;
					}
				}
				table.set(col, index.getRow(), child);
			}
			public void childRemoved(PReadOnlyLayout layout, 
					PComponent child, Object constraint) 
			{
				System.out.println("PTableLayout3.childRemoved cnstr="+constraint+", child="+child);
				PTableIndex index = (PTableIndex) constraint;
				table.set(index.getColumn(), index.getRow(), null);
			}
		});
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint != null && constraint instanceof PTableIndex) {
			PTableIndex ti = (PTableIndex) constraint;
			return ti.getColumn() >= 0 && ti.getColumn() < getColumnCount() 
					&& ti.getRow() >= 0 && ti.getRow() < getRowCount()
					&& getChildForConstraint(constraint) == null;
		}
		return false;
	}
	
	public void setColumnWidth(int index, int value) {
		colWidths[index] = value;
		colWidthSet[index] = true;
		fireInvalidateEvent();
	}
	
	public int getColumnWidth(int index) {
		return colWidths[index];
	}
	
	public void unsetColumnWidth(int index) {
		colWidthSet[index] = false;
		colWidths[index] = DEFAULT_COLUMN_WIDTH;
		for (int r = 0; r < getRowCount(); r++) {
			PComponent child = table.get(index, r);
			int childW = getPreferredSizeOf(child).getWidth();
			if (colWidths[index] < childW) {
				colWidths[index] = childW;
			}
		}
		fireInvalidateEvent();
	}
	
	public void addColumn() {
		addColumn(getColumnCount());
	}
	
	public void addColumn(int index) {
		table.addColumn(index);
		int colCount = table.getColumnCount();
		int[] newColWidths = Arrays.copyOf(colWidths, colCount);
		boolean[] newColWidthSet = Arrays.copyOf(colWidthSet, colCount);
		System.arraycopy(colWidths, index, newColWidths, index + 1, colWidths.length - index);
		System.arraycopy(colWidthSet, index, newColWidthSet, index + 1, colWidthSet.length - index);
		newColWidths[index] = DEFAULT_COLUMN_WIDTH;
		newColWidthSet[index] = false;
		colWidths = newColWidths;
		colWidthSet = newColWidthSet;
		fireInvalidateEvent();
	}
	
	public void addRow() {
		addRow(getRowCount());
	}
	
	public void addRow(int index) {
		table.addRow(index);
		fireInvalidateEvent();
	}
	
	public void removeAllColumnsAndRows() {
		clearChildren();
		table.setSize(0, 0);
		colWidths = new int[0];
		colWidthSet = new boolean[0];
		fireInvalidateEvent();
	}
	
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	public int getCellGapWidth() {
		return cellGapW;
	}
	
	public int getCellGapHeight() {
		return cellGapH;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		
		int compX = x;
		int compY = y;
		for (int r = 0; r < getRowCount(); r++) {
			int rowH = 0;
			for (int c = 0; c < getColumnCount(); c++) {
				PComponent child = table.get(c, r);
				PSize childPrefSize = getPreferredSizeOf(child);
				int childW = colWidths[c];
				int childH = childPrefSize.getHeight();
				
				setChildBounds(child, compX, compY, childW, childH);
				if (rowH < childH) {
					rowH = childH;
				}
				compX += childW + cellGapW;
			}
			compX = x;
			compY += rowH + cellGapH;
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = cellGapW * (getColumnCount() - 1);
		for (int c = 0; c < getColumnCount(); c++) {
			prefW += colWidths[c];
		}
		int prefH = cellGapH * (getRowCount() - 1);
		for (int r = 0; r < getRowCount(); r++) {
			int colH = 0;
			for (int c = 0; c < getColumnCount(); c++) {
				PComponent child = table.get(c, r);
				PSize childPrefSize = getPreferredSizeOf(child);
				int childH = childPrefSize.getHeight();
				colH += childH;
			}
			if (prefH < colH) {
				prefH = colH;
			}
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
}