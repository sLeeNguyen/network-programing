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
	private static Map<Integer, Socket> listConnection;
	
	public TCPServer(int port) {
		this.port = port;
		listRoom = new ArrayList<Room>();
		listConnection = new HashMap<Integer, Socket>();
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
	
	public static void addClientConnection(Integer clientID, Socket socketClient) {
		listConnection.put(clientID, socketClient);
	}
	
	public static boolean hasRoom(String name) {
		for (Room room: listRoom) {
			if (name.equals(room.getRoomName())) return true;
		}
		return false;
	}
}
