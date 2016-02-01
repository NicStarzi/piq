package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.charts.PLineChart;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PLineChart {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PLineChart window = new SwingPTest_PLineChart();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PLineChart() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getJPanel());
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PPanel btm = new PPanel();
		btm.setLayout(new PWrapLayout(btm, ListAlignment.CENTERED_HORIZONTAL));
		bodyPnl.addChild(btm, Constraint.BOTTOM);
		
		PSlider sldAdd = new PSlider();
		sldAdd.getModel().setMinValue(0);
		sldAdd.getModel().setMaxValue(50);
		sldAdd.getModel().setValue(25);
		btm.addChild(sldAdd, null);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add Data")));
		btm.addChild(btnAdd, null);
		
		PButton btnRmv = new PButton();
		btnRmv.setContent(new PLabel(new DefaultPTextModel("Remove Data")));
		btm.addChild(btnRmv, null);
		
		PPanel cnt = new PPanel();
		cnt.setLayout(new PCentricLayout(cnt));
		bodyPnl.addChild(cnt, Constraint.CENTER);
		
		PLineChart chart = new PLineChart();
		int i = 0;
		chart.getModel().addDataPoint(i++, 5);
		chart.getModel().addDataPoint(i++, 8);
		chart.getModel().addDataPoint(i++, 12);
		chart.getModel().addDataPoint(i++, 9);
		chart.getModel().addDataPoint(i++, 11);
		chart.getModel().addDataPoint(i++, 13);
		chart.getModel().addDataPoint(i++, 18);
		chart.getModel().addDataPoint(i++, 26);
		chart.getModel().addDataPoint(i++, 3);
		chart.getModel().addDataPoint(i++, 2);
		chart.getModel().addDataPoint(i++, 2);
		chart.getModel().addDataPoint(i++, 4);
		chart.getModel().addDataPoint(i++, 3);
		chart.getModel().addDataPoint(i++, 6);
		chart.getModel().addDataPoint(i++, 5);
		chart.getModel().addDataPoint(i++, 3);
		chart.getModel().addDataPoint(i++, 2);
		chart.getModel().addDataPoint(i++, 2);
		cnt.addChild(chart, null);
		
		btnAdd.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				new Thread(new Runnable() {
					public void run() {
						int index = chart.getModel().getDataCount();
						chart.getModel().addDataPoint(index, sldAdd.getModel().getValue());
					}
				}).run();
			}
		});
		btnRmv.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				new Thread(new Runnable() {
					public void run() {
						int index = chart.getModel().getDataCount() - 1;
						if (chart.getModel().canRemoveDataPoint(index)) {
							chart.getModel().removeDataPoint(index);
						}
					}
				}).run();
			}
		});
	}
	
}