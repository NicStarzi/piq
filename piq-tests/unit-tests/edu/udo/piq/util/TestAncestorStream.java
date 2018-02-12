package edu.udo.piq.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.BorderLayoutConstraint;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PListLayout;

public class TestAncestorStream {
	
	AncestorStream<PComponent> stream;
	List<PComponent> componentHierarchy;
	PComponent leaf;
	PPanel root;
	
	private static class PanelA extends PPanel{}
	private static class PanelB extends PPanel{}
	private static class PanelC extends PPanel{}
	
	@Before
	public void setupTestEnviroment() {
		root = new PPanel();
		root.setID("root");
		root.setLayout(new PBorderLayout(root));
		
		@SuppressWarnings({ "unchecked" })
		Supplier<PPanel>[] allConstructors = new Supplier[] {
			PanelA::new, PanelB::new, PanelC::new,
		};
		
		BiConsumer<PPanel, PComponent> freeLayoutTest = (PPanel panel, PComponent child) -> {
			panel.setLayout(new PFreeLayout(panel));
			panel.addChild(child, new FreeConstraint(32, 32));
			
			panel.addChild(new PLabel(), new FreeConstraint(128, 128));
		};
		BiConsumer<PPanel, PComponent> listLayoutTest = (PPanel panel, PComponent child) -> {
			panel.setLayout(new PListLayout(panel));
			panel.addChild(child, null);
			
			panel.addChild(new PButton(), null);
		};
		BiConsumer<PPanel, PComponent> gridLayoutTest = (PPanel panel, PComponent child) -> {
			panel.setLayout(new PGridLayout(panel, 1, 2));
			panel.addChild(child, new GridConstraint(0, 0));
			
			panel.addChild(new PButton(), new GridConstraint(0, 1));
		};
		BiConsumer<PPanel, PComponent> borderLayoutTest = (PPanel panel, PComponent child) -> {
			panel.setLayout(new PBorderLayout(panel));
			panel.addChild(child, BorderLayoutConstraint.CENTER);
			
			panel.addChild(new PButton(), BorderLayoutConstraint.BOTTOM);
		};
		
		@SuppressWarnings("unchecked")
		BiConsumer<PPanel, PComponent>[] addChildFunctions = new BiConsumer[] {
			freeLayoutTest, listLayoutTest, gridLayoutTest, borderLayoutTest,
		};
		
		List<PComponent> allComponents = new ArrayList<>();
		PPanel curPnl = root;
		allComponents.add(curPnl);
		for (int i = 1; i < 20; i++) {
			Supplier<PPanel> panelConstructor = allConstructors[i % allConstructors.length];
			BiConsumer<PPanel, PComponent> layoutFunction = addChildFunctions[i % addChildFunctions.length];
			
			PPanel child = panelConstructor.get();
			child.setID(child.getClass().getSimpleName()+" #"+i);
			layoutFunction.accept(curPnl, child);
			curPnl = child;
			allComponents.add(curPnl);
		}
		
		componentHierarchy = Collections.unmodifiableList(allComponents);
		leaf = componentHierarchy.get(componentHierarchy.size() - 1);
		leaf.setID("Leaf");
		leaf.getParent().setID("Parent of leaf");
		stream = leaf.getAncestors();
	}
	
	@Test
	public void testHierarchy() {
		for (int i = componentHierarchy.size() - 1; i >= 0; i--) {
			Assert.assertTrue(stream.hasRemaining());
			PComponent fromList = componentHierarchy.get(i);
			PComponent fromStream = stream.getNext();
			Assert.assertSame(fromList, fromStream);
		}
		Assert.assertFalse(stream.hasRemaining());
	}
	
	@Test
	public void testAccumulateSize() {
		int sizeFromList = componentHierarchy.size();
		int sizeFromStream = stream.accumulateInt(0, c -> 1, Integer::sum);
		Assert.assertEquals(sizeFromList, sizeFromStream);
	}
	
	@Test
	public void test3() {
		System.out.println("test3() stream="+stream);
	}
	
}
