package frame;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import panel.SubtitlePanel;

@SuppressWarnings("serial")
public class TestFrame extends JFrame {
	
	public TestFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SubtitlePanel panel = SubtitlePanel.getInstance();
		add(panel);
		pack();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				TestFrame frame = new TestFrame();
				frame.setVisible(true);
			}
			
		});
	}
}
