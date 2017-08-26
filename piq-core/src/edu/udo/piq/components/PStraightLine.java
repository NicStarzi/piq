package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ThrowException;

public class PStraightLine extends AbstractPComponent {
	
	public static final LineOrientation DEFAULT_ORIENTATION = LineOrientation.HORIZONTAL;
	public static final int DEFAULT_LINE_THICKNESS = 1;
	
	private LineOrientation orientation = DEFAULT_ORIENTATION;
	private int lineThickness = DEFAULT_LINE_THICKNESS;
	
	public PStraightLine() {
		this(DEFAULT_ORIENTATION, DEFAULT_LINE_THICKNESS);
	}
	
	public PStraightLine(LineOrientation orientation) {
		this(orientation, DEFAULT_LINE_THICKNESS);
	}
	
	public PStraightLine(LineOrientation orientation, int lineThickness) {
		super();
		setOrientation(orientation);
		setLineThickness(lineThickness);
	}
	
	public void setLineThickness(int value) {
		ThrowException.ifLess(1, value, "value < 1");
		if (getLineThickness() != value) {
			lineThickness = value;
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	}
	
	public int getLineThickness() {
		return lineThickness;
	}
	
	public void setOrientation(LineOrientation value) {
		ThrowException.ifNull(value, "value == null");
		if (getOrientation() != value) {
			orientation = value;
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	}
	
	public LineOrientation getOrientation() {
		return orientation;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		
		int lineThickness = getLineThickness();
		LineOrientation orientation = getOrientation();
		int startX = orientation.getStartX(bnds, lineThickness);
		int startY = orientation.getStartY(bnds, lineThickness);
		int finalX = orientation.getFinalX(bnds, lineThickness);
		int finalY = orientation.getFinalY(bnds, lineThickness);
		
		renderer.setRenderMode(renderer.getRenderModeFill());
		renderer.setColor(PColor.GREY50);
		renderer.drawLine(startX, startY, finalX, finalY, lineThickness / 2);
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		int lineThickness = getLineThickness();
		LineOrientation orientation = getOrientation();
		int prefW = orientation.getPreferredWidth(lineThickness);
		int prefH = orientation.getPreferredHeight(lineThickness);
		prefSize.set(prefW, prefH);
		return prefSize;
	}
	
	public static enum LineOrientation {
		HORIZONTAL {
			@Override
			public int getStartX(PBounds bnds, int lineThickness) {
				return bnds.getX();
			}
			@Override
			public int getStartY(PBounds bnds, int lineThickness) {
				return bnds.getY() + bnds.getHeight() / 2 - lineThickness / 2;
			}
			@Override
			public int getFinalX(PBounds bnds, int lineThickness) {
				return bnds.getFinalX();
			}
			@Override
			public int getFinalY(PBounds bnds, int lineThickness) {
				return getStartY(bnds, lineThickness);
			}
			@Override
			public int getPreferredWidth(int lineThickness) {
				return 0;
			}
			@Override
			public int getPreferredHeight(int lineThickness) {
				return lineThickness + PADDING;
			}
		},
		VERTICAL {
			@Override
			public int getStartX(PBounds bnds, int lineThickness) {
				return bnds.getX() + bnds.getWidth() / 2 - lineThickness / 2;
			}
			@Override
			public int getStartY(PBounds bnds, int lineThickness) {
				return bnds.getY();
			}
			@Override
			public int getFinalX(PBounds bnds, int lineThickness) {
				return getStartX(bnds, lineThickness);
			}
			@Override
			public int getFinalY(PBounds bnds, int lineThickness) {
				return bnds.getFinalY();
			}
			@Override
			public int getPreferredWidth(int lineThickness) {
				return lineThickness + PADDING;
			}
			@Override
			public int getPreferredHeight(int lineThickness) {
				return 0;
			}
		},
		;
		private static final int PADDING = 4;
		
		public abstract int getStartX(PBounds bnds, int lineThickness);
		
		public abstract int getStartY(PBounds bnds, int lineThickness);
		
		public abstract int getFinalX(PBounds bnds, int lineThickness);
		
		public abstract int getFinalY(PBounds bnds, int lineThickness);
		
		public abstract int getPreferredWidth(int lineThickness);
		
		public abstract int getPreferredHeight(int lineThickness);
		
	}
	
}