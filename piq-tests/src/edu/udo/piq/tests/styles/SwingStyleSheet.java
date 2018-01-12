package edu.udo.piq.tests.styles;

import java.awt.RenderingHints;

import edu.udo.piq.PRoot;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.style.DefaultPStyleSheet;
import edu.udo.piq.style.PStyleComponent;
import edu.udo.piq.swing.SwingPRoot;

public class SwingStyleSheet extends DefaultPStyleSheet {
	
	protected final SwingStylePPanel stylePanel = new SwingStylePPanel();
	protected final SwingStylePLabel styleLabel = new SwingStylePLabel();
	protected final SwingStylePButton styleButton = new SwingStylePButton();
	protected final SwingStylePCheckBox styleCheckBox = new SwingStylePCheckBox();
	protected final SwingStylePSlider styleSlider = new SwingStylePSlider();
	
	public SwingStyleSheet() {
		setStyleFor(PPanel.class, this::getPanelStyle);
		setStyleFor(PLabel.class, this::getLabelStyle);
		setStyleFor(PButton.class, this::getButtonStyle);
		setStyleFor(PCheckBox.class, this::getCheckBoxStyle);
		setStyleFor(PSlider.class, this::getSliderStyle);
	}
	
	public PStyleComponent getPanelStyle() {
		return stylePanel;
	}
	
	public PStyleComponent getLabelStyle() {
		return styleLabel;
	}
	
	public PStyleComponent getButtonStyle() {
		return styleButton;
	}
	
	public PStyleComponent getCheckBoxStyle() {
		return styleCheckBox;
	}
	
	public PStyleComponent getSliderStyle() {
		return styleSlider;
	}
	
	@Override
	public void onAddedToRoot(PRoot root) {
		super.onAddedToRoot(root);
		if (root instanceof SwingPRoot) {
			SwingPRoot swingRoot = (SwingPRoot) root;
			swingRoot.addRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			swingRoot.addRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}
	
}