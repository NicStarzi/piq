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
	 * @param renderer the renderer to be used for rendering
	 * @see PRenderer
	 * @see PDesignSheet
	 * @see PDesign
	 */
	public void defaultRender(PRenderer renderer);
	
	/**
	 * Returns true if this component fills all pixels within its 
	 * {@link PBounds} when the {@link #defaultRender(PRenderer)} 
	 * method is invoked.<br>
	 * This method is important for the {@link PRenderer} to 
	 * determine whether the parent of this component needs to be 
	 * re-rendered when this component is re-rendered.<br>
	 * For a component which is translucent or has transparent parts 
	 * this method should always return false.<br>
	 * 
	 * @return true if the component is completely opaque
	 * @see #defaultRender(PRenderer)
	 * @see PRenderer
	 * @see PDesignSheet
	 * @see PDesign
	 */
	public boolean isDefaultOpaque();
	
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
	 * Returns true if this {@link PComponent} may become the focus owner of a GUI.<br>
	 * If this method returns false this method will be ignored when the user is 
	 * tabbing through components with the focus traversal keys.<br>
	 * A component that is not focusable may still get the focus programmatically 
	 * through the use of the {@link PRoot#setFocusOwner(PComponent)} or 
	 * {@link PCompUtil#takeFocus(PComponent)} method.<br>
	 * 
	 * @return true if this component should be included in focus traversal
	 * @see PRoot#setFocusOwner(PComponent)
	 * @see PRoot#getFocusOwner()
	 * @see PCompUtil#takeFocus(PComponent)
	 * @see PCompUtil#hasFocus(PComponent)
	 */
	public boolean isFocusable();
	
	/**
	 * Returns either an instance of {@link PDnDSupport} if this component supports 
	 * drag and drop or returns null if this component does not support drag and drop.<br>
	 * The {@link PDnDSupport} of a component may change within the components life-
	 * cycle but this must never happen while a drag is taking place on the support.<br>
	 * 
	 * @return an instance of {@link PDnDSupport} if drag and drop is supported, or null if drag and drop is not supported
	 * @see PDnDManager
	 * @see PDnDSupport
	 * @see PDnDTransfer
	 */
	public PDnDSupport getDragAndDropSupport();
	
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
	 * @param obs the obs to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PComponentObs)
	 */
	public void addObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the observer from this component.<br>
	 * If the observer was not added to this component previously nothing will happen.<br>
	 * 
	 * @param obs the obs to be unregistered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PComponentObs)
	 */
	public void removeObs(PComponentObs obs) throws NullPointerException;
	
	/**
	 * Adds an observer to this {@link PComponent}.<br>
	 * A component can have any number of observers and observers may be added 
	 * more then once to a component.<br>
	 * 
	 * @param obs the obs to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PFocusObs)
	 * @see PRoot#addObs(PFocusObs)
	 */
	public void addObs(PFocusObs obs) throws NullPointerException;
	
	/**
	 * Removes one occurrence of the observer from this component.<br>
	 * If the observer was not added to this component previously nothing will happen.<br>
	 * 
	 * @param obs the obs to be unregistered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PFocusObs)
	 * @see PRoot#addObs(PFocusObs)
	 */
	public void removeObs(PFocusObs obs) throws NullPointerException;
	
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