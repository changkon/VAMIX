package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Opens the readme document in a JTextArea. This file is accessed from VamixFrame. {@link frame.VamixFrame}
 * @author chang
 *
 */

@SuppressWarnings("serial")
public class ReadmeFrame extends JFrame {
	private static ReadmeFrame theInstance = null;
	private JTextArea text = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(text);
	
	public static ReadmeFrame getInstance() {
		if (theInstance == null) {
			theInstance = new ReadmeFrame();
		}
		return theInstance;
	}
	
	private ReadmeFrame() {
		super("README");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 850));
		setMinimumSize(new Dimension(900, 700));
		
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEditable(false);
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(scrollPane, BorderLayout.CENTER);
		addText();
	}
	
	private void addText() {
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("README.md");
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
			
			String line = "";
			
			while ((line = buffer.readLine()) != null) {
				text.append(line + "\n");
			}
			
			buffer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
