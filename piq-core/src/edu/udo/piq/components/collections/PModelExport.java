package edu.udo.piq.components.collections;

import java.util.List;

import edu.udo.piq.dnd.PDnDTransfer;

public interface PModelExport {
	
	public boolean canExport(PModel src, List<PModelIndex> indices);
	
	public PModel createExportModel(PModel src, List<PModelIndex> indices);
	
	public void finishExport(PModel src, PDnDTransfer transfer, List<PModelIndex> indices);
	
	public void abortExport(PModel src, PDnDTransfer transfer);
	
}