package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PTableLayout2 extends AbstractPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	private Constraint[] table;
	private int cols;
	private int rows;
	
	public PTableLayout2(PComponent component) {
		super(component);
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint cnstr = (Constraint) constraint;
				cnstr.comp = child;
				table[cnstr.col + cnstr.row * cols] = cnstr;
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint cnstr = (Constraint) constraint;
				cnstr.comp = null;
				table[cnstr.col + cnstr.row * cols] = null;
			}
		});
	}
	
	public void setSize(int columns, int rows) {
		clearChildren();
		this.cols = columns;
		this.rows = rows;
		table = new Constraint[columns * rows];
	}
	
	public int getColumnCount() {
		return cols;
	}
	
	public int getRowCount() {
		return rows;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint 
				&& getChildForConstraint(constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		
		int compY = y;
		for (int r = 0; r < rows; r++) {
			int compX = x;
			int lineH = 0;
			for (int c = 0; c < cols; c++) {
				PComponent child = table[c + r * rows].comp;
				PSize childPrefSize = getPreferredSizeOf(child);
				int w = childPrefSize.getWidth();
				int h = childPrefSize.getHeight();
				setChildBounds(child, compX, compY, w, h);
				
				compX += w;
				if (lineH < h) {
					lineH = h;
				}
			}
			compY += lineH;
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = 0;
		int prefH = 0;
		for (int y = 0; y < rows; y++) {
			int lineW = 0;
			for (int x = 0; x < cols; x++) {
				PComponent child = table[x + y * rows].comp;
				PSize childPrefSize = getPreferredSizeOf(child);
				lineW += childPrefSize.getWidth();
				prefH += childPrefSize.getHeight();
			}
			if (prefW < lineW) {
				prefW = lineW;
			}
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static class Constraint {
		final int col;
		final int row;
		PComponent comp;
		
		public Constraint(int columnIndex, int rowIndex) {
			col = columnIndex;
			row = rowIndex;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + row;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Constraint other = (Constraint) obj;
			return col == other.col && row == other.row;
		}
	}
	
}