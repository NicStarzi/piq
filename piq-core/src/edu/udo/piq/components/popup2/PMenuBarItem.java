package edu.udo.piq.components.popup2;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PMenuBarItem extends AbstractPLayoutOwner implements PClickable {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final AlignmentX DEFAULT_ALIGNMENT_X = AlignmentX.PREFERRED_OR_CENTER;
	public static final AlignmentY DEFAULT_ALIGNMENT_Y = AlignmentY.PREFERRED_OR_CENTER;
	
	protected final List<PComponent> items = new ArrayList<>();
	protected boolean mouseHover = false;
	protected boolean armed = false;
	
	public PMenuBarItem(Object initialLabelValue) {
		this(new PLabel(initialLabelValue));
	}
	
	public PMenuBarItem(PTextModel initialLabelModel) {
		this(new PLabel(initialLabelModel));
	}
	
	public PMenuBarItem(PComponent initialContent) {
		PAnchorLayout layout = new PAnchorLayout(this);
		layout.setAlignment(DEFAULT_ALIGNMENT_X, DEFAULT_ALIGNMENT_Y);
		layout.setInsets(DEFAULT_INSETS);
		setLayout(layout);
		
		layout.setContent(initialContent);
	}
	
	public void addMenuItem(PComponentActionIndicator actionIndicator) {
		addMenuItem(new PMenuItem(actionIndicator));
	}
	
	public void addMenuItem(PComponent item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifIncluded(items, item, "items.contains(item) == true");
		items.add(item);
	}
	
	public void removeMenuItem(AbstractPMenuItem item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifExcluded(items, item, "items.contains(item) == false");
		items.remove(item);
	}
	
	public int getMenuItemCount() {
		return items.size();
	}
	
	public PComponent getMenuItem(int index) {
		return items.get(index);
	}
	
	public void setMouseHover(boolean value) {
		if (mouseHover != value) {
			mouseHover = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isMouseHover() {
		return mouseHover;
	}
	
	public boolean isPressed() {
		return isMouseHover() && isArmed();
	}
	
	public void setArmed(boolean value) {
		if (armed != value) {
			armed = value;
			if (isMouseHover()) {
				fireReRenderEvent();
			}
		}
	}
	
	public boolean isArmed() {
		return armed;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		// isPressed must be checked before isMouseHover
		if (isPressed()) {
			renderer.setColor(PColor.GREY50);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.WHITE);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else if (isMouseHover()) {
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY50);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x, y, fx, fy);
		}
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return super.getDefaultPreferredSize();
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
}