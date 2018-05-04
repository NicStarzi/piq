package edu.udo.piq.components.collections.list;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.list.PListHiPerf.PElementRenderer;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.tools.MutablePSize;

public class PListHPDefaultElementRenderer implements PElementRenderer {
	
	protected final MutablePSize tmpSize = new MutablePSize();
	protected PSize minSize = PSize.ZERO_SIZE;
	protected Object fontKey = PLabel.FONT_ID;
	protected AlignmentX alignX = AlignmentX.LEFT;
	protected AlignmentY alignY = AlignmentY.CENTER;
	protected PColor backColor = null;
	protected PColor backColorSelected = DefaultPCellComponent.DEFAULT_BACKGROUND_SELECTED_COLOR;
	protected PColor textColor = PColor.BLACK;
	protected PColor textColorSelected = DefaultPCellComponent.DEFAULT_TEXT_SELECTED_COLOR;
	protected PColor focusOutlineColor = PList.FOCUS_COLOR;
	protected PFontResource cachedFontRes;
	
	protected String getElementText(PModel model, PModelIndex index) {
		Object content = model.get(index);
		String text = content == null ? "null" : content.toString();
		return text;
	}
	
	@Override
	public int getWidth(PRoot root, PModel model, PModelIndex index) {
		int width = 0;
		if (root != null) {
			PFontResource fontRes = root.fetchFontResource(fontKey);
			if (fontRes != null) {
				String text = getElementText(model, index);
				width += fontRes.getSize(text, tmpSize).getWidth();
			}
		}
		return width;
	}
	
	@Override
	public int getHeight(PRoot root) {
		int height = 0;
		if (root != null) {
			PFontResource fontRes = root.fetchFontResource(fontKey);
			if (fontRes != null) {
				height += fontRes.getPixelSize();
			}
		}
		if (height < minSize.getHeight()) {
			return minSize.getHeight();
		}
		return height;
	}
	
	@Override
	public void renderElement(PRoot root, PBounds maxElemBounds, PRenderer renderer, 
			PSelection selection, PModel model, PListIndex index) 
	{
		if (cachedFontRes == null) {
			return;
		}
		String text = getElementText(model, index);
		PSize textSize = cachedFontRes.getSize(text, tmpSize);
		
		int textMinX = maxElemBounds.getX();
		int textMaxW = maxElemBounds.getWidth();
		int textPrefW = textSize.getWidth();
		
		int textMinY = maxElemBounds.getY();
		int textMaxH = maxElemBounds.getHeight();
		int textPrefH = textSize.getHeight();
		
		int textX = alignX.getLeftX(textMinX, textMaxW, textPrefW);
		int textY = alignY.getTopY(textMinY, textMaxH, textPrefH);
		if (selection.isSelected(index)) {
			renderer.setColor(textColorSelected);
		} else {
			renderer.setColor(textColor);
		}
		renderer.drawString(cachedFontRes, text, textX, textY);
	}
	
	@Override
	public void renderBackground(PRoot root, PBounds maxElemBounds, PRenderer renderer, 
			PSelection selection, PModel model, PListIndex index) 
	{
		PColor bgColor;
		if (selection.isSelected(index)) {
			bgColor = backColorSelected;
		} else {
			bgColor = backColor;
		}
		if (bgColor != null) {
			renderer.setColor(bgColor);
			renderer.drawQuad(maxElemBounds);
		}
	}
	
	@Override
	public void renderLastSelectedElement(PRoot root, PBounds maxElemBounds, 
			PRenderer renderer, PSelection selection, PModel model, PListIndex index) 
	{
		int cx = maxElemBounds.getX() - 1;
		int cy = maxElemBounds.getY() - 1;
		int cfx = maxElemBounds.getFinalX() + 1;
		int cfy = maxElemBounds.getFinalY() + 1;
		renderer.setColor(focusOutlineColor);
		renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
		renderer.drawQuad(cx, cy, cfx, cfy);
	}
	
	@Override
	public void renderDropHighlightIndex(PRoot root, PBounds listBounds, PBounds maxElemBoundsAtIndex, 
			PRenderer renderer, PSelection selection, PModel model, PListIndex dropIdx) 
	{
		int elemCount = model.getSize();
		int ex, ey, efx, efy;
		if (elemCount == 0) {
			ex = listBounds.getX();
			ey = listBounds.getY();
			efx = listBounds.getFinalX();
		} else if (dropIdx.getIndexValue() >= elemCount) {
			ex = maxElemBoundsAtIndex.getX();
			ey = maxElemBoundsAtIndex.getFinalY();
			efx = maxElemBoundsAtIndex.getFinalX();
		} else {
			ex = maxElemBoundsAtIndex.getX();
			ey = maxElemBoundsAtIndex.getY();
			efx = maxElemBoundsAtIndex.getFinalX();
		}
		efy = ey + 2;
		renderer.drawQuad(ex, ey, efx, efy);
	}
	
	@TemplateMethod
	@Override
	public void beforeRender(PRoot root, PRenderer renderer) {
		if (root == null) {
			return;
		}
		cachedFontRes = root.fetchFontResource(fontKey);
	}
	
	@TemplateMethod
	@Override
	public void afterRender(PRoot root, PRenderer renderer) {
		cachedFontRes = null;
	}
	
}