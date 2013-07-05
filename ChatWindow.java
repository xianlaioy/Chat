package dchat;


import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 3421135597049702610L;
	public static final String DATE_FORMAT_NOW = "HH:mm:ss";

	JTextArea textArea;
	JTextField inputArea;
	JLabel label;

	ChatWindowClient client = null;

	private WindowListener windowListener = new WindowListener() {

		public void windowActivated(WindowEvent e) {}

		//Handles the event of the window becoming closed.
		public void windowClosed(WindowEvent e) {
			//Exit the application
			if(client != null)
				client.windowClosed();
		}

		//Handles the event of a user trying to close the window
		public void windowClosing(WindowEvent e) {
			//Disposing the Window
			e.getWindow().dispose();
		}

		public void windowDeactivated(WindowEvent e) {}

		public void windowDeiconified(WindowEvent e) {}

		public void windowIconified(WindowEvent e) {}

		//Handles the event of a user starting the window
		public void windowOpened(WindowEvent e) {
			if(client != null)
				client.windowOpened();
		}
		
	};
	
	private KeyListener keyListener = new KeyListener(){
		/*
		 * Handles the event of a keypress. If the key is an ENTER, the text is
		 * sent to the client and the input is cleared.
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0x0a) {
				if (inputArea.getText().length() > 0) {

					if (client != null)
						try {
							client.textAvailable(inputArea.getText() + '\n');
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						

					inputArea.setText(new String());
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	};

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	/*
	 * Main Constructor for the ChatWindow class Accepts a ChatWindowClient as
	 * parameter
	 */
	public ChatWindow(String name, ChatWindowClient cl) {

		client = cl;

		label = new JLabel("Nick: " + name);

		textArea = new JTextArea();
		inputArea = new JTextField();

		// Add a Label with nick
		getContentPane().add(label, BorderLayout.NORTH);

		// Add a scrolling text area
		textArea.setEditable(false);
		textArea.setRows(20);
		textArea.setColumns(80);
		textArea.setFont(new Font("Courier", 14, 14));

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setSize(100, 100);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		getContentPane().add(scroll, BorderLayout.CENTER);

		// Add a text input area
		inputArea.setEditable(true);
		inputArea.setColumns(80);
		inputArea.addKeyListener(keyListener);
		
		getContentPane().add(inputArea, BorderLayout.SOUTH);
		
		addWindowListener(windowListener);
		
		// Pack and set visible
		pack();
		setVisible(true);
	}

	/*
	 * Sets the current nick to the string provided
	 */
	public void setNick(String name) {
		label.setText("Nick: " + name);
	}

	/*
	 * Writes a String to the Output Text Area
	 */
	public void writeText(String text) {
				

		textArea.append(now()+" : "+text);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	/*
	 * Clears the Output Text Area
	 */
	public void clearTextArea() {
		textArea.setText(new String());
	}

	/*
	 * Clears the Input Text Area
	 */

	public void clearInputArea(){
		inputArea.setText(new String());
	}

	/*
	 * Sets the value of the ChatWindowClient
	 */
	public void setClient(ChatWindowClient cl) {
		client = cl;
	}
};
