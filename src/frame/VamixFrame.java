package frame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import panel.AudioFilterPanel;
import panel.DownloadPanel;
import panel.FadeFilterPanel;
import panel.MediaPanel;
import panel.MediaPlayerComponentPanel;
import panel.PlaybackPanel;
import panel.SubtitlePanel;
import panel.TextEditPanel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * 
 * The JFrame which shows VAMIX program. The main JPanel uses card layout and panels are swapped when different
 * selections are clicked in the Panel Menu tab.
 * 
 */

@SuppressWarnings("serial")
public class VamixFrame extends JFrame implements ActionListener {

	private JPanel vamixPanel;
	
	private JMenuBar menuBar;
	
	private JMenu mediaMenu, helpMenu;
	private JMenuItem openMenuOption, aboutMenuOption;
	
//	private JMenu toolMenu = new JMenu("Tools");
//	private JMenuItem settingMenuOption = new JMenuItem("Settings");
	
	private MediaPlayerComponentPanel mediaPlayerComponentPanel;
	private PlaybackPanel playbackPanel;
	
	private JTabbedPane tabbedPane;
	
	public VamixFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1500, 600));
		setPreferredSize(new Dimension(1500, 600));
		setResizable(false); // change later but at the moment, make it not resizable.
		
		setMenuBar();
		setJMenuBar(menuBar);
		setVamixPanel();
		
		add(vamixPanel);
		
		addListeners();
	}
	
	private void setMenuBar() {
		menuBar = new JMenuBar();
		mediaMenu = new JMenu("Media");
		openMenuOption = new JMenuItem("Open..");
		
		helpMenu = new JMenu("Help");
		aboutMenuOption = new JMenuItem("About");
		
		mediaMenu.add(openMenuOption);
		
//		toolMenu.add(settingMenuOption);
		
		helpMenu.add(aboutMenuOption);
		
		menuBar.add(mediaMenu);
//		menuBar.add(toolMenu);
		menuBar.add(helpMenu);
	}
	
	private void setVamixPanel() {
		vamixPanel = new JPanel(new MigLayout());
		
		// Get all the panels
		MediaPanel mediaPanel = MediaPanel.getInstance();
		DownloadPanel downloadPanel = DownloadPanel.getInstance();
		AudioFilterPanel audioPanel = AudioFilterPanel.getInstance();
		TextEditPanel textEditPanel = TextEditPanel.getInstance();
		SubtitlePanel subtitlePanel = SubtitlePanel.getInstance();
		FadeFilterPanel fadePanel = FadeFilterPanel.getInstance();
		
		mediaPlayerComponentPanel = MediaPanel.getInstance().getMediaPlayerComponentPanel();
		playbackPanel = MediaPanel.getInstance().getPlaybackPanel();
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Download", downloadPanel);
		tabbedPane.addTab("Audio", audioPanel);
		tabbedPane.addTab("Fade Filter", fadePanel);
		tabbedPane.addTab("Text", textEditPanel);
		tabbedPane.addTab("Subtitle", subtitlePanel);
		
		vamixPanel.add(mediaPanel, "width 1000px, height 600px, push, grow");
		vamixPanel.add(tabbedPane, "push, grow");
	}
	
	private void addListeners() {
		openMenuOption.addActionListener(this);
//		settingMenuOption.addActionListener(this);
		aboutMenuOption.addActionListener(this);
		
		// Makes sure when window closes, it releases the mediaPlayer.
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mediaPlayerComponentPanel.getMediaPlayer().release();
			}
			
		});
		
		EmbeddedMediaPlayerComponent mediaPlayerComponent = mediaPlayerComponentPanel.getMediaPlayerComponent();
		// Go to full screen when double clicked.
		mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JFrame vamixFrame = (JFrame)SwingUtilities.getWindowAncestor(MediaPanel.getInstance());
				if (e.getClickCount() == 2) {
					FullScreenMediaPlayerFrame fullScreen = new FullScreenMediaPlayerFrame(vamixFrame);
					fullScreen.setFullScreen();
				}
			}
			
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openMenuOption) {
			playbackPanel.playFile();
//		} else if (e.getSource() == settingMenuOption) {
//			SettingFrame settingFrame = SettingFrame.getInstance();
//			settingFrame.setVisible(true);
		} else if (e.getSource() == aboutMenuOption) {
			ReadmeFrame readmeFrame = ReadmeFrame.getInstance();
			readmeFrame.setVisible(true);
		}
	}
	
	/** Initialise
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				VamixFrame vamixFrame = new VamixFrame();
				vamixFrame.setVisible(true);
			}
			
		});
	}
}
