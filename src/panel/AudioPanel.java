package panel;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import res.MediaIcon;
import component.Playback;

@SuppressWarnings("serial")
public class AudioPanel extends JPanel implements ActionListener {
	private static AudioPanel theInstance = null;
	
	private JPanel contentPanel, buttonPanel, audioFirstPagePanel, audioSecondPagePanel;
	
	public final String audioFirstPageString = "First Page";
	public final String audioSecondPageString = "Second Page";
	
	private JButton leftButton, rightButton;
	
	public static AudioPanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioPanel();
		}
		return theInstance;
	}
	
	private AudioPanel() {
		setLayout(new MigLayout());
		setContentPanel();
		setButtonPanel();
		addListeners();
		
		add(contentPanel, "push, grow");
		add(buttonPanel, "south");
	}
	
	private void setContentPanel() {
		contentPanel = new JPanel(new CardLayout());
		audioFirstPagePanel = AudioFirstPagePanel.getInstance();
		audioSecondPagePanel = AudioSecondPagePanel.getInstance();
		
		contentPanel.add(audioFirstPagePanel, audioFirstPageString);
		contentPanel.add(audioSecondPagePanel, audioSecondPageString);
	}
	
	private void setButtonPanel() {
		buttonPanel = new JPanel(new MigLayout());
		
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		
		leftButton = new JButton(mediaIcon.getIcon(Playback.LEFT));
		rightButton = new JButton(mediaIcon.getIcon(Playback.RIGHT));
		
		buttonPanel.add(leftButton, "align left");
		buttonPanel.add(rightButton, "push, align right");
	}
	
	private void addListeners() {
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		
		leftButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	leftButton.setBorderPainted(true);
	            } else {
	            	leftButton.setBorderPainted(false);
	            }
	        }
	    });
		
		rightButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	rightButton.setBorderPainted(true);
	            } else {
	            	rightButton.setBorderPainted(false);
	            }
	        }
	    });
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CardLayout card = (CardLayout)contentPanel.getLayout();
		if (e.getSource() == leftButton) {
			card.previous(contentPanel);
		} else if (e.getSource() == rightButton) {
			card.next(contentPanel);
		}
	}
}
