package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import handler.UDPHandler;

public class UDPServer extends Thread {
	private int port;
	
	public UDPServer(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		
		try {
			DatagramSocket udpSocket = new DatagramSocket(port);
			while (true) {
				DatagramPacket dataPacket = receivePacket(udpSocket);
				if (dataPacket != null) new UDPHandler(udpSocket, dataPacket).start();
					
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private DatagramPacket receivePacket(DatagramSocket udpSocket) throws IOException {
		byte[] temp = new byte[1024];
		DatagramPacket dataPacket = new DatagramPacket(temp, temp.length);
		udpSocket.receive(dataPacket);
		return dataPacket;
	}
}
