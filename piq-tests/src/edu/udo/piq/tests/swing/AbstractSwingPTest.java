package edu.udo.piq.tests.swing;

import java.awt.EventQueue;

import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.swing.JFramePRoot;

public abstract class AbstractSwingPTest {
	
	private final JFramePRoot frameRoot = new JFramePRoot();
	protected final JCompPRoot root = frameRoot.getPRoot();
	
	public AbstractSwingPTest() {
	}
	
	public AbstractSwingPTest(int w, int h) {
		this();
		buildSwing(w, h);
	}
	
	protected final void buildSwing(int w, int h) {
		EventQueue.invokeLater(() -> {
			try {
				frameRoot.setSizeUndecorated(w, h);
				buildGUI();
				frameRoot.setVisible(true);
			} catch (Exception e1) {
				frameRoot.dispose();
				e1.printStackTrace();
			}
		});
	}
	
	protected abstract void buildGUI();
	
}