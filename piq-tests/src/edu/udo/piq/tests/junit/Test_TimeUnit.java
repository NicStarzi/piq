package edu.udo.piq.tests.junit;

import static edu.udo.piq.TimeUnit.HOURS;
import static edu.udo.piq.TimeUnit.MILLI_SECONDS;
import static edu.udo.piq.TimeUnit.MINUTES;
import static edu.udo.piq.TimeUnit.SECONDS;

import org.junit.Assert;
import org.junit.Test;

import edu.udo.piq.TimeUnit;

public class Test_TimeUnit {
	
	private final double testTimeMil = 12345.0;
	private final double testTimeSec = testTimeMil / 1000.0;
	private final double testTimeMin = testTimeSec / 60.0;
	private final double testTimeHour = testTimeMin / 60.0;
	
	@Test
	public void testIdentityTransform() {
		double test = 123.0;
		for (TimeUnit tu : TimeUnit.ALL) {
			double check = tu.convertTo(tu, test);
			Assert.assertEquals(test, check, 0.0001);
		}
	}
	
	@Test
	public void testMilToSec() {
		double timeSec = MILLI_SECONDS.convertTo(SECONDS, testTimeMil);
		Assert.assertEquals(testTimeSec, timeSec, 0.0001);
	}
	
	@Test
	public void testMilToMin() {
		double timeSec = MILLI_SECONDS.convertTo(MINUTES, testTimeMil);
		Assert.assertEquals(testTimeMin, timeSec, 0.0001);
	}
	
	@Test
	public void testMilToHour() {
		double timeSec = MILLI_SECONDS.convertTo(HOURS, testTimeMil);
		Assert.assertEquals(testTimeHour, timeSec, 0.0001);
	}
	
	@Test
	public void testSecToMil() {
		double timeSec = SECONDS.convertTo(MILLI_SECONDS, testTimeSec);
		Assert.assertEquals(testTimeMil, timeSec, 0.0001);
	}
	
	@Test
	public void testSecToMin() {
		double timeSec = SECONDS.convertTo(MINUTES, testTimeSec);
		Assert.assertEquals(testTimeMin, timeSec, 0.0001);
	}
	
	@Test
	public void testSecToHour() {
		double timeSec = SECONDS.convertTo(HOURS, testTimeSec);
		Assert.assertEquals(testTimeHour, timeSec, 0.0001);
	}
	
	@Test
	public void testMinToMil() {
		double timeSec = MINUTES.convertTo(MILLI_SECONDS, testTimeMin);
		Assert.assertEquals(testTimeMil, timeSec, 0.0001);
	}
	
	@Test
	public void testMinToSec() {
		double timeSec = MINUTES.convertTo(SECONDS, testTimeMin);
		Assert.assertEquals(testTimeSec, timeSec, 0.0001);
	}
	
	@Test
	public void testMinToHour() {
		double timeSec = MINUTES.convertTo(HOURS, testTimeMin);
		Assert.assertEquals(testTimeHour, timeSec, 0.0001);
	}
	
	@Test
	public void testHourToMil() {
		double timeSec = HOURS.convertTo(MILLI_SECONDS, testTimeHour);
		Assert.assertEquals(testTimeMil, timeSec, 0.0001);
	}
	
	@Test
	public void testHourToSec() {
		double timeSec = HOURS.convertTo(SECONDS, testTimeHour);
		Assert.assertEquals(testTimeSec, timeSec, 0.0001);
	}
	
	@Test
	public void testHourToMin() {
		double timeSec = HOURS.convertTo(MINUTES, testTimeHour);
		Assert.assertEquals(testTimeMin, timeSec, 0.0001);
	}
	
}
