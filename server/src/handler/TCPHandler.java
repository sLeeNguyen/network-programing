package handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import code.ErrorCode;
import code.Message;
import code.RequestCode;
import code.StatusCode;
import database.DatabaseHandler;

public class TCPHandler extends Thread {
	private Socket socket;
	private Connection conn;
	private Date date;
	
	public TCPHandler(Socket socket) {
		this.socket = socket;
		conn = DatabaseHandler.getInstance().getConnection();
		date = new Date(System.currentTimeMillis());
	}
	
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String s_req = br.readLine();
			System.out.println(s_req);
			JSONObject request = (JSONObject) JSONValue.parse(s_req);
			
			long req_code = (long) request.get("req_code");
			
			// handle request and send response
			if (req_code == RequestCode.SIGNIN_REQ) {
				signInHandler(request);
			} 
			else if (req_code == RequestCode.SIGNUP_REQ) {
				signUpHanlder(request);
			}
			
		} catch (IOException e) {
			System.out.println("Error when read or write data");
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SERVER_FAILED, Message.SERVER_ERROR));
		}
	}
	
	private void signInHandler(JSONObject request) {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		int id = checkSignIn(username, password);
		if (id != 0) {
			sendResponse(makeJSONResponseAttachData(StatusCode.SUCCESS, ""+id, ErrorCode.DEFAULT, null));
		} else {
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SIGNIN_FAILED, Message.USER_AND_PASS_NOT_EXISTS));
		}
	}
	
	private void signUpHanlder(JSONObject request) {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		if (!checkUserExists(username) && saveUserToDatabase(username, password)) {
			sendResponse(makeJSONResponse(StatusCode.SUCCESS, ErrorCode.DEFAULT, null));
		} else {
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SIGNUP_FAILED, Message.USER_EXISTS));
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
	
	@SuppressWarnings("unchecked")
	private String makeJSONResponseAttachData(int status_code, String data, int error_code, String message) {
		JSONObject response = new JSONObject();
		
		response.put("status", status_code);
		if (data != null) response.put("data", data);
		if (error_code != ErrorCode.DEFAULT) response.put("error_code", error_code);
		if (message != null) response.put("message", message);
		
		return response.toJSONString();
	}
	
	private void sendResponse(String response) {
		PrintStream pr;
		try {
			pr = new PrintStream(socket.getOutputStream());
			pr.println(response);
			pr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkUserExists(String username) {
		String sql = "SELECT * FROM Account WHERE username=?";
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);

			ResultSet rs = ps.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private int checkSignIn(String username, String password) {
		String sql = "SELECT * FROM Account WHERE username=? AND password=?";
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private boolean saveUserToDatabase(String username, String password) {
		String sql = "INSERT dbo.Account(username, password, date_create) VALUES(?, ?, ?)" ;
		
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setDate(3, date);

			return ps.executeUpdate() != 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
