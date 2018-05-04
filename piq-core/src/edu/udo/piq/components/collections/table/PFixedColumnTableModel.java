package edu.udo.piq.components.collections.table;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.components.collections.PModelIndex;

public class PFixedColumnTableModel extends AbstractPTableModel {
	
	public static final int DEFAULT_ROW_CAPACITY = 10;
	
	private final List<Object[]> cells;
	private final int colCount;
	
	public PFixedColumnTableModel(int columnCount) {
		this(columnCount, DEFAULT_ROW_CAPACITY);
	}
	
	public PFixedColumnTableModel(int columnCount, int rowCapacity) {
		cells = new ArrayList<>(rowCapacity);
		colCount = columnCount;
	}
	
	@Override
	public int getColumnCount() {
		return colCount;
	}
	
	@Override
	public int getRowCount() {
		return cells.size();
	}
	
	@Override
	public void set(int column, int row, Object content) {
		cells.get(row)[column] = content;
	}
	
	@Override
	public Object get(int column, int row) {
		return cells.get(row)[column];
	}
	
	public void addRow(Object ... elements) {
		
	}
	
	@Override
	public void add(PModelIndex index, Object content) {
		
	}
	
	@Override
	public void remove(PModelIndex index) {
		
	}
	
}