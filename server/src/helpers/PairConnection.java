package helpers;

import java.net.InetAddress;

import handler.TCPHandler;
import handler.UDPHandler;

public class PairConnection {
	private TCPHandler tcpHandler;
	private UdpConnection udpConnection;
	
	public PairConnection(TCPHandler tcpHandler, UdpConnection udpConnection) {
		this.tcpHandler = tcpHandler;
		this.udpConnection = udpConnection;
	}

	public TCPHandler getTcpHandler() {
		return tcpHandler;
	}

	public void setTcpHandler(TCPHandler tcpHandler) {
		this.tcpHandler = tcpHandler;
	}

	public UdpConnection getUdpConnection() {
		return udpConnection;
	}

	public void setUdpConnection(UdpConnection udpConnection) {
		this.udpConnection = udpConnection;
	}
}