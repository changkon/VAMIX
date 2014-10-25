package frame;

import java.awt.CardLayout;
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
import javax.swing.SwingUtilities;

import panel.AudioFilterPanel;
import panel.DownloadPanel;
import panel.MainPanel;
import panel.MediaPanel;
import panel.MediaPlayerComponentPanel;
import panel.PlaybackPanel;
import panel.VideoFilterPanel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * 
 * The JFrame which shows VAMIX program. The main JPanel uses card layout and panels are swapped when different
 * selections are clicked in the Panel Menu tab.
 * 
 */

@SuppressWarnings("serial")
public class VamixFrame extends JFrame implements ActionListener {

	private JPanel panels, mainPanel, downloadPanel;
	
	private JMenuBar menuBar;
	
	private JMenu panelMenu, mediaMenu, helpMenu;
	private JMenuItem mainPanelOption, downloadPanelOption, openMenuOption, aboutMenuOption;
	
	private MediaPlayerComponentPanel mediaPlayerComponentPanel;
	private PlaybackPanel playbackPanel;
	
	private final String MAIN = "Main";
	private final String DOWNLOAD = "Download";
	
	public VamixFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1400, 950));
		setPreferredSize(new Dimension(1400, 950));
		setResizable(false); // change later but at the moment, make it not resizable.
		
		setMenuBar();
		setJMenuBar(menuBar);
		
		mediaPlayerComponentPanel = MediaPanel.getInstance().getMediaPlayerComponentPanel();
		playbackPanel = MediaPanel.getInstance().getPlaybackPanel();
		
		panels = new JPanel(new CardLayout());
		
		downloadPanel = DownloadPanel.getInstance();
		mainPanel = MainPanel.getInstance();
		
		panels.add(mainPanel, MAIN);
		panels.add(downloadPanel, DOWNLOAD);
		
		add(panels);
		
		addListeners();
	}
	
	private void setMenuBar() {
		menuBar = new JMenuBar();
		panelMenu = new JMenu("Panel");
		
		mainPanelOption = new JMenuItem("Main");
		downloadPanelOption = new JMenuItem("Download");
		
		mediaMenu = new JMenu("Media");
		openMenuOption = new JMenuItem("Open..");
		
		helpMenu = new JMenu("Help");
		aboutMenuOption = new JMenuItem("About");
		
		panelMenu.add(mainPanelOption);
		panelMenu.add(downloadPanelOption);
		
		mediaMenu.add(openMenuOption);
		
		helpMenu.add(aboutMenuOption);
		
		menuBar.add(panelMenu);
		menuBar.add(mediaMenu);
		menuBar.add(helpMenu);
	}
	
	private void addListeners() {
		mainPanelOption.addActionListener(this);
		downloadPanelOption.addActionListener(this);
		openMenuOption.addActionListener(this);
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
		if (e.getSource() == mainPanelOption) {
			CardLayout card = (CardLayout)panels.getLayout();
			card.show(panels, MAIN);
		} else if (e.getSource() == downloadPanelOption) {
			CardLayout card = (CardLayout)panels.getLayout();
			card.show(panels, DOWNLOAD);
		} else if (e.getSource() == openMenuOption) {
			playbackPanel.playFile();
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
