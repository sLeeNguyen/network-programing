package helpers;

import java.net.InetAddress;

public class UdpConnection {
	private InetAddress addr;
	private int clientPort;
	
	public UdpConnection(InetAddress addr, int clientPort) {
		this.addr = addr;
		this.clientPort = clientPort;
	}

	public InetAddress getAddress() {
		return addr;
	}

	public void setConnection(InetAddress addr, int clientPort) {
		this.addr = addr;
		this.clientPort = clientPort;
	}

	public int getClientPort() {
		return clientPort;
	}
}
