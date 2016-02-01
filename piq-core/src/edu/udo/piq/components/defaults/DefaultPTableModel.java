package edu.udo.piq.components.defaults;
//package edu.udo.piq.comps.selectcomps;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
public class DefaultPTableModel {}
//public class DefaultPTableModel extends AbstractPModel implements PTableModel {
//	
//	private final List<Object[]> rows = new ArrayList<>();
//	private int colCount;
//	
//	public Object get(PModelIndex index) {
//		PTableIndex tableIndex = asTableIndex(index);
//		return get(tableIndex.getColumn(), tableIndex.getRow());
//	}
//	
//	public boolean contains(PModelIndex index) {
//		PTableIndex tableIndex = asTableIndex(index);
//		return contains(tableIndex.getColumn(), tableIndex.getRow());
//	}
//	
//	public PTableIndex getIndexOf(Object content) {
//		for (int colIndex = 0; colIndex < rows.size(); colIndex++) {
//			Object[] row = rows.get(colIndex);
//			for (int rowIndex = 0; rowIndex < row.length; rowIndex++) {
//				if (content.equals(row[rowIndex])) {
//					return new PTableIndex(colIndex, rowIndex);
//				}
//			}
//		}
//		return null;
//	}
//	
//	public boolean canAdd(PModelIndex index, Object content) {
//		PTableIndex tableIndex = asTableIndex(index);
//		return canAdd(tableIndex.getColumn(), tableIndex.getRow(), content);
//	}
//	
//	public void add(PModelIndex index, Object content) {
//		PTableIndex tableIndex = asTableIndex(index);
//		add(tableIndex.getColumn(), tableIndex.getRow(), content);
//	}
//	
//	public boolean canRemove(PModelIndex index) {
//		PTableIndex tableIndex = asTableIndex(index);
//		return canRemove(tableIndex.getColumn(), tableIndex.getRow());
//	}
//	
//	public void remove(PModelIndex index) {
//		PTableIndex tableIndex = asTableIndex(index);
//		remove(tableIndex.getColumn(), tableIndex.getRow());
//	}
//	
//	public Iterator<PModelIndex> iterator() {
//		return null;
//	}
//	
//	public int getColumnCount() {
//		return colCount;
//	}
//	
//	public int getRowCount() {
//		return rows.size();
//	}
//	
//	public Object get(int column, int row) {
//		return rows.get(row)[column];
//	}
//	
//	public boolean contains(int column, int row) {
//		return column >= 0 && column < getColumnCount() 
//				&& row >= 0 && row < getRowCount();
//	}
//	
//	public boolean canAdd(int column, int row, Object content) {
//		return content != null && contains(column, row);
//	}
//	
//	public void add(int column, int row, Object content) {
//		Object oldContent = get(column, row);
//		set(column, row, content);
//		if (oldContent == null) {
//			fireAddEvent(new PTableIndex(column, row), content);
//		} else {
//			fireChangeEvent(new PTableIndex(column, row), content);
//		}
//	}
//	
//	public boolean canRemove(int column, int row) {
//		return contains(column, row);
//	}
//	
//	public void remove(int column, int row) {
//		Object oldContent = get(column, row);
//		if (oldContent != null) {
//			set(column, row, null);
//			fireRemoveEvent(new PTableIndex(column, row), oldContent);
//		}
//	}
//	
//	protected void set(int column, int row, Object content) {
//		Object[] rowArr = rows.get(row);
//		Object oldValue = rowArr[column];
//		if (oldValue != content) {
//			rowArr[column] = content;
//		}
//	}
//	
//}