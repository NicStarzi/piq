package edu.udo.piq.tests.swing;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.collections.list.PListLike;
import edu.udo.piq.components.containers.PSizeTestArea;
import edu.udo.piq.components.defaults.DefaultPListModel;

public class SwingPTest_PList3 extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PList3();
//		EventQueue.invokeLater(SwingPTest_PList3::compareWithSwing);
	}
	
	public SwingPTest_PList3() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PSizeTestArea sizeTester = new PSizeTestArea();
		root.setBody(sizeTester);
		
		DefaultPListModel nameModel = new DefaultPListModel(18000);
		char[] chars = new char[3];
		chars[0] = '.';
		chars[1] = '.';
		chars[2] = '.';
		for (char a = 'a'; a <= 'b'; a++) {
			for (char b = 'a'; b <= 'b'; b++) {
				for (char c = 'a'; c <= 'b'; c++) {
					chars[0] = a;
					chars[1] = b;
					chars[2] = c;
					nameModel.add(new String(chars));
				}
			}
		}
		
		PListLike list = new PList(nameModel);
//		PListLike list = new PListHP(nameModel);
		sizeTester.setContent(list);
	}
	
	public static void compareWithSwing() {
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		
		JPanel body = new JPanel(new BorderLayout());
		frame.setContentPane(body);
		
		DefaultListModel<String> model = new DefaultListModel<>();
		char[] chars = new char[3];
		chars[0] = '.';
		chars[1] = '.';
		chars[2] = '.';
		for (char a = 'a'; a <= 'z'; a++) {
			for (char b = 'a'; b <= 'z'; b++) {
				for (char c = 'a'; c <= 'z'; c++) {
					chars[0] = a;
					chars[1] = b;
					chars[2] = c;
					model.addElement(new String(chars));
				}
			}
		}
		JList<String> list = new JList<>(model);
		body.add(list, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
}