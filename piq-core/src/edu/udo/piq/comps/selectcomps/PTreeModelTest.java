package edu.udo.piq.comps.selectcomps;

public class PTreeModelTest {
	
	public static void main(String[] args) {
		DefaultPTreeModel model = new DefaultPTreeModel();
		model.addObs(new PModelObs() {
			public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
				System.out.println("REMOVED::"+index+" == "+oldContent);
			}
			public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			}
			public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
				System.out.println("ADDED::"+index+" == "+newContent);
			}
		});
		PTreeIndex index = new PTreeIndex();
		
		model.add(index, "Root");
		model.add(index.append(0), "Dir A");
		model.add(index.append(1), "Dir B");
		model.add(index.append(2), "Dir D");
		model.add(index.append(2), "Dir C");
		
		model.add(index.append(0, 0), "Dir Aa");
		model.add(index.append(1, 0), "Dir Ba");
		model.add(index.append(1, 1), "Dir Bb");
		model.add(index.append(3, 0), "Dir Da");
		model.add(index.append(3, 1), "Dir Dc");
		model.add(index.append(3, 1), "Dir Db");
		
		model.add(index.append(0, 0, 0), "File AaA");
		model.add(index.append(1, 1, 0), "File BbA");
		model.add(index.append(2, 0), "File Ca");
		model.add(index.append(3, 0, 0), "File DaB");
		model.add(index.append(3, 0, 0), "File DaA");
		model.add(index.append(3, 1, 0), "File DbA");
		model.add(index.append(3, 2, 0), "File DcA");
		model.add(index.append(3, 2, 0), "File DcB");
		
		System.out.println(model);
		System.out.println("IndexOf(Root) = "+model.getIndexOf("Root"));
		System.out.println("IndexOf(File DaB) = "+model.getIndexOf("File DaB"));
		System.out.println("IndexOf(File CaB) = "+model.getIndexOf("File CaB"));
		
		model.remove(new PTreeIndex(new int[] {3, 1, 0}));
		System.out.println(model);
		
	}
	
}