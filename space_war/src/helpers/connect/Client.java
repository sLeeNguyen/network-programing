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
	
	public Client() {}
	
	@SuppressWarnings("unchecked")
	public static boolean sendLoginRequestAndHandleResponse(String username, String password) {
		JSONObject request = new JSONObject();
		request.put("req_code", 11);
		request.put("username", username);
		request.put("password", password);
		
		Socket socketClient = null;
		try {
			socketClient = new Socket(serverIP, serverPort);
			PrintStream pr = new PrintStream(socketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			String s_res = br.readLine();
			
			JSONObject response = (JSONObject) JSONValue.parse(s_res);
			long status = (long) response.get("status");
			
			socketClient.close();
			if (status == 0) {
				long error_code = (long) response.get("error_code");
				String message = (String) response.get("message");
				
				CheckAndAlert.alertErrorMessage((int)error_code, message);
				return false;
			}
			
			return true;
			
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Lỗi kết nối. Hãy thử lại!");
			if (socketClient != null)
				try {
					socketClient.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean sendRegistrationRequestAndHandleResponse(String username, String password) {
		JSONObject request = new JSONObject();
		request.put("req_code", 12);
		request.put("username", username);
		request.put("password", password);
		
		Socket socketClient = null;
		try {
			socketClient = new Socket(serverIP, serverPort);
			PrintStream pr = new PrintStream(socketClient.getOutputStream());
			pr.println(request.toJSONString());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			String s_res = br.readLine();
			
			JSONObject response = (JSONObject) JSONValue.parse(s_res);
			long status = (long) response.get("status");
			
			socketClient.close();
			if (status == 0) {
				long error_code = (long) response.get("error_code");
				String message = (String) response.get("message");
				
				CheckAndAlert.alertErrorMessage((int)error_code, message);
				return false;
			}
			
			return true;
			
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Lỗi kết nối. Hãy thử lại!");
			if (socketClient != null)
				try {
					socketClient.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		}
		
		return false;
	}
}