package edu.udo.piq.components.containers;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.defaults.DefaultPTabBackground;
import edu.udo.piq.components.defaults.DefaultPTabFactory;
import edu.udo.piq.layouts.PTabPanelLayout;
import edu.udo.piq.layouts.PTabPanelLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PTabPanel extends AbstractPLayoutOwner {
	
	private final List<TabBodyTuple> tabList = new ArrayList<>();
	private PTabFactory tabFactory = new DefaultPTabFactory();
	
	public PTabPanel() {
		super();
		setLayout(new PTabPanelLayout(this));
		getLayoutInternal().setTabBackground(new DefaultPTabBackground());
		
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
					int mx = mouse.getX();
					int my = mouse.getY();
					int tabIndex = getLayoutInternal().getTabIndexAt(mx, my);
					if (tabIndex != -1) {
						selectTab(tabIndex);
					}
				}
			}
		});
	}
	
	protected PTabPanelLayout getLayoutInternal() {
		return (PTabPanelLayout) super.getLayout();
	}
	
	public PTabFactory getTabFactory() {
		return tabFactory;
	}
	
	public void addTab(PComponent tabPreview, PComponent body) {
		int index = tabList.size();
		addTab(index, tabPreview, body);
	}
	
	public void addTab(int index, PComponent tabPreview, PComponent body) {
		PTabComponent tabComp = getTabFactory().getTabComponentFor(tabPreview, index);
		TabBodyTuple tuple = new TabBodyTuple(tabComp, body);
		tabList.add(index, tuple);
		getLayoutInternal().addChild(tabComp, Constraint.TAB);
		getLayoutInternal().addChild(body, Constraint.BODY);
		
		if (getLayoutInternal().getSelectedIndex() == -1) {
			selectTab(0);
		}
	}
	
	public void removeTab(int index) {
		if (index == getSelectedTabIndex() && tabList.size() > 1) {
			// TODO: select different tab
		}
		TabBodyTuple tuple = tabList.remove(index);
		getLayoutInternal().removeChild(tuple.getTabComponent());
		getLayoutInternal().removeChild(tuple.getBodyComponent());
	}
	
	public void selectTab(int tabIndex) {
		int oldIndex = getSelectedTabIndex();
		if (tabIndex == oldIndex) {
			return;
		}
		if (oldIndex != -1) {
			TabBodyTuple tuple = tabList.get(oldIndex);
			tuple.getTabComponent().setSelected(false);
		}
		getLayoutInternal().setSelectedIndex(tabIndex);
		if (tabIndex != -1) {
			TabBodyTuple tuple = tabList.get(tabIndex);
			tuple.getTabComponent().setSelected(true);
		}
	}
	
	public int getSelectedTabIndex() {
		return getLayoutInternal().getSelectedIndex();
	}
	
	protected static class TabBodyTuple {
		
		protected final PTabComponent tab;
		protected final PComponent body;
		
		protected TabBodyTuple(PTabComponent tabComp, PComponent bodyComp) {
			tab = tabComp;
			body = bodyComp;
		}
		
		public PTabComponent getTabComponent() {
			return tab;
		}
		
		public PComponent getBodyComponent() {
			return body;
		}
		
	}
	
}