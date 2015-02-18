package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PDockLayout extends AbstractPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	protected final List<List<PComponent>> rows = new ArrayList<>();
	protected PInsets insets = new ImmutablePInsets(4);
	
	public PDockLayout(PComponent component) {
		super(component);
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				addToRow(component, constr);
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				removeFromRow(component, constr);
			}
		});
	}
	
	public void setInsets(PInsets value) {
		if (value == null) {
			throw new IllegalArgumentException("insets="+value);
		}
		insets = new ImmutablePInsets(value);
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	private void addToRow(PComponent comp, Constraint constr) {
		int x = constr.x;
		int y = constr.y;
		while (rows.size() < y) {
			rows.add(new ArrayList<>());
		}
		List<PComponent> row = rows.get(y);
		while (row.size() < x) {
			row.add(null);
		}
		row.set(x, comp);
	}
	
	private void removeFromRow(PComponent comp, Constraint constr) {
		int x = constr.x;
		int y = constr.y;
		rows.get(y).set(x, null);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint 
				&& getChildForConstraint(constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
	}
	
	public PSize getPreferredSize() {
		int maxW = 0;
		int prefH = 0;
		for (int y = 0; y < rows.size(); y++) {
			List<PComponent> row = rows.get(y);
			int rowW = 0;
			int rowH = 0;
			for (int x = 0; x < row.size(); x++) {
				PComponent comp = row.get(x);
				if (comp != null) {
					PSize compPrefSize = getPreferredSizeOf(comp);
					int compW = compPrefSize.getWidth();
					int compH = compPrefSize.getHeight();
					rowW += compW;
					if (rowH < compH) {
						rowH = compH;
					}
				}
			}
			if (maxW < rowW) {
				maxW = rowW;
			}
			prefH += rowH;
		}
		prefSize.setWidth(maxW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static class Constraint {
		int x, y;
		
		public Constraint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Constraint other = (Constraint) obj;
			return x == other.x && y == other.y;
		}
	}
	
}