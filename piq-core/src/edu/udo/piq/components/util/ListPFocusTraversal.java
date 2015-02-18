package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.udo.piq.PComponent;

public class ListPFocusTraversal implements PFocusTraversal {
	
	private final PComponent owner;
	
	public ListPFocusTraversal(PComponent owner) {
		if (owner == null) {
			throw new NullPointerException("owner="+owner);
		}
		this.owner = owner;
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public PComponent getNext(PComponent currentFocusOwner) {
		if (getOwner().getLayout() == null || getOwner().getLayout().getChildren().isEmpty()) {
			return null;
		}
		List<PComponent> compList = new ArrayList<>(getOwner().getLayout().getChildren());
		int index = compList.indexOf(currentFocusOwner);
		if (index == -1) {
			return getDefault();
		} if (index == compList.size() - 1) {
			index = 0;
		} else {
			index += 1;
		}
		return compList.get(index);
	}
	
	public PComponent getPrevious(PComponent currentFocusOwner) {
		if (getOwner().getLayout() == null || getOwner().getLayout().getChildren().isEmpty()) {
			return null;
		}
		List<PComponent> compList = new ArrayList<>(getOwner().getLayout().getChildren());
		int index = compList.indexOf(currentFocusOwner);
		if (index == -1) {
			return getDefault();
		} if (index == 0) {
			index = compList.size() - 1;
		} else {
			index -= 1;
		}
		return compList.get(index);
	}
	
	public PComponent getInitial() {
		if (getOwner().getLayout() == null || getOwner().getLayout().getChildren().isEmpty()) {
			return null;
		}
		return getAt(0);
	}
	
	public PComponent getDefault() {
		return getInitial();
	}
	
	private PComponent getAt(int index) {
		Iterator<PComponent> iter = getOwner().getLayout().getChildren().iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		return iter.next();
	}
	
}