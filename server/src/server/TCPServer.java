package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;

import handler.TCPHandler;
import helpers.PairConnection;
import helpers.Room;
import helpers.UdpConnection;

public class TCPServer extends Thread {
	private int port;
	private static Map<Integer, Room> listRoom;
	private static Map<String, PairConnection> listConnection;
	
	public TCPServer(int port) {
		this.port = port;
		listRoom = new HashMap<Integer, Room>();
		listConnection = new HashMap<>();
	}
	
	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);

			while (true) {
				Socket socketClient = serverSocket.accept();
				System.out.println("Accept new connection from client. NumOfRooms: " + listRoom.size() + " | NumOfConnections: " + listConnection.size());
				TCPHandler handler = new TCPHandler(socketClient);
				handler.start();
			}
			
		} catch (IOException e) {
			System.out.println("Loi tao server socket");
		}
	}
	
	public static boolean checkRoomId(Integer roomId) {
		return listRoom.containsKey(roomId);
	}
	
	public static void addNewRoom(Integer roomId, Room newRoom) {
		listRoom.put(roomId, newRoom);
	}
	
	public static Room hasRoom(String name) {
		for (Room room: listRoom.values()) {
			if (room.getRoomName().equals(name)) return room;
		}
		return null;
	}
	
	public static Room getRoomById(Integer roomId) {
		return listRoom.get(roomId);
	}
	
	public static void deleteRoom(Integer roomId) {
		listRoom.remove(roomId);
	}
	
	public static int getListRoomSize() {
		return listRoom.size();
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getRoomArrayInfor() {
		JSONArray roomArr = new JSONArray();
		
		for (Room room: listRoom.values()) {
			roomArr.add(room.toJSONArray());
		}
		
		return roomArr;
	}
	
	public static void addTCPClientConnection(String key, TCPHandler tcpHandler) {
		listConnection.put(key, new PairConnection(tcpHandler, null));
	}
	
	public static void addUDPClientConnection(String key, InetAddress addr, int clientPort) {
		listConnection.get(key).setUdpConnection(new UdpConnection(addr, clientPort));
	}
	
	public static TCPHandler getTCPConnection(String key) {
		return listConnection.get(key).getTcpHandler();
	}
	
	public static UdpConnection getUDPConnection(String key) {
		return listConnection.get(key).getUdpConnection();
	}
	
	public static PairConnection getConnections(String key) {
		return listConnection.get(key);
	}
}
