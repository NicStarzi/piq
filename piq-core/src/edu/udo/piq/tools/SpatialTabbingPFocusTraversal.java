package edu.udo.piq.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.Axis;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.VirtualKey;
import edu.udo.piq.components.textbased.PTextComponent;

public class SpatialTabbingPFocusTraversal extends AbstractPFocusTraversal implements PFocusTraversal {
	
	protected PComponent container;
	
	public SpatialTabbingPFocusTraversal() {
		this(null);
	}
	
	public SpatialTabbingPFocusTraversal(PComponent container) {
		this.container = container;
	}
	
	@Override
	protected void onKeyTriggered(PKeyboard keyboard, Key key) {
		PComponent focusOwner = curRoot.getFocusOwner();
		if (focusOwner == null) {
			return;
		}
		if (focusOwner instanceof PTextComponent) {
			if (keyboard.isTriggered(VirtualKey.FOCUS_UP)) {
				moveFocus(focusOwner, +1);
			} else if (keyboard.isTriggered(VirtualKey.FOCUS_DOWN)) {
				moveFocus(focusOwner, -1);
			}
		} else {
			if (keyboard.isTriggered(VirtualKey.FOCUS_NEXT)) {
				moveFocus(focusOwner, +1);
			} else if (keyboard.isTriggered(VirtualKey.FOCUS_PREV)) {
				moveFocus(focusOwner, -1);
			}
		}
	}
	
	protected void moveFocus(PComponent focusOwner, int offset) {
		List<PComponent> all = clusterComponentsByLines();
		if (all.isEmpty()) {
			return;
		} else if (all.size() == 1) {
			all.get(0).tryToTakeFocus();
			return;
		}
		focusOwner = focusOwner.getHighestFocusableAncestor();
		int index = all.indexOf(focusOwner);
		if (index == -1) {
			all.get(0).tryToTakeFocus();
			return;
		}
		index = (index + offset) % all.size();
		if (index < 0) {
			index += all.size();
		}
		PComponent newFocusOwner = all.get(index);
		newFocusOwner.tryToTakeFocus();
	}
	
	protected List<PComponent> clusterComponentsByLines() {
		PComponent focusRoot = container == null ? curRoot : container;
		List<PComponent> allFocusable = PFocusTraversal.getAllFocusableComponents(focusRoot);
		if (allFocusable.isEmpty()) {
			return Collections.emptyList();
		} else if (allFocusable.size() == 1) {
			return allFocusable;
		}
		List<PComponent> topLvlFocusable = new ArrayList<>(allFocusable);
		for (PComponent comp : allFocusable) {
			for (PComponent maybeAncestor : allFocusable) {
				if (comp != maybeAncestor && maybeAncestor.isAncestorOf(comp)) {
					topLvlFocusable.remove(comp);
				}
			}
		}
		topLvlFocusable.sort((c1, c2) -> Integer.compare(c1.getBounds().getY(), c2.getBounds().getY()));
		if (topLvlFocusable.size() == 1) {
			return topLvlFocusable;
		}
		List<PComponent> lines = new ArrayList<>();
		List<PComponent> curLine = new ArrayList<>();
		for (PComponent current : topLvlFocusable) {
			if (curLine.isEmpty()) {
				curLine.add(current);
			} else {
				PBounds curBounds = current.getBounds();
				boolean isOnLine = true;
				for (PComponent lineComp : curLine) {
					if (!Axis.Y.isOverlapping(curBounds, lineComp.getBounds())) {
						isOnLine = false;
						break;
					}
				}
				if (isOnLine) {
					curLine.add(current);
				} else {
					curLine.sort((c1, c2) -> Integer.compare(c1.getBounds().getX(), c2.getBounds().getX()));
					lines.addAll(curLine);
					curLine.clear();
					curLine.add(current);
				}
			}
		}
		curLine.sort((c1, c2) -> Integer.compare(c1.getBounds().getX(), c2.getBounds().getX()));
		lines.addAll(curLine);
		return lines;
	}
	
}