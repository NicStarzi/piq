package edu.udo.piq.tests.swing;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.layouts.PGridListLayout;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.style.ImmutablePStyleComponent;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingPTest_MoveItemsInGridList extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_MoveItemsInGridList();
	}
	
	public SwingPTest_MoveItemsInGridList() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		root.setBody(bodyPnl);
		
		PGridListLayout bodyLayout = new PGridListLayout(bodyPnl, 2);
		bodyPnl.setLayout(bodyLayout);
		
		int itemPnlShadowSize = 2;
		int itemPnlRoundingArc = 8;
		int itemCount = 5;
		for (int i = 0; i < itemCount; i++) {
			ItemData item = new ItemData("Item #"+(i+1));
			PAnchorLayout itemLayout = new PAnchorLayout(item.pnl);
			itemLayout.setInsets(new ImmutablePInsets(
					/*top*/ itemPnlRoundingArc,
					/*btm*/ itemPnlRoundingArc + itemPnlShadowSize,
					/*lft*/ itemPnlRoundingArc,
					/*rgt*/ itemPnlRoundingArc + itemPnlShadowSize));
			item.pnl.setLayout(itemLayout);
			
			item.pnl.setCustomStyle(new ImmutablePStyleComponent() {
				@Override
				public boolean fillsAllPixels(PComponent component) {
					return false;
				}
				@Override
				public void render(PRenderer renderer, PComponent component) {
					PBounds bounds = component.getBounds();
					int x = bounds.getX();
					int y = bounds.getY();
					int fx = bounds.getFinalX() - itemPnlShadowSize;
					int fy = bounds.getFinalY() - itemPnlShadowSize;
					
					renderer.setColor(PColor.BLACK.mult1(1f, 1f, 1f, 0.5f));
					renderer.drawRoundedRect(x+itemPnlShadowSize, y+itemPnlShadowSize, fx+itemPnlShadowSize, fy+itemPnlShadowSize, itemPnlRoundingArc, itemPnlRoundingArc);
					renderer.setColor(PColor.WHITE);
					renderer.drawRoundedRect(x, y, fx, fy, itemPnlRoundingArc, itemPnlRoundingArc);
				}
			});
			
			item.lbl.setModelValue("Item No. #"+(i + 1));
			item.pnl.addChild(item.lbl, null);
			bodyPnl.addChild(item.pnl, null);
			allItems.add(item);
			
			int itemIdx = i;
			
			PButton itemBtn = new PButton();
			allBtns.add(itemBtn);
			itemBtn.addObs(self -> onItemClick(allItems.get(itemIdx)));
			bodyPnl.addChild(itemBtn, null);
		}
		refreshButtons();
	}
	
	private final List<ItemData> allItems = new ArrayList<>();
	private final List<PButton> allBtns = new ArrayList<>();
	private ItemData item1, item2;
	
	private void onItemClick(ItemData item) {
		if (item1 == null) {
			item1 = item;
		} else if (item2 == null) {
			item2 = item;
			
			ItemData d1 = item1;
			ItemData d2 = item2;
			PTimer timer = new PTimer(deltaMilliSc -> onTimerFinished(d1, d2));
			timer.setOwner(root);
			timer.setDelay(2000);
			timer.setRepeating(false);
			timer.start();
		} else {
			throw new IllegalStateException("item1="+item1+"; item2="+item2+"; item="+item);
		}
		refreshButtons();
	}
	
	private void onTimerFinished(ItemData d1, ItemData d2) {
		PComponent parent = d1.pnl.getParent();
		PLayout layout = (PLayout) parent.getLayout();
		Object c1 = d1.pnl.getConstraintAtParent();
		Object c2 = d2.pnl.getConstraintAtParent();
		int idx1 = allItems.indexOf(d1);
		int idx2 = allItems.indexOf(d2);
		allItems.set(idx2, d1);
		allItems.set(idx1, d2);
		layout.removeChild(d1.pnl);
		layout.removeChild(d2.pnl);
		layout.addChild(d2.pnl, c1);
		layout.addChild(d1.pnl, c2);
		
		item1 = null;
		item2 = null;
		refreshButtons();
	}
	
	private void refreshButtons() {
		for (int i = 0; i < allBtns.size(); i++) {
			ItemData item = allItems.get(i);
			PButton btn = allBtns.get(i);
			
			String txt;
			boolean enabled;
			if (item2 != null) {
				enabled = false;
				txt = "wait";
			} else if (item1 == item) {
				enabled = false;
				txt = "selected";
			} else if (item1 == null) {
				enabled = true;
				txt = "click here!";
			} else {
				enabled = true;
				txt = "switch items";
			}
			btn.setContent(new PLabel(txt));
			btn.setEnabled(enabled);
		}
	}
	
	private static class ItemData {
		final PPanel pnl = new PPanel();
		final PLabel lbl = new PLabel();
		final String id;
		public ItemData(String identifier) {
			id = identifier;
		}
		@Override
		public String toString() {
			return id;
		}
	}
	
}