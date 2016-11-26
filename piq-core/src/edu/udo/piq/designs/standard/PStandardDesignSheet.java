package edu.udo.piq.designs.standard;

import java.util.HashSet;
import java.util.Set;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPDesignSheet;
import edu.udo.piq.util.DepthFirstDescendantIterator;

public class PStandardDesignSheet extends AbstractPDesignSheet implements PDesignSheet {
	
	private final Set<PCompFilter> filterListRender = new HashSet<>();
	private final Set<PCompFilter> filterListLayout = new HashSet<>();
	private final PStandardLabelDesign dLabel = new PStandardLabelDesign(this);
	private final PStandardDesign[] designs = new PStandardDesign[] {
		dLabel,
	};
	
	public PDesign getDesignFor(PComponent component) {
		for (int i = 0; i < designs.length; i++) {
			if (designs[i].includeComponent(component)) {
				return designs[i];
			}
		}
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
	public PLayoutDesign getDesignFor(PReadOnlyLayout layout) {
		return PLayoutDesign.NULL_ATTRIBUTE_DESIGN;
	}
	
	public PStandardLabelDesign getLabelDesign() {
		return dLabel;
	}
	
	protected void addReRenderFilter(PCompFilter filter) {
		filterListRender.add(filter);
	}
	
	protected void addReLayoutFilter(PCompFilter filter) {
		filterListLayout.add(filter);
	}
	
	protected void reRenderAll(PComponent from) {
		if (filterListRender.isEmpty()) {
			return;
		}
		PRoot root = from.getRoot();
		for (PComponent comp : new DepthFirstDescendantIterator(from)) {
			for (PCompFilter filter : filterListRender) {
				if (filter.includeComponent(comp)) {
					root.reRender(comp);
					continue;
				}
			}
		}
		filterListRender.clear();
	}
	
	protected void reLayOutAll(PComponent from) {
		if (filterListLayout.isEmpty()) {
			return;
		}
		for (PComponent comp : new DepthFirstDescendantIterator(from)) {
			for (PCompFilter filter : filterListLayout) {
				if (filter.includeComponent(comp)) {
					if (comp.getParent() != null) {
						comp.getParent().getLayout().layOut();
					}
					continue;
				}
			}
		}
		filterListLayout.clear();
	}
	
}