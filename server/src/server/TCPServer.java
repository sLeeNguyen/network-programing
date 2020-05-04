package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import handler.TCPHandler;
import helpers.Room;

public class TCPServer {
	private int port;
	private static List<Room> listRoom;
	private static Map<String, TCPHandler> listConnection;
	
	public TCPServer(int port) {
		this.port = port;
		listRoom = new ArrayList<Room>();
		listConnection = new HashMap<String, TCPHandler>();
	}
	
	public void start() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);

			while (true) {
				System.out.println("Server is listening ...");
				Socket socketClient = serverSocket.accept();
				System.out.println("Accept new connection from client.\nNumOfRoom " + listRoom.size() + "\nNumOfConnection " + listConnection.size());
				TCPHandler handler = new TCPHandler(socketClient);
				handler.start();
			}
			
		} catch (IOException e) {
			System.out.println("Loi tao server socket");
			e.printStackTrace();
		}
	}
	
	public static void addNewRoom(Room newRoom) {
		listRoom.add(newRoom);
	}
	
	public static void addClientConnection(String username, TCPHandler tcpHandler) {
		listConnection.put(username, tcpHandler);
	}
	
	public static TCPHandler getConnection(String key) {
		return listConnection.get(key);
	}
	
	public static int hasRoom(String name) {
		for (int i = 0; i < listRoom.size(); ++i) {
			if (listRoom.get(i).getRoomName().equals(name)) return i;
		}
		return -1;
	}
	
	public static Room getRoom(int index) {
		return listRoom.get(index);
	}
}
