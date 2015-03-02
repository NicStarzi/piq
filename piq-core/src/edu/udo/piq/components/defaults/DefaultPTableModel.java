package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.udo.piq.components.PTableModel;
import edu.udo.piq.components.util.PModelHistory;
import edu.udo.piq.tools.AbstractPTableModel;

public class DefaultPTableModel extends AbstractPTableModel implements PTableModel {
	
	private final List<Object[]> rows = new ArrayList<>();
	private int colCount;
	
	public DefaultPTableModel() {
		this(0, 0);
	}
	
	public DefaultPTableModel(int columnCount, int rowCount) {
		resize(columnCount, rowCount);
	}
	
	public void addRow(int rowIndex) {
		Object[] newRow = new Object[getColumnCount()];
		rows.add(rowIndex, newRow);
		fireRowAddedEvent(rowIndex);
	}
	
	public void removeRow(int rowIndex) {
		rows.remove(rowIndex);
		fireRowRemovedEvent(rowIndex);
	}
	
	public void addColumn(int columnIndex) {
		colCount += 1;
		int afterIndex = columnIndex + 1;
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			Object[] newRow = new Object[colCount];
			rows.set(i, newRow);
			System.out.println("row="+Arrays.toString(row));
			System.arraycopy(row, 0, newRow, 0, columnIndex);
			System.arraycopy(row, columnIndex, newRow, afterIndex, colCount - afterIndex);
			System.out.println("newRow="+Arrays.toString(newRow));
		}
		fireColumnAddedEvent(columnIndex);
	}
	
	public void removeColumn(int columnIndex) {
		colCount -= 1;
//		int beforeIndex = columnIndex - 1;
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			Object[] newRow = new Object[colCount];
			rows.set(i, newRow);
			System.out.println("row="+Arrays.toString(row));
			System.arraycopy(row, 0, newRow, 0, columnIndex);
			System.arraycopy(row, columnIndex + 1, newRow, columnIndex, colCount - columnIndex);
			System.out.println("newRow="+Arrays.toString(newRow));
		}
		fireColumnRemovedEvent(columnIndex);
	}
	
	public void resize(int newColumnCount, int newRowCount) {
		while (newColumnCount > getColumnCount()) {
			addColumn(getColumnCount());
		}
		while (newRowCount > getRowCount()) {
			addRow(getRowCount());
		}
	}
	
	public int getColumnCount() {
		return colCount;
	}
	
	public int getRowCount() {
		return rows.size();
	}
	
	public int getColumnIndexOf(Object cell) {
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			for (int j = 0; j < row.length; j++) {
				if (cell.equals(row[j])) {
					return j;
				}
			}
		}
		return -1;
	}
	
	public int getRowIndexOf(Object cell) {
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			for (int j = 0; j < row.length; j++) {
				if (cell.equals(row[j])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public Object getCell(int columnIndex, int rowIndex) {
		return rows.get(rowIndex)[columnIndex];
	}
	
	public boolean canAdd(Object cell, int columnIndex, int rowIndex) {
		return cell != null && columnIndex >= 0 && columnIndex < getColumnCount() 
				&& rowIndex >= 0 && rowIndex < getRowCount();
	}
	
	public void add(Object cell, int columnIndex, int rowIndex) {
		setCell(cell, columnIndex, rowIndex);
	}
	
	public boolean canRemove(int columnIndex, int rowIndex) {
		return columnIndex >= 0 && columnIndex < getColumnCount() 
				&& rowIndex >= 0 && rowIndex < getRowCount();
	}
	
	public void remove(int columnIndex, int rowIndex) {
		setCell(null, columnIndex, rowIndex);
	}
	
	protected void setCell(Object newValue, int columnIndex, int rowIndex) {
		Object[] row = rows.get(rowIndex);
		Object oldValue = row[columnIndex];
		if (oldValue != newValue) {
			row[columnIndex] = newValue;
			fireCellChangedEvent(columnIndex, rowIndex);
		}
	}
	
	public PModelHistory getHistory() {
		return null;
	}
	
	public String getDebugInfo() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < getRowCount(); r++) {
			Object[] row = rows.get(r);
			for (int c = 0; c < colCount; c++) {
				sb.append(row[c]);
				sb.append(", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}