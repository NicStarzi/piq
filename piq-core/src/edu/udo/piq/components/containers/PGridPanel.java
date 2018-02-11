package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.tools.AbstractPContainer;

public class PGridPanel extends AbstractPContainer<GridConstraint> {
	
	public static final Object STYLE_ID = PPanel.STYLE_ID;
	{
		setStyleID(STYLE_ID);
	}
	
	public PGridPanel(int numberOfColumns, int numberOfRows) {
		super();
		setLayout(new PGridLayout(this, numberOfColumns, numberOfRows));
	}
	
	public PGridPanel(int numberOfColumns, int numberOfRows, PInsets insets) {
		this(numberOfColumns, numberOfRows);
		getLayout().setInsets(insets);
	}
	
	public PGridPanel(Growth[] columns, Growth[] rows) {
		this(columns.length, rows.length);
		for (int col = 0; col < columns.length; col++) {
			setColumnGrowth(col, columns[col]);
		}
		for (int row = 0; row < rows.length; row++) {
			setRowGrowth(row, rows[row]);
		}
	}
	
	public void addChild(PComponent component, String constraintAsString) {
		getLayout().addChild(component, constraintAsString);
	}
	
	@Override
	public PGridLayout getLayout() {
		return (PGridLayout) super.getLayout();
	}
	
	public void setInsets(PInsets value) {
		getLayout().setInsets(value);
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
	}
	
	public int getColumnCount() {
		return getLayout().getColumnCount();
	}
	
	public int getRowCount() {
		return getLayout().getRowCount();
	}
	
	public void setColumnGrowth(Growth value) {
		getLayout().setColumnGrowth(value);
	}
	
	public void setColumnGrowth(int colIndex, Growth value) {
		getLayout().setColumnGrowth(colIndex, value);
	}
	
	public Growth getColumnGrowth(int colIndex) {
		return getLayout().getColumnGrowth(colIndex);
	}
	
	public void setRowGrowth(Growth value) {
		getLayout().setRowGrowth(value);
	}
	
	public void setRowGrowth(int rowIndex, Growth value) {
		getLayout().setRowGrowth(rowIndex, value);
	}
	
	public Growth getRowGrowth(int rowIndex) {
		return getLayout().getRowGrowth(rowIndex);
	}
	
	public void setGapAfterColumn(int value) {
		getLayout().setGapAfterColumn(value);
	}
	
	public void setGapAfterColumn(int colIndex, int value) {
		getLayout().setGapAfterColumn(colIndex, value);
	}
	
	public int getGapAfterColumn(int colIndex) {
		return getLayout().getGapAfterColumn(colIndex);
	}
	
	public void setGapAfterRow(int value) {
		getLayout().setGapAfterRow(value);
	}
	
	public void setGapAfterRow(int rowIndex, int value) {
		getLayout().setGapAfterRow(rowIndex, value);
	}
	
	public int getGapAfterRow(int rowIndex) {
		return getLayout().getGapAfterRow(rowIndex);
	}
	
	public PComponent getCell(int cx, int cy) {
		return getLayout().getCell(cx, cy);
	}
	
}