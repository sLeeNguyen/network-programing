package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	private int port;
	
	public TCPServer(int port) {
		this.port = port;
	}
	
	public void start() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);

			while (true) {
				System.out.println("Server is listening ...");
				Socket socketClient = serverSocket.accept();
				TCPHandler handler = new TCPHandler(socketClient);
				handler.start();
			}
			
		} catch (IOException e) {
			System.out.println("Loi tao server socket");
			e.printStackTrace();
		}
	}
}
