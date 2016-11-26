package edu.udo.piq;

///**
// * A {@link PBorder} is a special kind of {@link PComponent} that can be 
// * used to draw borders around components.<br>
// * A border traditionally belongs to one component that the border will 
// * be drawn around, this component is called the content of the border.<br>
// * Implementations of this interface usually use a {@link PBorderLayout} 
// * as their layout but are not needed to.<br> 
// * 
// * @author Nic Starzi
// */
public interface PBorder {// extends PComponent {
	
	public PInsets getDefaultInsets(PComponent component);
	
	public void defaultRender(PRenderer renderer, PComponent component);
	
	public boolean defaultFillsAllPixels(PComponent component);
	
	public void addObs(PBorderObs obs);
	
	public void removeObs(PBorderObs obs);
	
//	/**
//	 * Sets the {@link PComponent} that this border should be drawn 
//	 * around of.<br>
//	 * This can be null in which case this border is drawn around an 
//	 * empty space.<br>
//	 * 
//	 * @param component the content for this border
//	 * @see #getContent()
//	 */
//	public void setContent(PComponent component);
//	
//	/**
//	 * The {@link PComponent} that this border belongs to.<br>
//	 * This can be null in which case the border should be drawn around 
//	 * an empty space.<br>
//	 * 
//	 * @return the component this border belongs to
//	 * @see #setContent(PComponent)
//	 */
//	public PComponent getContent();
	
}