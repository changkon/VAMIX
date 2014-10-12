package frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import setting.MediaSetting;

/**
 * Incomplete so far.
 * @author chang
 *
 */

@SuppressWarnings("serial")
public class SettingFrame extends JFrame implements ActionListener {
	private static SettingFrame theInstance = null;
	private MediaSetting mediaSetting;
	
	private JPanel buttonPanel = new JPanel();
	
	private JButton applyButton = new JButton("Apply");
	private JButton closeButton = new JButton("Close");
	
	public static SettingFrame getInstance() {
		if (theInstance == null) {
			theInstance = new SettingFrame();
		}
		return theInstance;
	}
	
	private SettingFrame() {
		super("Settings");
		setLayout(new MigLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(600, 600));
		setResizable(false);
		mediaSetting = MediaSetting.getInstance();
		
		
		add(buttonPanel, "push, grow");
		buttonPanel.setBackground(Color.BLUE);
		setButtonPanel();
		addListeners();
	}
	
	private void setButtonPanel() {
		applyButton.setBackground(Color.RED);
		
		buttonPanel.add(applyButton, "pushx, growx, align right");
		buttonPanel.add(closeButton);
	}
	
	private void addListeners() {
		applyButton.addActionListener(this);
		closeButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == applyButton) {
			
		} else {
			dispose();
		}
	}
}
