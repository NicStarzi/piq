//package edu.udo.piq.components;
//
//public interface PTreePath {
//	
//	public static PTreePath viaNodes(Object[] nodes) {
//		return new PTreePath() {
//			public Object getObjectFrom(PTreeModel model) {
//				return null;
//			}
//		};
//	}
//	
//	public static PTreePath viaIndices(int[] indices) {
//		return null;
//	}
//	
//	public static PTreePath viaModel(PTreeModel model, Object node) {
//		return null;
//	}
//	
//	public Object getObjectFrom(PTreeModel model);
//	
//	public default Object getParentFrom(PTreeModel model) {
//		Object child = getObjectFrom(model);
//		return model.getParentOf(child);
//	}
//	
//	public default int getIndexFrom(PTreeModel model) {
//		Object child = getObjectFrom(model);
//		Object parent = model.getParentOf(child);
//		return model.getChildIndex(parent, child);
//	}
//	
//	private static class NodesTreePath implements PTreePath {
//		
//		final Object[] nodes;
//		
//		public NodesTreePath(Object[] nodes) {
//			this.nodes = nodes;
//		}
//		
//		public Object getObjectFrom(PTreeModel model) {
//			return null;
//		}
//		
//	}
//	
//}