package edu.udo.piq.tests.swing;

import java.awt.EventQueue;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.lwjgl3.GlfwPRoot;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.swing.JFramePRoot;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePColor;

public class Test_Clipping {
	
	public static void main(String[] args) {
		JFramePRoot frameRoot = new JFramePRoot();
		frameRoot.getJFrame().setTitle("Test");
		JCompPRoot root = frameRoot.getPRoot();
		Test_Clipping test = new Test_Clipping();
		
		EventQueue.invokeLater(() -> {
			try {
				frameRoot.setSizeUndecorated(480, 320);
				test.buildGui(root);
				frameRoot.setVisible(true);
			} catch (Exception e1) {
				frameRoot.dispose();
				e1.printStackTrace();
			}
		});
		
		GlfwPRoot root2 = new GlfwPRoot("Test", 480, 320);
		Test_Clipping test2 = new Test_Clipping();
		test2.buildGui(root2);
		root2.startGlfwLoop();
	}
	
	public void buildGui(PRoot root) {
		PPanel body = new PPanel();
		body.setLayout(new PAnchorLayout(body, AlignmentX.FILL, AlignmentY.FILL));
		root.setBody(body);
		
		PColor colorRed = new ImmutablePColor(1.0, 0.0, 0.0, 0.25);
		PColor colorGrn = new ImmutablePColor(0.0, 1.0, 0.0, 0.25);
		PColor colorBlu = new ImmutablePColor(0.0, 0.0, 1.0, 0.25);
		PComponent customComp = new AbstractPComponent() {
			@Override
			public void defaultRender(PRenderer renderer) {
				System.out.println("customComp.defaultRender()");
				PBounds bnds = getBounds();
				
				renderer.setClipBounds(32, 32, 256, 128);
				renderer.setColor(colorRed);
				renderer.drawQuad(bnds);
				
				renderer.setClipBounds(128, 48, 128, 256);
				renderer.setColor(colorGrn);
				renderer.drawQuad(bnds);
				
				renderer.setClipBounds(64, 128, 160, 160);
				renderer.setColor(colorBlu);
				renderer.drawQuad(bnds);
			}
			@Override
			public boolean defaultFillsAllPixels() {
				return false;
			}
		};
		body.addChild(customComp, null);
	}
}