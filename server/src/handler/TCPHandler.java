package handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import code.ErrorCode;
import code.Message;
import code.StatusCode;

public class TCPHandler extends Thread {
	private Socket socket;
	
	public TCPHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String s_req = br.readLine();
			System.out.println(s_req);
			JSONObject request = (JSONObject) JSONValue.parse(s_req);
			
			long req_code = (long) request.get("req_code");
			
			// handle request
			if (req_code == 11) {
				signInHandler(request);
			}
			
		} catch (IOException e) {
			System.out.println("Error when read or write data");
			e.printStackTrace();
		}
	}
	
	private void signInHandler(JSONObject request) throws IOException {
		PrintStream pr = new PrintStream(socket.getOutputStream());
		
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		if ("admin".equals(username) && "admin".equals(password)) {
			pr.println(makeJSONResponse(StatusCode.SUCCESS, ErrorCode.DEFAULT, null));
		} else {
			pr.println(makeJSONResponse(StatusCode.FAILED, ErrorCode.SIGNIN_FAILED, Message.USER_AND_PASS_NOT_EXISTS));
		}
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONResponse(int status_code, int error_code, String message) {
		JSONObject response = new JSONObject();
		
		response.put("status", status_code);
		if (error_code != ErrorCode.DEFAULT) response.put("error_code", error_code);
		if (message != null) response.put("message", message);
		
		return response.toJSONString();
	}
}
