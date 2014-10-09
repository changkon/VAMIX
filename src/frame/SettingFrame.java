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

@SuppressWarnings("serial")
public class SettingFrame extends JFrame implements ActionListener {
	private static SettingFrame theInstance = null;
	private MediaSetting mediaSetting;
	
	private JPanel buttonPanel = new JPanel();
	
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
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
		
		add(buttonPanel, "dock south");
		
		setButtonPanel();
		addListeners();
	}
	
	private void setButtonPanel() {
		saveButton.setBackground(Color.RED);
		
		buttonPanel.add(cancelButton, "pushx, growx");
		buttonPanel.add(saveButton);
	}
	
	private void addListeners() {
		saveButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveButton) {
			
		} else {
			dispose();
		}
	}
}
