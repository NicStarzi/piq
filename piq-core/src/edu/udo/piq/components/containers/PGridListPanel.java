package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PGridListLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPContainer;

public class PGridListPanel extends AbstractPContainer<PTableCellIndex> {
	
	public PGridListPanel(int numberOfColumns) {
		super();
		setLayout(new PGridListLayout(this, numberOfColumns));
	}
	
	public PGridListPanel(ListAlignment alignment, int numberOfColumns) {
		this(alignment, PListLayout.DEFAULT_INSETS, numberOfColumns);
	}
	
	public PGridListPanel(PInsets insets, int numberOfColumns) {
		this(PListLayout.DEFAULT_ALIGNMENT, insets, numberOfColumns);
	}
	
	public PGridListPanel(ListAlignment alignment, PInsets insets, int numberOfColumns) {
		this(numberOfColumns);
		getLayout().setListAlignment(alignment);
		getLayout().setInsets(insets);
	}
	
	public PGridListLayout getLayout() {
		return (PGridListLayout) super.getLayout();
	}
	
	public void addChild(PComponent component) {
		super.addChild(component, null);
	}
	
	public int getColumnCount() {
		return getLayout().getColumnCount();
	}
	
	public int getRowCount() {
		return getLayout().getRowCount();
	}
	
	public void setGapBetweenColumns(int value) {
		getLayout().setGapBetweenColumns(value);
	}
	
	public int getGapBetweenColumns() {
		return getLayout().getGapBetweenColumns();
	}
	
	public void setGapBetweenRows(int value) {
		getLayout().setGapBetweenRows(value);
	}
	
	public int getGapBetweenRows() {
		return getLayout().getGapBetweenRows();
	}
	
	public void setAllColumnAlignmentX(AlignmentX value) {
		getLayout().setColumnAlignmentX(value);
	}
	
	public void setColumnAlignmentX(int columnIdx, AlignmentX value) {
		getLayout().setColumnAlignmentX(columnIdx, value);
	}
	
	public AlignmentX getColumnAlignmentX(int columnIdx) {
		return getLayout().getColumnAlignmentX(columnIdx);
	}
	
	public void setAllColumnAlignmentY(AlignmentY value) {
		getLayout().setColumnAlignmentY(value);
	}
	
	public void setColumnAlignmentY(int columnIdx, AlignmentY value) {
		getLayout().setColumnAlignmentY(columnIdx, value);
	}
	
	public AlignmentY getColumnAlignmentY(int columnIdx) {
		return getLayout().getColumnAlignmentY(columnIdx);
	}
	
	public void setAllColumnGrowth(Growth value) {
		getLayout().setColumnGrowth(value);
	}
	
	public void setColumnGrowth(int columnIdx, Growth value) {
		getLayout().setColumnGrowth(columnIdx, value);
	}
	
	public Growth getColumnGrowth(int columnIdx) {
		return getLayout().getColumnGrowth(columnIdx);
	}
	
	public void setListAlignment(ListAlignment value) {
		getLayout().setListAlignment(value);
	}
	
	public ListAlignment getAlignment() {
		return getLayout().getListAlignment();
	}
	
	public void setInsets(PInsets value) {
		getLayout().setInsets(value);
	}
	
	public PInsets getInsets() {
		return getLayout().getInsets();
	}
	
}