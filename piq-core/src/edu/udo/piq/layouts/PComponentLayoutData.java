package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.util.ThrowException;

public class PComponentLayoutData {
	
	protected final LayoutPBounds cellBnds = new LayoutPBounds();
	protected final LayoutPBounds compBnds = new LayoutPBounds();
	protected final PComponent comp;
	protected Object constr;
	
	public PComponentLayoutData(PComponent component, Object constraint) {
		ThrowException.ifNull(component, "component == null");
		comp = component;
		constr = constraint;
	}
	
	public PBounds getCellBounds() {
		return cellBnds;
	}
	
	public PBounds getComponentBounds() {
		return compBnds;
	}
	
	public PComponent getComponent() {
		return comp;
	}
	
	protected void setConstr(Object value) {
		constr = value;
	}
	
	public Object getConstraint() {
		return constr;
	}
	
	protected void setCellBySize(int x, int y, int w, int h, AlignmentX alignX, AlignmentY alignY) {
//		System.out.println(this+"\n\t.setCellBySize() alignX="+alignX+"; alignY="+alignY);
		cellBnds.set(x, y, w, h);
		
		PLayoutPreference layoutPref = comp.getLayoutPreference();
		PSize prefSize = comp.getPreferredSize();
		int prefW = prefSize.getWidth();
		int prefH = prefSize.getHeight();
		
		if (alignX == AlignmentX.PREFERRED_OR_CENTER) {
			alignX = layoutPref.getAlignmentX();
		}
		if (alignY == AlignmentY.PREFERRED_OR_CENTER) {
			alignY = layoutPref.getAlignmentY();
		}
//		System.out.println("layoutPref="+layoutPref+"; alignX="+alignX+"; alignY="+alignY);
		PInsets margin = layoutPref.getMargin();
		x += margin.getFromLeft();
		y += margin.getFromTop();
		w = Math.max(0, w - margin.getWidth());
		h = Math.max(0, h - margin.getHeight());
		
		int bndsX = alignX.getLeftX(x, w, prefW);
		int bndsW = alignX.getWidth(x, w, prefW);
		int bndsY = alignY.getTopY(y, h, prefH);
		int bndsH = alignY.getHeight(y, h, prefH);
		
		compBnds.set(bndsX, bndsY, bndsW, bndsH);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [component=");
		sb.append(getComponent());
		sb.append(", constraint=");
		sb.append(getConstraint());
		sb.append(", cell bounds=");
		sb.append(getCellBounds());
		sb.append(", bounds=");
		sb.append(getComponentBounds());
		sb.append("]");
		return sb.toString();
	}
	
	private static class LayoutPBounds extends AbstractPBounds implements PBounds {
		
		protected int x;
		protected int y;
		protected int w;
		protected int h;
		
		protected void set(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		@Override
		public int getX() {
			return x;
		}
		
		@Override
		public int getY() {
			return y;
		}
		
		@Override
		public int getWidth() {
			return w;
		}
		
		@Override
		public int getHeight() {
			return h;
		}
		
	}
	
}