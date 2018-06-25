package edu.udo.piq.components.collections.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import edu.udo.piq.components.collections.PModelIndex;

public class PListTableModel<ELEM_T> extends AbstractPTableModel {
	
	public static final int DEFAULT_CAPACITY = 10;
	
	private final List<ELEM_T> elems;
	private final List<Function<ELEM_T, Object>> getters;
	
	public PListTableModel(Collection<Function<ELEM_T, Object>> columns) {
		this(DEFAULT_CAPACITY, columns);
	}
	
	public PListTableModel(Function<ELEM_T, Object> ... columns) {
		this(DEFAULT_CAPACITY, Arrays.asList(columns));
	}
	
	public PListTableModel(int rowCapacity, Collection<Function<ELEM_T, Object>> columns) {
		elems = new ArrayList<>(rowCapacity);
		getters = new ArrayList<>(columns);
	}
	
	@Override
	public int getColumnCount() {
		return getters.size();
	}
	
	@Override
	public int getRowCount() {
		return elems.size();
	}
	
	@Override
	public void set(int column, int row, Object content) {
//		cells.get(row)[column] = content;
	}
	
	@Override
	public Object get(int column, int row) {
		Function<ELEM_T, Object> getFunc = getters.get(column);
		ELEM_T elem = elems.get(row);
		return getFunc.apply(elem);
	}
	
	public void add(ELEM_T content) {
		add(new PRowIndex(getRowCount()), content);
	}
	
	@Override
	public void add(PModelIndex index, Object content) {
		
	}
	
	@Override
	public void remove(PModelIndex index) {
		
	}
	
}