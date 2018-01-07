package edu.udo.piq.borders;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.MutablePInsets;
import edu.udo.piq.util.PModelFactory;

public class PTitleBorder extends AbstractPBorder {
	
	public static final Object FONT_ID = new SymbolicFontKey(PTitleBorder.class);
	
	protected final PSingleValueModelObs modelObs = this::onModelValueChanged;
	protected final MutablePInsets insets = new MutablePInsets();
	protected PTextModel model;
	protected Orientation orientation = Orientation.TOP_LEFT;
	protected int lineSize = 1;
	protected int padding = 0;
	
	public PTitleBorder() {
		this(1, (Object) null);
	}
	
	public PTitleBorder(Object defaultValue) {
		this(Orientation.TOP_LEFT, 1, defaultValue);
	}
	
	public PTitleBorder(PTextModel initialModel) {
		this(Orientation.TOP_LEFT, 1, initialModel);
	}
	
	public PTitleBorder(int lineThickness, Object defaultValue) {
		this(Orientation.TOP_LEFT, lineThickness, defaultValue);
	}
	
	public PTitleBorder(int lineThickness, PTextModel initialModel) {
		this(Orientation.TOP_LEFT, lineThickness, initialModel);
	}
	
	public PTitleBorder(Orientation orientation, Object defaultValue) {
		this(orientation, 1, defaultValue);
	}
	
	public PTitleBorder(Orientation orientation, PTextModel initialModel) {
		this(orientation, 1, initialModel);
	}
	
	public PTitleBorder(Orientation orientation, int lineThickness, Object defaultValue) {
		super();
		
		setModel(PModelFactory.createModelFor(this, DefaultPTextModel::new, PTextModel.class));
		getModel().setValue(defaultValue);
		
		setLineThickness(lineThickness);
		setOrientation(orientation);
	}
	
	public PTitleBorder(Orientation orientation, int lineThickness, PTextModel initialModel) {
		super();
		setModel(initialModel);
		setLineThickness(lineThickness);
		setOrientation(orientation);
	}
	
	protected void onModelValueChanged(PSingleValueModel model, Object oldValue, Object newValue) {
		fireInsetsChangedEvent();
		fireReRenderEvent();
	}
	
	public void setModel(PTextModel newModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		model = newModel;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
	}
	
	public PTextModel getModel() {
		return model;
	}
	
	public void setTitle(Object value) {
		PTextModel model = getModel();
		if (model != null) {
			model.setValue(value);
		}
	}
	
	public Object getTitle() {
		PTextModel model = getModel();
		if (model != null) {
			return model.getValue();
		}
		return null;
	}
	
	public String getTitleText() {
		PTextModel model = getModel();
		if (model != null) {
			return model.getText();
		}
		return "";
	}
	
