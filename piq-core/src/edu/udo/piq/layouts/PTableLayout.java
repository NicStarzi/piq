package edu.udo.piq.layouts;
//
//import java.util.Arrays;
//
//import edu.udo.piq.PBounds;
//import edu.udo.piq.PComponent;
//import edu.udo.piq.PReadOnlyLayout;
//import edu.udo.piq.PLayoutObs;
//import edu.udo.piq.PSize;
//import edu.udo.piq.components.PTablePosition;
//import edu.udo.piq.tools.AbstractPLayout;
//import edu.udo.piq.tools.MutablePSize;
//
public class PTableLayout {// extends AbstractPLayout {
}
//	
//	protected static final int DEFAULT_COLUMN_SIZE = 150;
//	protected static final int DEFAULT_ROW_SIZE = 20;
//	protected static final int DEFAULT_GAP = 1;
//	
//	protected final MutablePSize prefSize;
//	private PComponent[] content;
//	private int[] colSizes;
//	private int[] rowSizes;
//	private int defaultColSize;
//	private int defaultRowSize;
//	private int gap = DEFAULT_GAP;
//	
//	public PTableLayout(PComponent component) {
//		this(component, DEFAULT_COLUMN_SIZE, DEFAULT_ROW_SIZE);
//	}
//	
//	public PTableLayout(PComponent component, int defaultColumnSize, int defaultRowSize) {
//		super(component);
//		this.defaultColSize = defaultColumnSize;
//		this.defaultRowSize = defaultRowSize;
//		prefSize = new MutablePSize();
//		content = new PComponent[0];
//		colSizes = new int[0];
//		rowSizes = new int[0];
//		
//		addObs(new PLayoutObs() {
//			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
//				PTablePosition cell = (PTablePosition) constraint;
//				setContent(cell, child);
//			}
//			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
//				PTablePosition cell = (PTablePosition) constraint;
//				setContent(cell, null);
//			}
//		});
//	}
//	
//	public void resize(int columns, int rows) {
//		PComponent[] oldContent = content;
//		int oldColCount = getColumnCount();
//		int oldRowCount = getRowCount();
//		
//		colSizes = Arrays.copyOf(colSizes, columns);
//		for (int col = oldColCount; col < getColumnCount(); col++) {
//			colSizes[col] = defaultColSize;
//		}
//		rowSizes = Arrays.copyOf(rowSizes, rows);
//		for (int row = oldRowCount; row < getRowCount(); row++) {
//			rowSizes[row] = defaultRowSize;
//		}
//		content = new PComponent[columns * rows];
//		
//		for (int col = 0; col < oldColCount; col++) {
//			for (int row = 0; row < oldRowCount; row++) {
//				int oldID = col + row * oldColCount;
//				int newID = col + row * columns;
//				
//				content[newID] = oldContent[oldID];
//			}
//		}
//	}
//	
//	private void setContent(PTablePosition cell, PComponent comp) {
//		int col = cell.getColumnIndex();
//		int row = cell.getRowIndex();
//		int id = col + row * getColumnCount();
//		content[id] = comp;
//	}
//	
//	public void setGap(int value) {
//		if (value < 0) {
//			throw new IllegalArgumentException("value="+value);
//		}
//		gap = value;
//		fireInvalidateEvent();
//	}
//	
//	public int getGap() {
//		return gap;
//	}
//	
//	public int getColumnCount() {
//		return colSizes.length;
//	}
//	
//	public int getRowCount() {
//		return rowSizes.length;
//	}
//	
//	public void setDefaultColumnSize(int size) {
//		if (size < 0) {
//			size = 0;
//		}
//		defaultColSize = size;
//	}
//	
//	public void setColumnSize(int columnIndex, int size) {
//		if (size < 0) {
//			size = 0;
//		}
//		colSizes[columnIndex] = size;
//		fireInvalidateEvent();
//	}
//	
//	public int getColumnSize(int columnIndex) {
//		return colSizes[columnIndex];
//	}
//	
//	public void setDefaultRowSize(int size) {
//		if (size < 0) {
//			size = 0;
//		}
//		defaultRowSize = size;
//	}
//	
//	public void setRowSize(int rowIndex, int size) {
//		if (size < 0) {
//			size = 0;
//		}
//		rowSizes[rowIndex] = size;
//		fireInvalidateEvent();
//	}
//	
//	public int getRowSize(int rowIndex) {
//		return rowSizes[rowIndex];
//	}
//	
//	public boolean isValid(PTablePosition cell) {
//		return isValid(cell.getColumnIndex(), cell.getRowIndex());
//	}
//	
//	public boolean isValid(int col, int row) {
//		return col >= 0 && col < getColumnCount() 
//				&& row >= 0 && row < getRowCount();
//	}
//	
//	protected boolean canAdd(PComponent component, Object constraint) {
//		return constraint != null && constraint instanceof PTablePosition
//				&& isValid((PTablePosition) constraint);
//	}
//	
//	public void layOut() {
//		PBounds ob = getOwner().getBounds();
//		int x = ob.getX();
//		int y = ob.getY();
////		int fx = ob.getFinalX();
////		int fy = ob.getFinalY();
//		
//		int colCount = getColumnCount();
//		int rowCount = getRowCount();
//		
//		int cellX = x;
//		int cellY = y;
//		for (int col = 0; col < colCount; col++) {
//			int cellW = getColumnSize(col);
////			if (col == colCount - 1) {
////				cellW = fx - cellX;
////			}
//			
//			for (int row = 0; row < rowCount; row++) {
//				int cellH = getRowSize(row);
////				if (row == rowCount - 1) {
////					cellH = fy - cellY;
////				}
//				
//				PComponent child = content[col + row * colCount];
//				
//				if (child != null) {
//					setChildBounds(child, cellX, cellY, cellW, cellH);
//				}
//				
//				cellY += cellH + gap;
//			}
//			cellX += cellW + gap;
//			cellY = y;
//		}
//	}
//	
//	public PSize getPreferredSize() {
//		int prefW = 0;
//		for (int col = 0; col < colSizes.length; col++) {
//			prefW += colSizes[col] + gap;
//		}
//		if (colSizes.length > 0) {
//			prefW -= gap;
//		}
//		int prefH = 0;
//		for (int row = 0; row < rowSizes.length; row++) {
//			prefH += rowSizes[row] + gap;
//		}
//		if (rowSizes.length > 0) {
//			prefH -= gap;
//		}
//		prefSize.setWidth(prefW);
//		prefSize.setHeight(prefH);
//		return prefSize;
//	}
//	
//}