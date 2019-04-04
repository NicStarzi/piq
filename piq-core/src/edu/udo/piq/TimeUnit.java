package edu.udo.piq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TimeUnit {
	
	MILLI_SECONDS	(1.0),
	SECONDS			(1000.0),
	MINUTES			(60 * 1000.0),
	HOURS			(60 * 60 * 1000.0),
	;
	public static final List<TimeUnit> ALL = Collections.unmodifiableList(
			Arrays.asList(TimeUnit.values()));
	public static final int COUNT = ALL.size();
	
	public final int IDX = ordinal();
	
	private final double asMs;
	
	private TimeUnit(double asMilliSeconds) {
		asMs = asMilliSeconds;
	}
	
	public double convertTo(TimeUnit targetUnit, double originalTimeValue) {
		double originalMs = originalTimeValue * asMs;
		return originalMs / targetUnit.asMs;
	}
	
}