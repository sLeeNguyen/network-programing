package helpers.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.function.Consumer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import helpers.CheckAndAlert;

public class Client {
	private static final String SERVER_IP = "192.168.0.112";
	private static final int SERVER_PORT = 8008;
	private static final int TIME_OUT = 10000;
	
	private static Socket socketClient;
	private static PrintStream pr;
	private static BufferedReader br;
	
	public Client() {
			try {
				socketClient = new Socket(SERVER_IP, SERVER_PORT);
			} catch (IOException e) {
				CheckAndAlert.alertErrorMessage("Kết nối tới server thất bại");
				e.printStackTrace();
			}
	}
	
	public static class TCPViewManager extends Thread {
		private Consumer<String> handleResponse;
		
		public TCPViewManager(Consumer<String> handleResponse) {
			this.handleResponse = handleResponse;
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					String s_res = br.readLine();
					System.out.println(s_res);
					
					handleResponse.accept(s_res);
				}
			} catch (IOException e) {
				closeConnection();
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Integer sendLoginRequestAndHandleResponse(String username, String password) {
		JSONObject request = new JSONObject();
		request.put("req_code", 11);
		request.put("username", username);
		request.put("password", password);
	
		try {
			pr = new PrintStream(socketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
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
			
		} catch (IOException e) {
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
			PrintStream pr = new PrintStream(socketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
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
			e.printStackTrace();
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	public static void sendRoomCreationRequest(String roomName, String roomMaster, int roomSize, String roomPass) {
		JSONObject request = new JSONObject();
		request.put("req_code", 21);
		request.put("room_name", roomName);
		request.put("room_master", roomMaster);
		request.put("size", roomSize);
		if (roomPass != null && !roomPass.isEmpty()) request.put("room_pass", roomPass);
		
		pr.println(request.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public static void sendJoinRoomRequest(String roomName, String roomPass, String memberName) {
		JSONObject request = new JSONObject();
		request.put("req_code", 22);
		request.put("room_name", roomName);
		request.put("member_name", memberName);
		if (roomPass != null && !roomPass.isEmpty()) request.put("room_pass", roomPass);
		
		pr.println(request.toJSONString());	
	}
	
	public static void closeConnection() {
		if (socketClient != null)
			try {
				socketClient.close();
				br.close(); pr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}