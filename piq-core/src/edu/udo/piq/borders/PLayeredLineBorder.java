package edu.udo.piq.borders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.tools.MutablePInsets;
import edu.udo.piq.util.Throw;

public class PLayeredLineBorder extends AbstractPBorder {
	
	public static final List<PColor> DEFAULT_COLORS_TOP = 
			Collections.unmodifiableList(Arrays.asList(new PColor[] {
				PColor.BLACK, PColor.GREY50,
			}));
	public static final List<PColor> DEFAULT_COLORS_LEFT = DEFAULT_COLORS_TOP;
	public static final List<PColor> DEFAULT_COLORS_BOTTOM = 
			Collections.unmodifiableList(Arrays.asList(new PColor[] {
				PColor.GREY50, PColor.GREY875,
			}));
	public static final List<PColor> DEFAULT_COLORS_RIGHT = DEFAULT_COLORS_BOTTOM;
	
	public static final PInsets DEFAULT_PADDING = PInsets.ZERO_INSETS;
	public static final PInsets DEFAULT_MARGIN = PInsets.ZERO_INSETS;
	
	public static enum Side {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		;
		public static final List<PLayeredLineBorder.Side> ALL
			= Collections.unmodifiableList(Arrays.asList(PLayeredLineBorder.Side.values()));
		public static final int COUNT = ALL.size();
		
		public final int IDX = ordinal();
		public boolean isHorizontal() {
			return this == TOP || this == BOTTOM;
		}
		public boolean isExtended() {
			return this == BOTTOM || this == RIGHT;
		}
	}
	
	protected final MutablePInsets insets = new MutablePInsets();
	protected final MutablePInsets insetsPadding = new MutablePInsets();
	protected final MutablePInsets insetsMargin = new MutablePInsets();
	protected final MutablePBounds tmpBounds = new MutablePBounds();
	protected final PColor[][] colors = new PColor[Side.COUNT][0];
	
	public PLayeredLineBorder() {
		setColors(Side.TOP, DEFAULT_COLORS_TOP);
		setColors(Side.BOTTOM, DEFAULT_COLORS_BOTTOM);
		setColors(Side.LEFT, DEFAULT_COLORS_LEFT);
		setColors(Side.RIGHT, DEFAULT_COLORS_RIGHT);
		setPadding(DEFAULT_PADDING);
		setMargin(DEFAULT_MARGIN);
	}
	
	public void setColors(Side side, Collection<PColor> values) {
		setColors(side, values.toArray(new PColor[values.size()]));
	}
	
	public void setColors(Side side, PColor ... values) {
		Throw.ifNull(side, "side == null");
		Throw.ifNull(values, "values == null");
		PColor[] old = colors[side.IDX];
		if (Arrays.equals(values, old)) {
			return;
		}
		colors[side.IDX] = Arrays.copyOf(values, values.length);
		if (old.length != values.length) {
			fireInsetsChangedEvent();
		}
		fireReRenderEvent();
	}
	
	public PColor[] getColors(Side side) {
		return colors[side.IDX];
	}
	
	public void setMargin(PInsets value) {
		insetsMargin.set(value);
		fireInsetsChangedEvent();
	}
	
	public PInsets getMargin() {
		return insetsMargin;
	}
	
	public void setPadding(PInsets value) {
		insetsPadding.set(value);
		fireInsetsChangedEvent();
	}
	
	public PInsets getPadding() {
		return insetsPadding;
	}
	
	@Override
	public PInsets getDefaultInsets(PComponent component) {
		insets.set(
				getColors(Side.TOP).length, 
				getColors(Side.BOTTOM).length, 
				getColors(Side.LEFT).length, 
				getColors(Side.RIGHT).length
		);
		insets.add(getPadding());
		insets.add(getMargin());
		return insets;
	}
	
	@Override
	public void defaultRender(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBounds();
		PInsets margin = getMargin();
		tmpBounds.set(bnds.getX() + margin.getFromLeft(), 
				bnds.getY() + margin.getFromTop(), 
				bnds.getWidth() - margin.getWidth(), 
				bnds.getHeight() - margin.getHeight());
		
		renderer.setRenderMode(renderer.getRenderModeFill());
		for (int lvl = 0, count = 1; count > 0; lvl++) {
			count = 0;
			for (Side side : Side.ALL) {
				PColor[] layers = getColors(side);
				if (layers.length > lvl) {
					count++;
					
					renderer.setColor(layers[lvl]);
					switch (side) {
					case TOP:
						renderer.strokeTop(tmpBounds);
						tmpBounds.setY(tmpBounds.getY() + 1);
						tmpBounds.setHeight(tmpBounds.getHeight() - 1);
						break;
					case BOTTOM:
						renderer.strokeBottom(tmpBounds);
						tmpBounds.setHeight(tmpBounds.getHeight() - 1);
						break;
					case LEFT:
						renderer.strokeLeft(tmpBounds);
						tmpBounds.setX(tmpBounds.getX() + 1);
						tmpBounds.setWidth(tmpBounds.getWidth() - 1);
						break;
					case RIGHT:
						renderer.strokeRight(tmpBounds);
						tmpBounds.setWidth(tmpBounds.getWidth() - 1);
						break;
					default:
						Throw.always(() -> "side == "+side);
					}
				}
			}
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return getPadding().isEmpty() && getMargin().isEmpty();
	}
	
}