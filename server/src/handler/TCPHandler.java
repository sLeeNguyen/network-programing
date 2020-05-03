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
import helpers.Room;
import server.TCPServer;

public class TCPHandler extends Thread {
	private Socket socket;
	private Connection conn;
	private PrintStream pr;
	private BufferedReader br;

	public TCPHandler(Socket socket) {
		this.socket = socket;
		conn = DatabaseHandler.getInstance().getConnection();
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pr = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				String s_req = br.readLine();
				System.out.println(s_req);
				JSONObject request = (JSONObject) JSONValue.parse(s_req);
				
				long req_code = (long) request.get("req_code");
				
				// handle request and send response			
				switch((int)req_code) {
					case RequestCode.SIGNIN_REQ:
						signInHandler(request);
						break;
					
					case RequestCode.SIGNUP_REQ:
						signUpHanlder(request);
						break;
						
					case RequestCode.ROOM_CREATION_REQ:
						roomCreationHandler(request);
						break;
						
					default: break;
				}
			}
			
		} catch (IOException | SQLException e) {
			System.out.println("Error: " + e.getMessage());
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SERVER_FAILED, Message.SERVER_ERROR));
		}
	}
	
	private void signInHandler(JSONObject request) throws SQLException, IOException {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		int id = checkSignIn(username, password);
		if (id != 0) {
			TCPServer.addClientConnection(id, socket);
			sendResponse(makeJSONResponseAttachData(StatusCode.SUCCESS, ""+id, ErrorCode.NONE, null));
		} else {
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SIGNIN_FAILED, Message.USER_AND_PASS_NOT_EXISTS));
		}
	}
	
	private void signUpHanlder(JSONObject request) throws SQLException, IOException {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		if (!checkUserExists(username) && saveUserToDatabase(username, password)) {
			sendResponse(makeJSONResponse(StatusCode.SUCCESS, ErrorCode.NONE, null));
		} else {
			sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.SIGNUP_FAILED, Message.USER_EXISTS));
		}
	}
	
	private void roomCreationHandler(JSONObject request) {
		String roomName = (String) request.get("room_name");
		String roomMaster = (String) request.get("room_master");
		String roomPass = (String) request.get("room_pass");
		long roomSize = (long) request.get("size");
		
		boolean exists = TCPServer.hasRoom(roomName);
		if (exists) sendResponse(makeJSONResponse(StatusCode.FAILED, ErrorCode.ROOM_CREATION_FAILED, Message.ROOM_EXISTS));
		else {
			Room newRoom = new Room(roomName, roomMaster, roomPass, (int)roomSize);
			TCPServer.addNewRoom(newRoom);
			sendResponse(makeJSONResponse(StatusCode.SUCCESS, ErrorCode.NONE, null));
		}
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONResponse(int status_code, int error_code, String message) {
		JSONObject response = new JSONObject();
		
		response.put("status", status_code);
		if (error_code != ErrorCode.NONE) response.put("error_code", error_code);
		if (message != null) response.put("message", message);
		
		return response.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONResponseAttachData(int status_code, String data, int error_code, String message) {
		JSONObject response = new JSONObject();
		
		response.put("status", status_code);
		if (data != null) response.put("data", data);
		if (error_code != ErrorCode.NONE) response.put("error_code", error_code);
		if (message != null) response.put("message", message);
		
		return response.toJSONString();
	}
	
	private void sendResponse(String response) {
		pr.println(response);
	}
	
	private boolean checkUserExists(String username) throws SQLException {
		String sql = "SELECT * FROM Account WHERE username=?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		
		return rs.next();		
	}
	
	private int checkSignIn(String username, String password) throws SQLException {
		String sql = "SELECT * FROM Account WHERE username=? AND password=?";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, password);
		
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}

		return 0;
	}
	
	private boolean saveUserToDatabase(String username, String password) throws SQLException {
		String sql = "INSERT dbo.Account(username, password, date_create) VALUES(?, ?, ?)" ;
		Date date = new Date(System.currentTimeMillis());
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, password);
		ps.setDate(3, date);

		return ps.executeUpdate() != 0;
	}
}
