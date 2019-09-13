package edu.udo.piq.scroll2;

import java.util.Collections;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.layouts.AbstractPLayout;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.tools.AbstractPComponent;

public class PScrollPanelViewport extends AbstractPComponent {
	
	protected final ViewportLayout layout = new ViewportLayout();
	
	@Override
	public PReadOnlyLayout getLayout() {
		return getLayoutInternal();
	}
	
	protected ViewportLayout getLayoutInternal() {
		return layout;
	}
	
	public PComponent getBody() {
		if (layout.bodyData == null) {
			return null;
		}
		return layout.bodyData.getComponent();
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBoundsWithoutBorder());
	}
	
	@TemplateMethod
	protected void onBodyChanged() {
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onBodyLaidOut() {
		fireReRenderEvent();
	}
	
	// This special layout is not a faithful implementation of PLayout.
	// It only works as a layout for a PScrollPanelViewport and in combination
	// with a PScrollPanelLayout at the parent of the viewport.
	protected class ViewportLayout extends AbstractPLayout {
		
		PComponentLayoutData bodyData;
		
		protected ViewportLayout() {
			super(PScrollPanelViewport.this);
		}
		
		@Override
		public Iterable<PComponentLayoutData> getAllData() {
			return bodyData == null ? Collections.emptyList() : Collections.singleton(bodyData);
		}
		
		@Override
		public int getChildCount() {
			return bodyData == null ? 0 : 1;
		}
		
		@Override
		protected boolean canAdd(PComponent component, Object constraint) {
			return bodyData == null;
		}
		
		@Override
		protected void clearAllDataInternal() {
			bodyData = null;
			onBodyChanged();
		}
		
		@Override
		protected void addDataInternal(PComponentLayoutData info) {
			bodyData = info;
			onBodyChanged();
		}
		
		@Override
		protected void removeDataInternal(PComponentLayoutData info) {
			bodyData = null;
			onBodyChanged();
		}
		
		@Override
		protected void layOutInternal() {
			// layouting is done by PScrollPanelLayout
		}
		
		// called by the layOutInternal() method of PScrollPanelLayout to set the cell for the body component
		protected void setBodyCellFilled(int x, int y, int width, int height) {
			super.setChildCellFilled(bodyData, x, y, width, height);
			onBodyLaidOut();
		}
		
	}
	
}