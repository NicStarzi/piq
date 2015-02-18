package edu.udo.piq.implementation.swing;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import edu.udo.piq.PClipboard;

public class SwingPClipboard implements PClipboard {
	
	private static Clipboard getClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	public boolean store(Object object) {
		try {
			Transferable trans;
			if (object instanceof String) {
				trans = new StringSelection((String) object);
			} else {
				trans = new ObjectTransferable<Object>(object);
			}
			getClipboard().setContents(trans, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E> E load(Class<E> expectedClass) {
		try {
			Transferable transferable = getClipboard().getContents(null);
			for (DataFlavor dataFlavor : transferable.getTransferDataFlavors()) {
				Object obj = transferable.getTransferData(dataFlavor);
				if (expectedClass == null || expectedClass.isInstance(obj)) {
					return (E) obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class ObjectTransferable<E> implements Transferable {
		
		private final DataFlavor[] flavors;
		private final E obj;
		
		public ObjectTransferable(E object) throws ClassNotFoundException {
			obj = object;
			flavors = new DataFlavor[] {
				new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class="+object.getClass().getName())
			};
		}
		
//		public ObjectTransferable(E object, DataFlavor flavor) {
//			obj = object;
//			flavors = new DataFlavor[] {
//				flavor
//			};
//		}
		
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return obj;
		}
		
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}
		
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavors[0].equals(flavor);
		}
		
	}
	
}