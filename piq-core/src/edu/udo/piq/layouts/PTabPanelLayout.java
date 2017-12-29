package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;

public class PTabPanelLayout extends AbstractMapPLayout {
	
	private final List<PComponent> tabList = new ArrayList<>();
	private final List<PComponent> bodyList = new ArrayList<>();
	private final List<PComponent> sortedChildList = new ArrayList<>();
	private PComponent tabBgCmp;
	private int selectedIndex = -1;
	
	public PTabPanelLayout(PComponent component) {
		super(component);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		Object constraint = data.getConstraint();
		PComponent child = data.getComponent();
		if (constraint == Constraint.TAB) {
			tabList.add(child);
		} else if (constraint == Constraint.TAB_BACKGROUND) {
			tabBgCmp = child;
		} else {
			bodyList.add(child);
		}
		if (constraint != Constraint.TAB_BACKGROUND && tabBgCmp != null) {
			sortedChildList.remove(tabBgCmp);
		}
		sortedChildList.add(child);
		if (constraint != Constraint.TAB_BACKGROUND && tabBgCmp != null) {
			sortedChildList.add(tabBgCmp);
		}
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		Object constraint = data.getConstraint();
		PComponent child = data.getComponent();
		if (constraint == Constraint.TAB) {
			tabList.remove(child);
		} else if (constraint == Constraint.TAB_BACKGROUND) {
			tabBgCmp = null;
		} else {
			bodyList.remove(child);
		}
		sortedChildList.remove(child);
		invalidate();
	}
	
//	private void sortChildList() {
//		Collections.sort(sortedChildList, childComparator);
//	}
	
	public void setTabBackground(PComponent backgroundComponent) {
		if (tabBgCmp != null) {
			removeChild(tabBgCmp);
		}
		addChild(backgroundComponent, Constraint.TAB_BACKGROUND);
	}
	
	public PComponent getTabBackground() {
		return tabBgCmp;
	}
	
	public void setSelectedIndex(int value) {
		if (value < 0 || value >= bodyList.size()) {
			throw new IllegalArgumentException("value="+value+", max="+(bodyList.size() -1));
		}
		selectedIndex = value;
		invalidate();
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public int getTabIndexAt(int x, int y) {
		PBounds ob = getOwner().getBounds();
		int ox = ob.getX();
		int fx = ob.getFinalX();
		if (x < ox || x > fx) {
			return -1;
		}
		int oy = ob.getY();
		int fy = oy + getTabsHeight();
		if (y >= oy && y <= fy) {
			int index = 0;
			for (PComponent tab : tabList) {
				PBounds tabBounds = getChildBounds(tab);
				if (tabBounds.contains(x, y)) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}
	
	public int getTabsHeight() {
		int tabH = 0;
		for (PComponent tab : tabList) {
			PSize tabPrefSize = getPreferredSizeOf(tab);
			int tabPrefH = tabPrefSize.getHeight();
			if (tabPrefH > tabH) {
				tabH = tabPrefH;
			}
		}
		return tabH;
	}
	
	public TabPosition getTabPosition() {
		return TabPosition.TOP;
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint;
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		int tabH = getTabsHeight();
		
		if (getTabBackground() != null) {
			setChildCellFilled(getTabBackground(), x, y, w, tabH);
		}
		
		int tabX = x;
		int tabY = y;
		for (PComponent tab : tabList) {
			PSize tabPrefSize = getPreferredSizeOf(tab);
			int tabW = tabPrefSize.getWidth();
			setChildCellFilled(tab, tabX, tabY, tabW, tabH);
			tabX += tabW;
		}
		
		int selectedIndex = getSelectedIndex();
		for (int i = 0; i < bodyList.size(); i++) {
			PComponent body = bodyList.get(i);
			if (i == selectedIndex) {
				setChildCellFilled(body, x, y + tabH, w, h - tabH);
			} else {
				setChildCellFilled(body, x, y + tabH, 0, 0);
			}
		}
	}
	
	@Override
	protected void onInvalidated() {
		int maxTabW = 0;
		int maxTabH = 0;
		for (PComponent Tab : tabList) {
			PSize tabPrefSize = getPreferredSizeOf(Tab);
			int tabW = tabPrefSize.getWidth();
			int tabH = tabPrefSize.getHeight();
			if (tabW > maxTabW) {
				maxTabW = tabW;
			}
			if (tabH > maxTabH) {
				maxTabH = tabH;
			}
		}
		int maxBodyW = 0;
		int maxBodyH = 0;
		for (PComponent body : bodyList) {
			PSize bodyPrefSize = getPreferredSizeOf(body);
			int bodyW = bodyPrefSize.getWidth();
			int bodyH = bodyPrefSize.getHeight();
			if (bodyW > maxBodyW) {
				maxBodyW = bodyW;
			}
			if (bodyH > maxBodyH) {
				maxBodyH = bodyH;
			}
		}
		int w = maxBodyW;
		int h = maxBodyH;
		if (getTabPosition().isHorizontal()) {
			w += maxTabW;
			if (h < maxTabH) {
				h = maxTabH;
			}
		} else {
			h += maxTabH;
			if (w < maxTabW) {
				w = maxTabW;
			}
		}
		prefSize.setWidth(w);
		prefSize.setHeight(h);
	}
	
	@Override
	public Iterable<PComponent> getChildren() {
		return Collections.unmodifiableList(sortedChildList);
	}
	
	public static enum Constraint {
		TAB, TAB_BACKGROUND, BODY;
	}
	
	public static enum TabPosition {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		;
		
		public boolean isHorizontal() {
			return this == LEFT || this == RIGHT;
		}
		
		public boolean isVertical() {
			return !isHorizontal();
		}
		
	}
	
}