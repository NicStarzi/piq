package edu.udo.piq;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.PGuiUtil;

/**
 * The key part of the piq GUI widget toolkit.<br>
 * PComponents define a tree like architecture where each component has a 
 * reference to its parent and every parent has, in turn, a {@link PLayout} 
 * that manages the components children.<br>
 * All kinds of user interaction is done by components and everything that 
 * is displayed graphically is a component that is being rendered to a 
 * {@link PRenderer}.<br>
 * This interface is supposed to provide the most basic key functionalities 
 * that are needed to make the piq library work. All implementations of 
 * PComponents may opt to add additional useful utility methods.<br>
 * When implementing a PComponent it is of prime importance that all methods 
 * of the implementation act exactly as defined by their documentation. 
 * Otherwise the newly implemented component might not correctly work together 
 * with other components and could potentially derange the entire GUI.<br>
 * PComponents can be observed by {@link PComponentObs}ervers using the observer 
 * pattern. All implementations of PComponent should provide a way of adding 
 * and removing observers without throwing {@link ConcurrentModificationException}s 
 * if an observer is added / removed while an event is fired. The use of the 
 * {@link CopyOnWriteArrayList} is encouraged but is not enforced.<br>
 * <br>
 * For an easy to use abstract implementation of the PComponent interface 
 * have a look at the {@link AbstractPComponent} class.
 * 
 * @author Nic Starzi
 * 
 * @see AbstractPComponent
 * @see PRoot
 * @see PRenderer
 * @see PLayout
 * @see PDesign
 * @see PComponentObs
 */
public interface PComponent {
	
	/**
	 * Returns the {@link PRoot} of the GUI-tree this component 
	 * is a part of.<br>
	 * If this component is not part of a GUI-tree this method 
	 * will return null.<br>
	 * 
	 * @return the root of this GUI-tree or null
	 * @see PRoot
	 */
	public PRoot getRoot();
	
	/**
	 * This method is supposed to be used internally by a {@link PLayout} to set 
	 * the newly added or removed child's parent.<br>
	 * This method should not be used by the end user or unpredictable, and most 
	 * likely erroneous, behavior will occur.<br>
	 * If parent is null this component will no longer have a parent.<br>
	 * 
	 * @param parent the newly set parent of this component or null
	 * @throws IllegalArgumentException if parent is a descendant of this component
	 * @throws IllegalStateException if this component already has a non-null parent
	 * @see #getParent()
	 * @see PLayout#addChild(PComponent, Object)
	 * @see PLayout#removeChild(Object)
	 * @see PLayout#removeChild(PComponent)
	 * @see PLayout#clearChildren()
	 * @see PCompUtil#isDescendant(PComponent, PComponent)
	 */
	public void setParent(PComponent parent) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Returns the parent component of this component in the GUI-tree.<br>
	 * If this component is not part of a GUI-tree this method 
	 * will return null.<br>
	 * 
	 * @return the parent of this component or null
	 * @see #setParent()
	 */
	public PComponent getParent();
	
	/**
	 * Returns the {@link PLayout} of this component.<br>
	 * If this component does not have a layout or does not 
	 * support layouts this method will return null.<br>
	 * 
	 * @return the layout of this component or null.
	 * @see PLayout
	 */
	public PLayout getLayout();
	
	/**
	 * Sets a custom {@link PDesign} for this component.<br>
	 * When this component is being rendered and a custom 
	 * design is set then the custom design will be used.<br>
	 * If no custom design is set the default design will 
	 * be taken from the {@link PDesignSheet} of the root.<br>
	 * 
	 * @param design the custom design for this component or null 
	 * to use the default design
	 */
	public void setDesign(PDesign design);
	
	/**
	 * Returns the {@link PDesign} used to render this component.<br>
	 * If this component has a custom design set then the custom 
	 * design is returned. Otherwise the returned design will be 
	 * taken from the {@link PDesignSheet} of the root.<br>
	 * If this component is not part of a GUI tree, and thus does 
	 * not have a root, null is returned.<br>
	 * 
	 * @return the design used to render this component
	 * @see PDesign
	 * @see PDesignSheet
	 * @see PCompUtil#getDesignOf(PComponent)
	 */
	public PDesign getDesign();
	
	/**
	 * Renders the default rendering of this component to the given 
	 * {@link PRenderer}.<br>
	 * This method is called if the {@link PDesignSheet} of the 
	 * {@link PRoot} of this component does not associate a 
	 * {@link PDesign} with this component.<br>
	 * 
	 * @param renderer - the renderer to be used for rendering
	 * @see PRenderer
	 * @see PDesignSheet
	 * @see PDesign
	 */
	public void defaultRender(PRenderer renderer);
	
	/**
	 * This method returns the default preferred size for this component 
	 * used by the default rendering mechanism.<br>
	 * This method should return a size as small as possible for rendering 
	 * itself with the {@link #defaultRender(PRenderer)} method.<br>
	 * {@link PLayout}s and {@link PDesign}s might use this value or ignore 
	 * it completely.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the preferred size for the default render
	 * @see #defaultRender(PRenderer)
	 * @see PSize
	 * @see PDesign
	 * @see PDesign#getPreferredSize(PComponent)
	 * @see PLayout
	 * @see PLayout#getChildBounds(PComponent)
	 */
	public PSize getDefaultPreferredSize();
	
	/**
	 * Called by the {@link PRoot} of this GUI-tree to update 
	 * the behavior of this component.<br>
	 */
	public void update();
	
	/**
	 * Adds an observer to this {@link PComponent}.<br>
	 * A component can have any number of observers and observers may be added 
	 * more then once to a component.<br>
	 * 
	 * @param obs
	 * @throws NullPointerException if obs is null
	 */
	public void addObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the observer from this component.<br>
	 * If the observer was not added to this component previously nothing will happen.<br>
	 * 
	 * @param obs the observer
	 * @throws NullPointerException if obs is null
	 */
	public void removeObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Sets the id of this {@link PComponent}.<br>
	 * The id should only be used for debugging purposes and has no meaning to the end 
	 * user whatsoever.<br>
	 * The id might be null.<br>
	 * 
	 * @param value the new id for this component
	 * @see #getID()
	 * @see PGuiUtil#componentToString(PComponent)
	 * @see PGuiUtil#guiTreeToString(PComponent)
	 */
	public void setID(String value);
	
	/**
	 * This id of a {@link PComponent} should only be used for debugging purposes and 
	 * has no meaning to the end user whatsoever.<br>
	 * The id might be null in which case it should be treated as if it was equal to 
	 * the simple name of the components class.<br>
	 * 
	 * @return the id use for debugging purposes
	 * @see #setID(String)
	 * @see PGuiUtil#componentToString(PComponent)
	 * @see PGuiUtil#guiTreeToString(PComponent)
	 */
	public String getID();
	
}