	public void setOrientation(Orientation value) {
		if (orientation != value) {
			orientation = value;
			fireReRenderEvent();
		}
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setLineThickness(int value) {
		if (getLineThickness() != value) {
			lineSize = value;
			fireInsetsChangedEvent();
		}
	}
	
	public int getLineThickness() {
		return lineSize;
	}
	
	public void setPadding(int value) {
		if (getPadding() != value) {
			padding = value;
			fireInsetsChangedEvent();
		}
	}
	
	public int getPadding() {
		return padding;
	}
	
	@Override
	public PInsets getDefaultInsets(PComponent component) {
		int lineSize = getLineThickness();
		int padding = getPadding();
		PRoot root = component.getRoot();
		if (root == null) {
			insets.set(lineSize + padding);
			return insets;
		}
		PFontResource font = root.fetchFontResource(FONT_ID);
		if (font == null) {
			insets.set(lineSize + padding);
			return insets;
		}
		PSize titleSize = font.getSize(getTitleText(), null);
		int insetsTop;
		int insetsBtm;
		if (getOrientation().isTop) {
			insetsTop = Math.max(titleSize.getHeight(), lineSize);
			insetsBtm = lineSize;
		} else {
			insetsTop = lineSize;
			insetsBtm = Math.max(titleSize.getHeight(), lineSize);
		}
		insets.set(insetsTop + padding, insetsBtm + padding, lineSize + padding, lineSize + padding);
		return insets;
	}
	
	@Override
	public void defaultRender(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBounds();
		PInsets insets = getInsets(component);
		Orientation ori = getOrientation();
		int lineSize = getLineThickness();
		int top = insets.getFromTop();
		int lft = insets.getFromLeft();
		int rgt = insets.getFromRight();
		int btm = insets.getFromBottom();
		int x = bnds.getX() + lft / 2;
		int y = bnds.getY() + top / 2;
		int fx = bnds.getFinalX() - rgt / 2;
		int fy = bnds.getFinalY() - btm / 2;
		
		renderer.setColor(PColor.BLACK);
		// Draw left
		renderer.drawQuad( x - 0,  y - 0,  x + lineSize, fy + 0);
		// Draw right
		renderer.drawQuad(fx - lineSize,  y - 0, fx + 0, fy + 0);
		if (ori.isTop) {
			// Draw bottom
			renderer.drawQuad( x - 0, fy - lineSize, fx + 0, fy + 0);
		} else {
			// Draw top
			renderer.drawQuad( x - 0,  y - 0, fx + 0,  y + lineSize);
		}
		
		PRoot root = component.getRoot();
		if (root == null) {
			if (ori.isTop) {
				// Draw top
				renderer.drawQuad( x - 0,  y - 0, fx + 0,  y + lineSize);
			} else {
				// Draw bottom
				renderer.drawQuad( x - 0, fy - lineSize, fx + 0, fy + 0);
			}
			return;
		}
		PFontResource font = root.fetchFontResource(FONT_ID);
		String titleText = getTitleText();
		PSize titleSize = font.getSize(titleText, null);
		int aliMultH = ori.getHorizontalAlignmentMultiplier();
		int txtX = ori.getDrawX(bnds, insets, titleSize) + (aliMultH * 2 * lineSize);
		int txtY = ori.getDrawY(bnds, insets, titleSize) + 2 * lineSize;
		int txtFx = txtX + titleSize.getWidth();
		if (ori.isTop) {
			// Draw top
			renderer.drawQuad(x, y, txtX, y + lineSize);
			renderer.drawQuad(txtFx, y, fx, y + lineSize);
		} else {
			// Draw bottom
			renderer.drawQuad(x, fy - lineSize, txtX, fy);
			renderer.drawQuad(txtFx, fy - lineSize, fx, fy);
		}
		// Draw text
		renderer.drawString(font, titleText, txtX, txtY);
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return false;
	}
	
	public static enum Orientation {
		TOP_LEFT		(true) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getX() + insets.getFromLeft();
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getY();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return 1;
			}
		},
		TOP_RIGHT		(true) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getFinalX() - insets.getFromRight() - textSize.getWidth();
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getY();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return -1;
			}
		},
		TOP_CENTER		(true) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				int bndsW = bounds.getWidth() - insets.getWidth();
				int txtW = textSize.getWidth();
				return bounds.getX() + bndsW / 2 - txtW / 2;
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getY();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return 0;
			}
		},
		BOTTOM_LEFT		(false) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getX() + insets.getFromLeft();
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getFinalY() - insets.getFromBottom();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return 1;
			}
		},
		BOTTOM_RIGHT	(false) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getFinalX() - insets.getFromRight() - textSize.getWidth();
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getFinalY() - insets.getFromBottom();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return -1;
			}
		},
		BOTTOM_CENTER		(false) {
			@Override
			public int getDrawX(PBounds bounds, PInsets insets, PSize textSize) {
				int bndsW = bounds.getWidth() - insets.getWidth();
				int txtW = textSize.getWidth();
				return bounds.getX() + bndsW / 2 - txtW / 2;
			}
			@Override
			public int getDrawY(PBounds bounds, PInsets insets, PSize textSize) {
				return bounds.getFinalY() - insets.getFromBottom();
			}
			@Override
			public int getHorizontalAlignmentMultiplier() {
				return 0;
			}
		},
		;
		
		public final boolean isTop;
		
		private Orientation(boolean top) {
			isTop = top;
		}
		
		public abstract int getDrawX(PBounds bounds, PInsets insets, PSize textSize);
		public abstract int getDrawY(PBounds bounds, PInsets insets, PSize textSize);
		public abstract int getHorizontalAlignmentMultiplier();
		
	}
	
}