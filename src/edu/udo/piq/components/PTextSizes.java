//package edu.udo.piq.components;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import edu.udo.piq.PBounds;
//import edu.udo.piq.PFontResource;
//import edu.udo.piq.PSize;
//import edu.udo.piq.tools.ImmutablePBounds;
//
//public class PTextSizes {
//	
//	private final PTextModelObs modelObs = new PTextModelObs() {
//		public void textChanged(PTextModel model) {
//			rebuild();
//		}
//	};
//	private final List<PBounds> boundsList;
//	private PTextModel model;
//	private PFontResource font;
//	
//	public PTextSizes() {
//		boundsList = new ArrayList<>();
//	}
//	
//	public void setFont(PFontResource font) {
//		this.font = font;
//		rebuild();
//	}
//	
//	public PFontResource getFont() {
//		return font;
//	}
//	
//	public void setModel(PTextModel model) {
//		if (getModel() != null) {
//			getModel().removeObs(modelObs);
//		}
//		this.model = model;
//		if (getModel() != null) {
//			getModel().addObs(modelObs);
//		}
//		rebuild();
//	}
//	
//	public PTextModel getModel() {
//		return model;
//	}
//	
//	public int getIndex(int x, int y) {
//		for (int i = 0; i < boundsList.size(); i++) {
//			if (boundsList.get(i).contains(x, y)) {
//				return i;
//			}
//		}
//		return -1;
//	}
//	
//	private void rebuild() {
//		PFontResource font = getFont();
//		PTextModel model = getModel();
//		if (font == null || model == null) {
//			return;
//		}
//		String text = model.getText().toString();
//		
//		int x = 0;
//		int y = 0;
//		int lineH = 0;
//		boundsList.clear();
//		for (int i = 0; i < text.length(); i++) {
//			char c = text.charAt(i);
//			PSize charSize = font.getSize(text.substring(i, i));
//			int charW = charSize.getWidth();
//			int charH = charSize.getHeight();
//			PBounds charBounds = new ImmutablePBounds(x, y, charW, charH);
//			boundsList.add(charBounds);
//			
//			if (c == '\n') {
//				x = 0;
//				y += lineH;
//			} else {
//				x += charW;
//			}
//			if (lineH < charH) {
//				lineH = charH;
//			}
//		}
//	}
//	
//}