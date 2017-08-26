package edu.udo.piq.tools;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.udo.piq.Axis;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.components.textbased.PTextComponent;

public class SpatialArrowKeyPFocusTraversal extends AbstractPFocusTraversal implements PFocusTraversal {
	
	protected PComponent container;
	
	public SpatialArrowKeyPFocusTraversal() {
		this(null);
	}
	
	public SpatialArrowKeyPFocusTraversal(PComponent container) {
		this.container = container;
	}
	
	@Override
	protected void onKeyTriggered(PKeyboard keyboard, Key key) {
		PComponent focusOwner = curRoot.getFocusOwner();
		// If the focused component is a text component the arrow keys have are used to move the caret.
		// In this case we require the ALT modifier key to be pressed also.
		if (focusOwner instanceof PTextComponent && !keyboard.isModifierToggled(Modifier.ALT)) {
			return;
		}
		if (keyboard.isModifierToggled(Modifier.CTRL)) {
			switch (key) {
			case UP:
				tryToGiveFocus(getFocusCandidateUp());
				break;
			case DOWN:
				tryToGiveFocus(getFocusCandidateDown());
				break;
			case LEFT:
				tryToGiveFocus(getFocusCandidateLeft());
				break;
			case RIGHT:
				tryToGiveFocus(getFocusCandidateRight());
				break;
			default:
			}
		}
	}
	
	protected void tryToGiveFocus(PComponent comp) {
		if (comp != null) {
			comp.tryToTakeFocus();
		}
	}
	
	protected PComponent getFocusCandidateUp() {
		return getFocusCandidate(Axis.X, PBounds::getY, PBounds::getFinalY, (cBnds, foBnds) -> cBnds.getFinalY() <= foBnds.getY());
	}
	
	protected PComponent getFocusCandidateDown() {
		return getFocusCandidate(Axis.X, PBounds::getFinalY, PBounds::getY, (cBnds, foBnds) -> cBnds.getY() >= foBnds.getFinalY());
	}
	
	protected PComponent getFocusCandidateLeft() {
		return getFocusCandidate(Axis.Y, PBounds::getX, PBounds::getFinalX, (cBnds, foBnds) -> cBnds.getFinalX() <= foBnds.getX());
	}
	
	protected PComponent getFocusCandidateRight() {
		return getFocusCandidate(Axis.Y, PBounds::getFinalX, PBounds::getX, (cBnds, foBnds) -> cBnds.getX() >= foBnds.getFinalX());
	}
	
	protected PComponent getFocusCandidate(Axis axis, Function<PBounds, Integer> focusOwnerCoordGetter,
			Function<PBounds, Integer> compCoordGetter, BiPredicate<PBounds, PBounds> filter)
	{
		PComponent focusOwner = curRoot.getFocusOwner();
		if (focusOwner == null) {// No focus owner => abort
			return null;
		}
		PComponent root = container == null ? curRoot : container;
		List<PComponent> comps = PFocusTraversal.getAllFocusableComponents(root);
		comps.remove(focusOwner);
		if (comps.isEmpty()) {// Focus owner is the only focusable component in GUI => abort
			return null;
		}
		PBounds focusOwnerBounds = focusOwner.getBounds();
		int focusOwnerCoord = focusOwnerCoordGetter.apply(focusOwnerBounds);
		
		comps = comps.stream()
				// remove all components in the wrong direction
				.filter(c -> filter.test(c.getBounds(), focusOwnerBounds))
				// remove all components which are not overlapping with the focus owner on the given axis
				.filter(c -> isOverlapping(c, focusOwner, axis))
				// sort by distance to focus owner
				.sorted((c1, c2) -> {
					int dist1 = Math.abs(focusOwnerCoord - compCoordGetter.apply(c1.getBounds()));
					int dist2 = Math.abs(focusOwnerCoord - compCoordGetter.apply(c2.getBounds()));
					return Integer.compare(dist1, dist2);
				})
				.collect(Collectors.toList());
		// if empty => no components in given spatial direction => abort
		if (comps.isEmpty()) {
			return null;
		}
		return comps.get(0);
	}
	
	// Code taken from PBounds.isOverlapping(PBounds) and simplified to use only given axis
	protected boolean isOverlapping(PComponent c1, PComponent c2, Axis axis) {
		int x1 = axis.getFirstCoordinate(c1);
		int fx1 = axis.getFinalCoordinate(c1);
		int x2 = axis.getFirstCoordinate(c2);
		int fx2 = axis.getFinalCoordinate(c2);
		return !(x1 > fx2 || fx1 < x2);
	}
	
}