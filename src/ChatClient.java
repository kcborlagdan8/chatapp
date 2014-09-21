import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.io.*;
import java.net.*;

public class ChatClient {
	JTextArea inbox;
	JTextField outbox;
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	
	public static void main(String[] args) {
		new ChatClient();
	}
	
	public ChatClient() {
		JFrame frame = new JFrame("Simple Chat Client");
		JPanel mainPanel = new JPanel();
		inbox = new JTextArea(15, 30);
		inbox.setLineWrap(true);
		inbox.setWrapStyleWord(true);
		inbox.setEditable(false);
		JScrollPane scroller = new JScrollPane(inbox);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outbox = new JTextField(20);
		JButton sendButton = new JButton("send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(scroller);
		mainPanel.add(outbox);
		mainPanel.add(sendButton);
		setUpNetworking();
		
		Thread readerThread = new Thread( new InboxReader() );
		readerThread.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(400, 500);
		frame.setVisible(true);
	}
	
	private void setUpNetworking() {
		try {
			socket = new Socket("127.0.0.1", 5000);
			reader = new BufferedReader(new InputStreamReader( socket.getInputStream() ));
			writer = new PrintWriter( socket.getOutputStream() );
			System.out.println("Connection established");				
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
		
	public class SendButtonListener implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			try {
				writer.println(outbox.getText());
				writer.flush();
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
			outbox.setText("");
			outbox.requestFocus();
		}
	}
	
	public class InboxReader implements Runnable {
		public void run() {
			String message;
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("message: " + message);
					inbox.append(message + "\n");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
