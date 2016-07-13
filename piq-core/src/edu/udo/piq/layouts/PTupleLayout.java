package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractEnumPLayout;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ThrowException;

public class PTupleLayout extends AbstractEnumPLayout<Constraint> {
	
	public static final PInsets			DEFAULT_INSETS			= PInsets.ZERO_INSETS;
	public static final Orientation		DEFAULT_ORIENTATION		= Orientation.LEFT_TO_RIGHT;
	public static final Distribution	DEFAULT_DISTRIBUTION	= Distribution.RESPECT_BOTH;
	public static final int				DEFAULT_GAP				= 4;
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize	= new MutablePSize();
	protected PInsets insets				= DEFAULT_INSETS;
	protected Orientation orientation		= DEFAULT_ORIENTATION;
	protected Distribution distPrim			= DEFAULT_DISTRIBUTION;
	protected Distribution distScnd			= DEFAULT_DISTRIBUTION;
	protected int gap						= DEFAULT_GAP;
	
	private final LayoutData dataX = new LayoutData();
	private final LayoutData dataY = new LayoutData();
	
	public PTupleLayout(PComponent owner) {
		super(owner, Constraint.class);
	}
	
	public void setInsets(PInsets value) {
		insets = value;
	}
	
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new IllegalArgumentException();
		}
		this.orientation = orientation;
		invalidate();
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setDistribution(Distribution value) {
		distPrim = value;
		invalidate();
	}
	
	public Distribution getDistribution() {
		return distPrim;
	}
	
	public void setSecondaryDistribution(Distribution value) {
		distScnd = value;
		invalidate();
	}
	
	public Distribution getSecondaryDistribution() {
		return distScnd;
	}
	
	public void setPInsets(PInsets value) {
		insets = value;
		invalidate();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public void setGap(int value) {
		gap = value;
		invalidate();
	}
	
	public int getGap() {
		return gap;
	}
	
	public PComponent getFirst() {
		return getChildForConstraint(Constraint.FIRST);
	}
	
	public PComponent getSecond() {
		return getChildForConstraint(Constraint.SECOND);
	}
	
	public void layOut() {
		Distribution distPrim = getDistribution();
		Distribution distScnd = this.distScnd;
		PComponent first = getFirst();
		PComponent second = getSecond();
		int gap;
		if (first == null || second == null) {
			gap = 0;
		} else {
			gap = getGap();
		}
		
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBounds();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromRight();
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		PSize prefSizeFirst = getPreferredSizeOf(first);
		PSize prefSizeSecond = getPreferredSizeOf(second);
		int prefFirstW = prefSizeFirst.getWidth();
		int prefFirstH = prefSizeFirst.getHeight();
		int prefSecondW = prefSizeSecond.getWidth();
		int prefSecondH = prefSizeSecond.getHeight();
		
		dataX.sizeTotal = w;
		dataX.size1 = min(prefFirstW, dataX.sizeTotal);
		dataX.size2 = min(prefSecondW, dataX.sizeTotal);
		dataX.pos1 = x;
		dataX.gap = gap;
		dataY.sizeTotal = h;
		dataY.size1 = min(prefFirstH, dataY.sizeTotal);
		dataY.size2 = min(prefSecondH, dataY.sizeTotal);
		dataY.pos1 = y;
		dataY.gap = gap;
		LayoutData dataPrim;
		LayoutData dataScnd;
		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
			dataPrim = dataX;
			dataScnd = dataY;
		} else {
			dataPrim = dataY;
			dataScnd = dataX;
		}
		distPrim = distPrim.suggestDelegate(dataPrim);
		distPrim.transformPrimary(dataPrim);
		distScnd.transformSecondary(dataScnd);
		
		int primPos2 = dataPrim.pos1 + dataPrim.size1 + dataPrim.gap;
		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
			int scndPos1 = y + dataScnd.pos1 - dataScnd.size1 / 2;
			int scndPos2 = y + dataScnd.pos1 - dataScnd.size2 / 2;
			setChildBounds(first, dataPrim.pos1, scndPos1, dataPrim.size1, dataScnd.size1);
			setChildBounds(second, primPos2, scndPos2, dataPrim.size2, dataScnd.size2);
		} else {
			int scndPos1 = x + dataScnd.pos1 - dataScnd.size1 / 2;
			int scndPos2 = x + dataScnd.pos1 - dataScnd.size2 / 2;
			setChildBounds(first, scndPos1, dataPrim.pos1, dataScnd.size1, dataPrim.size1);
			setChildBounds(second, scndPos2, primPos2, dataScnd.size2, dataPrim.size2);
		}
	}
	
	public PSize getPreferredSize() {
		PComponent first = getChildForConstraint(Constraint.FIRST);
		PComponent second = getChildForConstraint(Constraint.SECOND);
		PSize sizeFirst = getPreferredSizeOf(first);
		PSize sizeSecond = getPreferredSizeOf(second);
		
		PInsets insets = getInsets();
		int prefW = insets.getHorizontal();
		int prefH = insets.getVertical();
		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
			prefW += sizeFirst.getWidth();
			prefW += sizeSecond.getWidth();
			if (first != null && second != null) {
				prefW += gap;
			}
			prefH += max(sizeFirst.getHeight(), sizeSecond.getHeight());
		} else {
			prefW += max(sizeFirst.getWidth(), sizeSecond.getWidth());
			prefH += sizeFirst.getHeight();
			prefH += sizeSecond.getHeight();
			if (first != null && second != null) {
				prefH += gap;
			}
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		boolean primRespect = getDistribution() != Distribution.RESPECT_NONE;
		boolean scndRespect = getSecondaryDistribution() != Distribution.RESPECT_NONE;
		if (primRespect || scndRespect) {
			invalidate();
		}
	}
	
	public static enum Constraint {
		FIRST,
		SECOND;
	}
	
	public static enum Orientation {
		LEFT_TO_RIGHT,
		TOP_TO_BOTTOM,
		;
	}
	
	protected static class LayoutData {
		public int pos1;
		public int size1;
		public int size2;
		public int sizeTotal;
		public int gap;
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{pos1=");
			builder.append(pos1);
			builder.append(", size1=");
			builder.append(size1);
			builder.append(", size2=");
			builder.append(size2);
			builder.append(", sizeTotal=");
			builder.append(sizeTotal);
			builder.append("}");
			return builder.toString();
		}
	}
	
	public static enum Distribution {
		RESPECT_BOTH {
			protected void transformPrimary(LayoutData data) {
				int sizePref = data.size1 + data.size2 + data.gap;
				data.pos1 = data.pos1 + data.sizeTotal / 2 - sizePref / 2;
			}
			protected void transformSecondary(LayoutData data) {
//				int sizeMax = Math.max(data.size1, data.size2);
				data.pos1 = data.sizeTotal / 2;// - sizeMax / 2;
			}
			protected Distribution suggestDelegate(LayoutData data) {
				if (data.size1 + data.gap + data.size2 >= data.sizeTotal) 
				{
					return Distribution.RESPECT_NONE;
				}
				return this;
			}
		},
		RESPECT_NONE {
			protected void transformPrimary(LayoutData data) {
				int sizePref = data.size1 + data.size2;
				double percentFirst = data.size1 / (double) sizePref;
				data.size1 = (int) Math.ceil(data.sizeTotal * percentFirst);
				data.size2 = data.sizeTotal - (data.size1 + data.gap);
			}
			protected void transformSecondary(LayoutData data) {
				data.size1 = data.size2 = data.sizeTotal;
				data.pos1 = data.sizeTotal / 2;// - data.size1 / 2
			}
			protected Distribution suggestDelegate(LayoutData data) {
				return this;
			}
		},
		RESPECT_FIRST {
			protected void transformPrimary(LayoutData data) {
				data.size2 = data.sizeTotal - (data.size1 + data.gap);
			}
			protected void transformSecondary(LayoutData data) {
				data.size2 = data.size1;
				data.pos1 = data.sizeTotal / 2;// - data.size1 / 2
			}
			protected Distribution suggestDelegate(LayoutData data) {
				return this;
			}
		},
		RESPECT_SECOND {
			protected void transformPrimary(LayoutData data) {
				data.size1 = data.sizeTotal - (data.size2 + data.gap);
			}
			protected void transformSecondary(LayoutData data) {
				data.size1 = data.size2;
				data.pos1 = data.sizeTotal / 2;// - data.size1 / 2
			}
			protected Distribution suggestDelegate(LayoutData data) {
				return this;
			}
		},
		;
		
		protected abstract void transformPrimary(LayoutData data);
		
		protected abstract void transformSecondary(LayoutData data);
		
		protected abstract Distribution suggestDelegate(LayoutData data);
	}
	
}