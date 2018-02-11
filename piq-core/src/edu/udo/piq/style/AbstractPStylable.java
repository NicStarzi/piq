package edu.udo.piq.style;

import java.util.Objects;

import edu.udo.piq.TemplateMethod;
import edu.udo.piq.util.Throw;

public class AbstractPStylable<E extends PStyle> implements PStyleable<E> {
	
	protected E customStyle;
	protected E sheetStyle;
	protected PStyleObs styleObs = null;//lazy initialization
	protected Object styleID = getClass();
	
	@Override
	public void setCustomStyle(E style) {
		setStyles(style, getInheritedStyle());
	}
	
	@Override
	public E getCustomStyle() {
		return customStyle;
	}
	
	@Override
	public void setInheritedStyle(E style) {
		setStyles(getCustomStyle(), style);
	}
	
	@Override
	public E getInheritedStyle() {
		return sheetStyle;
	}
	
	@Override
	public void setStyleID(Object value) {
		Throw.ifNull(value, "value == null");
		if (!Objects.equals(styleID, value)) {
			styleID = value;
			onStyleIdChangedEvent();
		}
	}
	
	@Override
	public Object getStyleID() {
		return styleID;
	}
	
	protected void setStyles(E custom, E sheet) {
		E oldActiveStyle = getStyle();
		customStyle = custom;
		sheetStyle = sheet;
		E newActiveStyle = getStyle();
		if (Objects.equals(oldActiveStyle, newActiveStyle)) {
			return;
		}
		if (oldActiveStyle != null) {
			oldActiveStyle.removeObs(styleObs);
			if (newActiveStyle == null) {
				styleObs = null;
			}
		}
		if (newActiveStyle != null) {
			if (styleObs == null) {
				styleObs = new PStyleObs() {
					@Override
					public void onSizeChangedEvent() {
						AbstractPStylable.this.onStyleSizeChangedEvent();
					}
					@Override
					public void onReRenderEvent() {
						AbstractPStylable.this.onStyleReRenderEvent();
					}
				};
			}
			newActiveStyle.addObs(styleObs);
		}
		onActiveStyleChanged(oldActiveStyle, newActiveStyle);
	}
	
	@TemplateMethod
	protected void onActiveStyleChanged(E oldActiveStyle, E newActiveStyle) {}
	
	@TemplateMethod
	protected void onStyleSizeChangedEvent() {}
	
	@TemplateMethod
	protected void onStyleReRenderEvent() {}
	
	@TemplateMethod
	protected void onStyleIdChangedEvent() {}
	
}