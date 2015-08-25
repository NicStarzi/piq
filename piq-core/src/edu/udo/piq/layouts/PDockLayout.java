package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PDockLayout extends AbstractPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	protected final List<List<PComponent>> rows = new ArrayList<>();
	protected PInsets insets = new ImmutablePInsets(4);
	protected int gap;
	
	public PDockLayout(PComponent component) {
		super(component);
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				addToRow(child, constr);
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				removeFromRow(child, constr);
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
		PLayoutDesign design = getDesign();
		if (design == null) {
			return insets;
		}
		Object maybeInsets = getDesign().getAttribute(ATTRIBUTE_KEY_INSETS);
		if (maybeInsets != null && maybeInsets instanceof PInsets) {
			return (PInsets) maybeInsets;
		}
		return insets;
	}
	
	public void setGap(int value) {
		gap = value;
		fireInvalidateEvent();
	}
	
	public int getGap() {
		return gap;
	}
	
	private void addToRow(PComponent comp, Constraint constr) {
		int x = constr.x;
		int y = constr.y;
		while (rows.size() <= y) {
			rows.add(new ArrayList<>());
		}
		List<PComponent> row = rows.get(y);
		while (row.size() <= x) {
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
		PInsets insets = getInsets();
		int gap = getGap();
		int ox = ob.getX() + insets.getFromLeft();
		int oy = ob.getY() + insets.getFromRight();
//		int ow = ob.getWidth() - insets.getHorizontal();
//		int oh = ob.getHeight() - insets.getVertical();
		
		int cx;
		int cy = oy;
		for (int y = 0; y < rows.size(); y++) {
			cx = ox;
			List<PComponent> row = rows.get(y);
			int rowH = 0;
			
			for (int x = 0; x < row.size(); x++) {
				PComponent comp = row.get(x);
				if (comp != null) {
					PSize compPrefSize = getPreferredSizeOf(comp);
					int compW = compPrefSize.getWidth();
					int compH = compPrefSize.getHeight();
					setChildBounds(comp, cx, cy, compW, compH);
					cx += compW + gap;
					if (rowH < compH) {
						rowH = compH;
					}
				}
			}
			cy += rowH;
			if (rowH > 0) {
				cy += gap;
			}
		}
//		// alternative
//		int[] allColPrefH = new int[rows.size()];
//		boolean[] colGrowH = new boolean[rows.size()];
//		for (int y = 0; y < rows.size(); y++) {
//			List<PComponent> row = rows.get(y);
//			int colPrefH = 0;
//			
//			for (int x = 0; x < row.size(); x++) {
//				PComponent comp = row.get(x);
//				if (comp != null) {
//					PSize compPrefSize = getPreferredSizeOf(comp);
////					int compW = compPrefSize.getWidth();
//					int compH = compPrefSize.getHeight();
//					if (compH > colPrefH) {
//						colPrefH = compH;
//					}
//					
//					Constraint constr = null;
//					colGrowH[y] |= constr.growVertical();
//				}
//			}
//			allColPrefH[y] = colPrefH;
//		}
	}
	
	public PSize getPreferredSize() {
		int gap = getGap();
		int maxW = 0;
		int prefH = 0;
		for (int y = 0; y < rows.size(); y++) {
			List<PComponent> row = rows.get(y);
			int rowW = 0;
			int rowH = 0;
			for (int x = 0; x < row.size(); x++) {
				PComponent comp = row.get(x);
				if (comp != null) {
					if (comp instanceof PPanel) {
						throw new RuntimeException("comp="+comp);
					}
					PSize compPrefSize = getPreferredSizeOf(comp);
					int compW = compPrefSize.getWidth();
					int compH = compPrefSize.getHeight();
					if (x != row.size() - 1) {
						compW += gap;
						compH += gap;
					}
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
		PInsets insets = getInsets();
		prefSize.setWidth(maxW + insets.getHorizontal());
		prefSize.setHeight(prefH + insets.getVertical());
		return prefSize;
	}
	
	public static class Constraint {
		
		final int x, y;
		final Growth growth;
		
		public Constraint(int x, int y) {
			this(x, y, Growth.GROW_NONE);
		}
		
		public Constraint(int x, int y, Growth growth) {
			this.x = x;
			this.y = y;
			this.growth = growth;
		}
		
		protected boolean growHorizontal() {
			return growth == Growth.GROW_X || growth == Growth.GROW_BOTH;
		}
		
		protected boolean growVertical() {
			return growth == Growth.GROW_Y || growth == Growth.GROW_BOTH;
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
	
	public static enum Growth {
		GROW_X, GROW_Y, GROW_BOTH, GROW_NONE;
	}
	
}