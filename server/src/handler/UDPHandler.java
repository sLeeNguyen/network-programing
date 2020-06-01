package handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import code.RequestCode;
import code.ResponseCode;
import helpers.Room;
import helpers.Room.Player;
import helpers.UdpConnection;
import server.TCPServer;

public class UDPHandler extends Thread {
	private DatagramPacket dataPacket;
	private DatagramSocket udpSocket;
	
	public UDPHandler(DatagramSocket udpSocket, DatagramPacket dataPacket) {
		this.udpSocket = udpSocket;
		this.dataPacket = dataPacket;
	}
	
	@Override
	public void run() {
		String data = new String(dataPacket.getData()).trim();
		JSONObject request = (JSONObject) JSONValue.parse(data);
		
		if (request != null) {
			long req_code = (long) request.get("req_code");
			
			switch((int)req_code) {
				case RequestCode.PLAYER_ACTION_REQ:
					actionHandler(request);
					break;
			
				case RequestCode.UDP_CONNECTION_REQ:
					addUDPConnection(request);
					break;
			}
		}
	}
	
	private void addUDPConnection(JSONObject request) {
		long roomId = (long) request.get("room_id");
		String name = (String) request.get("member_name");
		
		Room room = TCPServer.getRoomById((int) roomId);
		for (Player p: room.getListMember()) {
			if (p.getName().equals(name)) {
				System.out.println("Addr: " + dataPacket.getAddress() + " : " + dataPacket.getPort() );
				p.getConnections().setUdpConnection(new UdpConnection(dataPacket.getAddress(), dataPacket.getPort()));
				break;
			}
		}
 	}
	
	
	/**
	 * Main: Game
	 * 
	 * move, shoot
	 * */
	private void actionHandler(JSONObject request) {
		int roomId = (int) (long) request.get("room_id");
		int playerId = (int) (long) request.get("player_id");
		JSONArray data = (JSONArray) request.get("data");
		
		Room room = TCPServer.getRoomById(roomId);
		String dataString = makeJSONDataString(ResponseCode.SHIP_ACTION_RES, "payload", data);
		sendToAll(room, playerId, dataString);
	}
	
	private void sendToAll(Room room, Integer senderId, String data) {
		byte[] dataBytes = data.getBytes();
		
		if (senderId == null) {
			for (Player p: room.getListMember()) {
				sendPaket(dataBytes, p.getConnections().getUdpConnection());
			}
		} else {
			for (Player p: room.getListMember()) {
				if (p.getPlayerId() != senderId) {
					sendPaket(dataBytes, p.getConnections().getUdpConnection());
				}
			}
		}
	}
	
	private void sendPaket(byte[] dataBytes, UdpConnection udpClient) {
		try {
			if (udpClient == null) return;
			DatagramPacket dataPacket = new DatagramPacket(dataBytes, dataBytes.length, udpClient.getAddress(), udpClient.getClientPort());
			udpSocket.send(dataPacket);
		} catch (IOException e) {
			// pass
		}
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONDataString(int udpCode, String dataName, Object dataValue) {
		JSONObject response = new JSONObject();
		
		response.put("udp_code", udpCode);
		if (dataName != null && dataValue != null) response.put(dataName, dataValue);
		
		return response.toJSONString();
	}
}
