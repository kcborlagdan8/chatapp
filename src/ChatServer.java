import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	ArrayList<PrintWriter> clientOutputStreams;
	
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket socket;
		
		public ClientHandler(Socket clientSocket) {
			try {
				socket = clientSocket;
				reader = new BufferedReader(new InputStreamReader (socket.getInputStream() ));
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			String message;
			try {
				while((message = reader.readLine()) != null) {
					System.out.println("message: " + message);
					send( message );
				}
			} catch( Exception e ) {
				e.printStackTrace();
			}			
		}		
	}
	

	public static void main(String[] args) {
		new ChatServer().go();
	}
	
	public void go() {
		clientOutputStreams = new ArrayList<>();
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("Got a connection");
			}
			//serverSocket.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void send( String message ) {
		Iterator<PrintWriter> it = clientOutputStreams.iterator();
		while( it.hasNext() ) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
