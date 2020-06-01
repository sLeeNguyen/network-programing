package server;

public class Main {

	public static void main(String[] args) {		
		TCPServer tcpServer = new TCPServer(Integer.parseInt(args[0]));
		UDPServer udpServer = new UDPServer(Integer.parseInt(args[1]));
		udpServer.start();
		tcpServer.start();
	}
}
