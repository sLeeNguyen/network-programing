package helpers.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import helpers.CheckAndAlert;

public class Client {
	private static final String serverIP = "192.168.0.105";
	private static final int serverPort = 8008;
	
	private static Socket socketClient;
	private static PrintStream pr;
	private static BufferedReader br;
	
	public Client() {
		try {
			Client.socketClient = new Socket(serverIP, serverPort);
		} catch (IOException e) {
			closeConnection();
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int sendLoginRequestAndHandleResponse(String username, String password) {
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
				return 0;
			}
			
			String data = (String) response.get("data");
			if (data != null) {
				int userID = Integer.parseInt(data);
				return userID;
			}
			
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
	public static boolean sendRoomCreationRequestAndHAndleResponse(String roomName, String roomMaster, int roomSize, String roomPass) {
		JSONObject request = new JSONObject();
		request.put("req_code", 21);
		request.put("room_name", roomName);
		request.put("room_master", roomMaster);
		request.put("size", roomSize);
		if (roomPass != null && !roomPass.isEmpty()) request.put("room_pass", roomPass);
		
		try {
			pr.println(request.toJSONString());
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
	
	public static void closeConnection() {
		CheckAndAlert.alertErrorMessage("Ngắt kết nối tới máy chủ. Hãy thử lại!");
		if (socketClient != null)
			try {
				socketClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}