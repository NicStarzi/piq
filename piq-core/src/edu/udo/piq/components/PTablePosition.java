package edu.udo.piq.components;

public class PTablePosition {
	
	private final PTableModel model;
	protected final int columnIndex;
	protected final int rowIndex;
	
	public PTablePosition(PTableModel model, int columnIndex, int rowIndex) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (columnIndex < 0 || rowIndex < 0) {
			throw new IllegalArgumentException("columnIndex="+columnIndex+", rowIndex="+rowIndex);
		}
		this.model = model;
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
	}
	
	public PTablePosition(PTableModel model, Object cell) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		}
		this.model = model;
		this.columnIndex = model.getColumnIndexOf(cell);
		this.rowIndex = model.getRowIndexOf(cell);
	}
	
	public PTableModel getModel() {
		return model;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public Object getCell() {
		return getModel().getCell(getColumnIndex(), getRowIndex());
	}
	
	public PTablePosition getAdjacent(int offsetColumn, int offsetRow) {
		return new PTablePosition(getModel(), 
				getColumnIndex() + offsetColumn, 
				getRowIndex() + offsetRow);
	}
	
	public boolean canAdd(Object cell) {
		return getModel().canAdd(cell, getColumnIndex(), getRowIndex());
	}
	
	public void add(Object cell) {
		getModel().add(cell, getColumnIndex(), getRowIndex());
	}
	
	public boolean canRemove() {
		return getModel().canRemove(getColumnIndex(), getRowIndex());
	}
	
	public void remove() {
		getModel().remove(getColumnIndex(), getRowIndex());
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getColumnIndex();
		result = prime * result + getRowIndex();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof PTablePosition)) {
			return false;
		}
		PTablePosition other = (PTablePosition) obj;
		return getColumnIndex() == other.getColumnIndex() && getRowIndex() == other.getRowIndex();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [column=");
		builder.append(getColumnIndex());
		builder.append(", row=");
		builder.append(getRowIndex());
		builder.append("]");
		return builder.toString();
	}
	
}