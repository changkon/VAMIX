package frame;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import panel.DownloadPanel;
import panel.MainPanel;
import panel.MediaPanel;

/**
 * 
 * The JFrame which shows VAMIX program. The main JPanel uses card layout and panels are swapped when different
 * selections are clicked in the Panel Menu tab.
 * 
 */

@SuppressWarnings("serial")
public class VamixFrame extends JFrame implements ActionListener {

	private JPanel panels = new JPanel(new CardLayout());
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu panelMenu = new JMenu("Panel");
	private JMenuItem mainPanelOption = new JMenuItem("Main"); 
	private JMenuItem downloadPanelOption = new JMenuItem("Download");
	
	private JMenu mediaMenu = new JMenu("Media");
	private JMenuItem openMenuOption = new JMenuItem("Open..");
	
	private MainPanel mainPanel = MainPanel.getInstance();
	private final String MAIN = "Main";
	
	private DownloadPanel downloadPanel = DownloadPanel.getInstance();
	private final String DOWNLOAD = "Download";
	
	public VamixFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1270, 950));
		setPreferredSize(new Dimension(1270, 950));
		setResizable(false); // change later but at the moment, make it not resizable.
		
		setMenuBar();
		setJMenuBar(menuBar);
		
		panels.add(mainPanel, MAIN);
		panels.add(downloadPanel, DOWNLOAD);
		
		add(panels);
		
		addListeners();
	}
	
	private void setMenuBar() {
		panelMenu.add(mainPanelOption);
		panelMenu.add(downloadPanelOption);
		
		mediaMenu.add(openMenuOption);
		
		menuBar.add(panelMenu);
		menuBar.add(mediaMenu);
	}
	
	private void addListeners() {
		openMenuOption.addActionListener(this);
		mainPanelOption.addActionListener(this);
		downloadPanelOption.addActionListener(this);
		
		// Makes sure when window closes, it releases the mediaPlayer.
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				MediaPanel.getInstance().getMediaPlayer().release();
			}
			
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openMenuOption) {
			MediaPanel.getInstance().playFile();
		} else if (e.getSource() == mainPanelOption) {
			// Shows main menu panel.
			CardLayout c = (CardLayout)panels.getLayout();
			c.show(panels, MAIN);
		} else if (e.getSource() == downloadPanelOption) {
			// Shows download menu panel.
			CardLayout c = (CardLayout)panels.getLayout();
			c.show(panels, DOWNLOAD);
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
