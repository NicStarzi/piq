package edu.udo.piq;

import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.style.PStyleable;
import edu.udo.piq.tools.AbstractPBorder;

/**
 * <p>Borders are parts of {@link PComponent components}. Each 
 * component either {@link PComponent#getBorder() has a border} 
 * or not. If a component has a border, the {@link #getDefaultInsets(PComponent) 
 * insets} of the border will be added to the {@link PComponent#getPreferredSize() 
 * preferred size} of the component and the border will be 
 * {@link #defaultRender(PRenderer, PComponent) rendered} before 
 * the component {@link PComponent#defaultRender(PRenderer) is rendered}. 
 * Whether or not the border {@link #defaultFillsAllPixels(PComponent) 
 * fills all pixels} will also affect whether or not the component will 
 * {@link PComponent#fillsAllPixels() fill all pixels}.</p>
 * 
 * <p>A border can be {@link PBorderObs observed} for 
 * {@link PBorderObs#onInsetsChanged(PBorder) changes to its insets} 
 * or for {@link PBorderObs#onReRender(PBorder) re-rendering requests}. 
 * A component should always observe its border to react to these events.</p>
 * 
 * <p>Borders are {@link PStyleable styleable}. A {@link PStyleBorder} 
 * can change the insets of a border and the way it is rendered.</p>
 * 
 * <p>Implementations of PBorder should inherit from {@link AbstractPBorder} 
 * for a clean implementation of the observer pattern and the {@link PStyleable} 
 * interface.</p>
 * 
 * @author Nic Starzi
 */
public interface PBorder extends PStyleable<PStyleBorder> {
	
	/**
	 * <p>Returns the {@link PInsets insets} used by this border in case 
	 * it is not {@link PStyleBorder styled}. This method need not be called 
	 * by users. Use the method {@link #getInsets(PComponent)} instead which 
	 * will return the styled insets or the default insets depending on 
	 * whether a style is used or not.</p> 
	 * @param component		the component for which the insets should be calculated
	 * @return				a non-null instance of PInsets representing the area needed by the border
	 * @see #getInsets(PComponent)
	 */
	public PInsets getDefaultInsets(PComponent component);
	
	/**
	 * <p>Returns the {@link PInsets insets} used by this border. 
	 * This method will return the actual insets used which may be the 
	 * {@link #getDefaultInsets(PComponent) default insets} or the 
	 * insets {@link PStyleBorder#getInsetsFor(PBorder, PComponent) as 
	 * defined by the current style}.</p>
	 * <p>User code should always call this method rather than the 
	 * {@link #getDefaultInsets(PComponent)} method.</p>
	 * @param component		the component for which the insets should be calculated
	 * @return				a non-null instance of PInsets representing the area needed by the border
	 * @see #getDefaultInsets(PComponent)
	 * @see PStyleBorder#getInsetsFor(PBorder, PComponent)
	 */
	public default PInsets getInsets(PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			return getDefaultInsets(component);
		} else {
			return style.getInsetsFor(this, component);
		}
	}
	
	/**
	 * <p>A default implementation for the rendering logic of this border. 
	 * This rendering implementation is used if no {@link PStyleBorder style} 
	 * is set.</p>
	 * <p>A border should be rendered at the {@link PComponent#getBounds() bounds} 
	 * of the component passed as the second argument. A border can assume to have 
	 * as much empty space as its {@link #getInsets(PComponent) insets} request. 
	 * Borders should not render anything {@link PComponent#getBoundsWithoutBorder() 
	 * outside of their insets}.</p>
	 * @param renderer			a valid non-null renderer
	 * @param component			the non-null component around which the border is to be rendered
	 * @see #defaultFillsAllPixels(PComponent)
	 * @see #render(PRenderer, PComponent)
	 * @see PComponent#defaultRender(PRenderer)
	 * @see PComponent#getBounds()
	 * @see PComponent#getBoundsWithoutBorder()
	 */
	public void defaultRender(PRenderer renderer, PComponent component);
	
	/**
	 * <p>Delegates the rendering logic either to the 
	 * {@link PStyleBorder#render(PRenderer, PBorder, PComponent) style} or to the 
	 * {@link #defaultRender(PRenderer, PComponent)} method depending on whether 
	 * or not a {@link #getStyle() style is set}.</p>
	 * @param renderer			a valid non-null renderer
	 * @param component			the non-null component around which the border is to be rendered
	 * @see #defaultRender(PRenderer, PComponent)
	 * @see #fillsAllPixels(PComponent)
	 * @see PComponent#render(PRenderer)
	 */
	public default void render(PRenderer renderer, PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			defaultRender(renderer, component);
		} else {
			style.render(renderer, this, component);
		}
	}
	
	/**
	 * <p>Returns {@code true} if the {@link #defaultRender(PRenderer, PComponent)} method 
	 * would draw on every pixel within the {@link #getInsets(PComponent) insets} 
	 * of this border.</p>
	 * <p>It is important to keep the implementation of {@link #defaultRender(PRenderer, PComponent)} 
	 * and this method synchronized. If this method returns {@code true} and the render method 
	 * does not draw every pixel, rendering artifacts may occur.</p>
	 * <p>This method may return {@code false} even though the {@link #defaultRender(PRenderer, PComponent)} 
	 * method draws every pixel. This is not an issue.</p>
	 * @param component			the component for which the render method will be called
	 * @return					true if defaultRender would draw on every pixel within the insets, otherwise false
	 * @see #defaultRender(PRenderer, PComponent)
	 * @see #fillsAllPixels(PComponent)
	 * @see PComponent#defaultFillsAllPixels()
	 */
	public boolean defaultFillsAllPixels(PComponent component);
	
	public default boolean fillsAllPixels(PComponent component) {
		PStyleBorder style = getStyle();
		if (style == null) {
			return defaultFillsAllPixels(component);
		} else {
			return style.fillsAllPixels(this, component);
		}
	}
	
	public void addObs(PBorderObs obs);
	
	public void removeObs(PBorderObs obs);
	
}