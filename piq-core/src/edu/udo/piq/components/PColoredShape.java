package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ThrowException;

public class PColoredShape extends AbstractPComponent {
	
	public static final Shape DEFAULT_SHAPE = Shape.RECTANGLE;
	public static final PColor DEFAULT_COLOR = PColor.BLACK;
	public static final PSize DEFAULT_SIZE = new ImmutablePSize(100, 100);
	
	private PColor color = DEFAULT_COLOR;
	private Shape shape = DEFAULT_SHAPE;
	private PSize prefSize = DEFAULT_SIZE;
	
	public PColoredShape() {
		this(DEFAULT_SHAPE, DEFAULT_COLOR);
	}
	
	public PColoredShape(PColor color) {
		this(DEFAULT_SHAPE, color);
	}
	
	public PColoredShape(Shape shape) {
		this(shape, DEFAULT_COLOR);
	}
	
	public PColoredShape(Shape shape, PColor color) {
		super();
		setShape(shape);
		setColor(color);
	}
	
	public void setShape(Shape value) {
		ThrowException.ifNull(value, "value == null");
		shape = value;
		fireReRenderEvent();
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void setColor(PColor value) {
		ThrowException.ifNull(value, "value == null");
		color = value;
		fireReRenderEvent();
	}
	
	public PColor getColor() {
		return color;
	}
	
	public void setSize(PSize size) {
		ThrowException.ifNull(size, "size == null");
		prefSize = size;
		firePreferredSizeChangedEvent();
	}
	
	public PSize getSize() {
		return prefSize;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(getColor());
		getShape().render(renderer, getBounds());
	}
	
	@Override
	protected PSize getConstantDefaultPreferredSize() {
		return prefSize;
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return getShape() == Shape.RECTANGLE;
	}
	
	public static enum Shape {
		RECTANGLE {
			@Override
			public void render(PRenderer renderer, PBounds bounds) {
				renderer.drawQuad(bounds);
			}
		},
		CIRCLE {
			@Override
			public void render(PRenderer renderer, PBounds bounds) {
				int x = bounds.getX();
				int y = bounds.getY();
				int w = bounds.getWidth();
				int h = bounds.getHeight();
				renderer.drawEllipse(x, y, w, h);
			}
		},
		TRIANGLE_DOWN {
			@Override
			public void render(PRenderer renderer, PBounds bounds) {
				int x = bounds.getX();
				int y = bounds.getY();
				int fx = bounds.getFinalX();
				int fy = bounds.getFinalY();
				renderer.drawTriangle(x, y, fx, y, x + (fx - x) / 2, fy);
			}
		},
		TRIANGLE_UP {
			@Override
			public void render(PRenderer renderer, PBounds bounds) {
				int x = bounds.getX();
				int y = bounds.getY();
				int fx = bounds.getFinalX();
				int fy = bounds.getFinalY();
				renderer.drawTriangle(x + (fx - x) / 2, y, x, fy, fx, fy);
			}
		},
		;
		
		public abstract void render(PRenderer renderer, PBounds bounds);
		
	}
	
}