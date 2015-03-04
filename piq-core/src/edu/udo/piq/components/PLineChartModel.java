package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PLineChartModel {
	
	public int getDataCount();
	
	public int getDataPoint(int index);
	
	public boolean canAddDataPoint(int index, Object value);
	
	public void addDataPoint(int index, Object value);
	
	public boolean canRemoveDataPoint(int index);
	
	public void removeDataPoint(int index);
	
	public boolean canSetDataPoint(int index, Object value);
	
	public void setDataPoint(int index, Object value);
	
	public PModelHistory getHistory();
	
	public void addObs(PLineChartModelObs obs);
	
	public void removeObs(PLineChartModelObs obs);
	
//	public int getLineCount();
//	
//	public int getDataCount(int lineID);
//	
//	public int getDataPoint(int lineID, int index);
//	
//	public boolean canAddDataPoint(int lineID, int index, Object value);
//	
//	public void addDataPoint(int lineID, int index, Object value);
//	
//	public boolean canRemoveDataPoint(int lineID, int index);
//	
//	public void removeDataPoint(int lineID, int index);
//	
//	public boolean canSetDataPoint(int lineID, int index, Object value);
//	
//	public void setDataPoint(int lineID, int index, Object value);
	
}