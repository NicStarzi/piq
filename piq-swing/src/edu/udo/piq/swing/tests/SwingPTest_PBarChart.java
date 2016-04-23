package edu.udo.piq.swing.tests;

import edu.udo.piq.PColor;
import edu.udo.piq.components.charts.PBarChart;
import edu.udo.piq.components.charts.PBarChartModel;
import edu.udo.piq.components.containers.PSizeTestArea;
import edu.udo.piq.components.defaults.DefaultPBarChartModel;

public class SwingPTest_PBarChart extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PBarChart();
	}
	
	protected void buildGUI() {
		PSizeTestArea sizeTest = new PSizeTestArea();
		sizeTest.setBackgroundColor(PColor.GREY75);
		root.setBody(sizeTest);
		
		PBarChart chart = new PBarChart();
		PBarChartModel model = new DefaultPBarChartModel(10);
		chart.setModel(model);
		model.setBarValue(0, 5);
		model.setBarValue(1, 7);
		model.setBarValue(2, 11);
		model.setBarValue(3, 6);
		model.setBarValue(4, 9);
		model.setBarValue(5, 4);
		model.setBarValue(6, 7);
		model.setBarValue(7, 6);
		model.setBarValue(8, 10);
		model.setBarValue(9, 8);
		
		sizeTest.setContent(chart);
	}
}