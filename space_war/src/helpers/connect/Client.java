package helpers.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import helpers.CheckAndAlert;

public class Client {
//	private static final String SERVER_IP = "192.168.0.112";
	private static final int SERVER_TCP_PORT = 8008;
	private static final int SERVER_UDP_PORT = 9009;
	private static final int TIME_OUT = 10000;
	
	private static InetAddress addr;
	
	private static Socket tcpSocketClient;
	private static DatagramSocket udpSocketClient;
	private static PrintStream pr;
	private static BufferedReader br;
	
	public Client() {
		
		try {
			SocketAddress socketAddr = new InetSocketAddress("localhost", SERVER_TCP_PORT);
			tcpSocketClient = new Socket();
			tcpSocketClient.connect(socketAddr, TIME_OUT);
			
			addr = InetAddress.getByName("localhost");
			
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Connection failed!");
		}
	}
	
	public static class TCPClient extends Thread {
		private Consumer<String> handleResponse;
		private boolean isRunning;
		
		public TCPClient(Consumer<String> handleResponse) {
			this.handleResponse = handleResponse;
			this.isRunning = true;
		}
		
		@Override
		public void run() {
			try {
				String s_res = br.readLine();
				while (isRunning) {
					handleResponse.accept(s_res);
					s_res = br.readLine();
				}
				
			} catch (IOException e) {
				closeConnection();
				e.printStackTrace();
			}
		}
		
		public void stopThread() {
			isRunning = false;
		}
	}
	
	public static class UDPClient extends Thread {
		private Consumer<String> handleResponse;
		private boolean isRunning;
		
		public UDPClient(Consumer<String> handleResponse) {
			this.handleResponse = handleResponse;
			this.isRunning = true;
		}
		
		@Override
		public void run() {
			try {
				String s_res = receiveData();
				while (isRunning) {
						handleResponse.accept(s_res);
						s_res = receiveData();
				}		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private String receiveData() throws IOException {
			byte[] temp = new byte[1024];
			DatagramPacket dataPacket = new DatagramPacket(temp, temp.length);
			udpSocketClient.receive(dataPacket);
			return new String(dataPacket.getData()).trim();
		}
		
		public void stopThread() {
			isRunning = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Integer sendLoginRequestAndHandleResponse(String username, String password) {
		JSONObject request = new JSONObject();
		request.put("req_code", 11);
		request.put("username", username);
		request.put("password", password);
	
		try {
			pr = new PrintStream(tcpSocketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			br = new BufferedReader(new InputStreamReader(tcpSocketClient.getInputStream()));
			String s_res = br.readLine();
			
			JSONObject response = (JSONObject) JSONValue.parse(s_res);
			long status = (long) response.get("status");
			
			if (status == 0) {
				long error_code = (long) response.get("error_code");
				String message = (String) response.get("message");
				
				CheckAndAlert.alertErrorMessage((int)error_code, message);
				closeConnection();
				return 0;
			}
			
			long id = (long) response.get("user_id");
			return (int) id;
			
		} catch (Exception e) {
			closeConnection();
			e.printStackTrace();
		}
		
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean sendRegistrationRequestAndHandleResponse(String username, String password) {
		JSONObject request = new JSONObject();
		request.put("req_code", 12);
		request.put("username", username);
		request.put("password", password);
		
		try {
			PrintStream pr = new PrintStream(tcpSocketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(tcpSocketClient.getInputStream()));
			String s_res = br.readLine();
			
			JSONObject response = (JSONObject) JSONValue.parse(s_res);
			long status = (long) response.get("status");
			
			if (status == 0) {
				long error_code = (long) response.get("error_code");
				String message = (String) response.get("message");
				
				CheckAndAlert.alertErrorMessage((int)error_code, message);
				return false;
			}
			
			return true;
			
		} catch (IOException e) {
			closeConnection();
			CheckAndAlert.alertErrorMessage("Connection failed!");
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	public static void sendRoomCreationRequest(String roomName, String roomOwner, int roomSize, String roomPass, String shipName) {
		JSONObject request = new JSONObject();
		request.put("req_code", 21);
		request.put("room_name", roomName);
		request.put("room_owner", roomOwner);
		request.put("size", roomSize);
		request.put("ship", shipName);
		if (roomPass != null && !roomPass.isEmpty()) request.put("room_pass", roomPass);
		
		pr.println(request.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public static void sendJoinRoomRequest(String roomName, String roomPass, String memberName, String shipName) {
		JSONObject request = new JSONObject();
		request.put("req_code", 22);
		request.put("room_name", roomName);
		request.put("member_name", memberName);
		request.put("ship", shipName);
		if (roomPass != null && !roomPass.isEmpty()) request.put("room_pass", roomPass);
		
		pr.println(request.toJSONString());	
	}

	@SuppressWarnings("unchecked")
	public static void sendPlayGameRequest() {
		JSONObject request = new JSONObject();
		request.put("req_code", 23);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendMessage(String sender, String message) {
		JSONArray data = new JSONArray();
		data.add(sender);
		data.add(message);
		
		JSONObject request = new JSONObject();
		request.put("req_code", 24);
		request.put("sender", sender);
		request.put("data", data);
		
		pr.println(request.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public static void sendReadyRequest(int playerId, int status) {
		// 0: unready, 1: ready
		JSONObject request = new JSONObject();
		request.put("req_code", 25);
		JSONArray dataArr = new JSONArray();
		dataArr.add(playerId);
		dataArr.add(status);
		request.put("player_id", playerId);
		request.put("data", dataArr);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendLeaveRoomRequest(int playerId) {
		JSONObject request = new JSONObject();
		request.put("req_code", 26);
		request.put("player_id", playerId);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendClearRoomRequest() {
		JSONObject request = new JSONObject();
		request.put("req_code", 27);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendPlayerDeadRequest(int playerId) {
		JSONObject request = new JSONObject();
		request.put("req_code", 42);
		request.put("player_id", playerId);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendEnemyDeadRequest(int enemyId, int killerId) {
		JSONObject request = new JSONObject();
		request.put("req_code", 43);
		request.put("enemy_id", enemyId);
		request.put("killer_id", killerId);
		
		pr.println(request.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	public static void sendPlayerActionData(int roomId, int shipId, double layoutX, int action) {
		JSONObject request = new JSONObject();
		JSONArray j_array = new JSONArray();
		
		j_array.add(shipId);
		j_array.add(layoutX);
		j_array.add(action);
		request.put("req_code", 41);
		request.put("player_id", shipId);
		request.put("room_id", roomId);
		request.put("data", j_array);
		
		sendPacket(request.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public static void sendUdpConnection(int roomId, String name) {
		if (udpSocketClient == null)
			try {
				udpSocketClient = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		
		JSONObject request = new JSONObject();
		request.put("req_code", 60);
		request.put("room_id", roomId);
		request.put("member_name", name);
		
		sendPacket(request.toJSONString());
	}
	
	private static void sendPacket(String data) {
		try {
			byte[] dataBytes = data.getBytes();
			DatagramPacket dataPacket = new DatagramPacket(dataBytes, dataBytes.length, addr, SERVER_UDP_PORT);
			udpSocketClient.send(dataPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		if (tcpSocketClient != null)
			try {
				tcpSocketClient.close();
				br.close(); pr.close();
			} catch (Exception e) {
				//pass
			}
	}
}