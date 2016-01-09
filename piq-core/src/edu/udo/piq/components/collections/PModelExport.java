package edu.udo.piq.components.collections;

import java.util.List;

public interface PModelExport {
	
	public boolean canExport(PModel src, List<PModelIndex> indices);
	
	public PModel createExportModel(PModel src, List<PModelIndex> indices);
	
	public void finishExport(PModel src, List<PModelIndex> indices);
	
}