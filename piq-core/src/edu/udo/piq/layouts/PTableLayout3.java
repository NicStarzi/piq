package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.comps.selectcomps.PTableIndex;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PTableLayout3 extends AbstractPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	private int cols;
	private int rows;
	
	public PTableLayout3(PComponent component) {
		super(component);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof PTableIndex 
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
	
}