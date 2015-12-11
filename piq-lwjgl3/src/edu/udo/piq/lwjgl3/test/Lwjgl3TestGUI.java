package edu.udo.piq.lwjgl3.test;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.lwjgl3.Lwjgl3PRoot;

public class Lwjgl3TestGUI {
	
	public static void main(String[] args) {
		new Lwjgl3TestGUI();
	}
	
	private final Lwjgl3PRoot root;
	
	public Lwjgl3TestGUI() {
		root = new Lwjgl3PRoot(100, 100, 640, 480);
		
		PPanel body = new PPanel() {
			public void defaultRender(PRenderer renderer) {
				renderer.setColor(PColor.RED);
				renderer.drawQuad(getBounds());
			}
		};
		root.setBody(body);
		
		root.startUpdateLoop();
	}
	
}