package panel;

import java.awt.CardLayout;

import javax.swing.JPanel;

/**
 * Panel which contains all the video filter panels. Used in mainPanel. {@link panel.MainPanel}
 * @author chang
 *
 */

@SuppressWarnings("serial")
public class VideoFilterPanel extends JPanel {
	private static VideoFilterPanel theInstance = null;
	
	private JPanel textEditPanel, fadeFilterPanel, subtitlePanel;
	
	public final String FADEFILTERSTRING = "Fade Filter";
	public final String TEXTEDITSTRING = "Text Edit";
	public final String SUBTITLESTRING = "Subtitle";
	
	public static VideoFilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new VideoFilterPanel();
		}
		return theInstance;
	}
	
	private VideoFilterPanel() {
		setLayout(new CardLayout());
		
		textEditPanel = TextEditPanel.getInstance();
		fadeFilterPanel = FadeFilterPanel.getInstance();
		subtitlePanel = SubtitlePanel.getInstance();
		
		add(textEditPanel, TEXTEDITSTRING);
		add(fadeFilterPanel, FADEFILTERSTRING);
		add(subtitlePanel, SUBTITLESTRING);
	}
}
