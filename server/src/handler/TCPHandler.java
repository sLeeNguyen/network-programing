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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import code.ErrorCode;
import code.Message;
import code.RequestCode;
import code.ResponseCode;
import code.StatusCode;
import code.UpdateCode;
import database.DatabaseHandler;
import helpers.Room;
import server.TCPServer;

public class TCPHandler extends Thread {
	private Socket socket;
	private Connection conn;
	private PrintStream pr;
	private BufferedReader br;
	
	private boolean isRunning;
	
	public TCPHandler(Socket socket) {
		this.isRunning = true;
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
			while (isRunning) {
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
						
					case RequestCode.JOIN_ROOM_REQ:
						joinRoomHandler(request);
						break;
					
					default: break;
				}
			}
			
		} catch (IOException | SQLException e) {
			System.out.println("Error: " + e.getMessage());
			send(makeJSONDataString(ResponseCode.ERROR_RES, StatusCode.FAILED, ErrorCode.SERVER_FAILED, Message.SERVER_ERROR));
		}
	}
	
	private void signInHandler(JSONObject request) throws SQLException, IOException {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		int id = checkSignIn(username, password);
		if (id != 0) {
			TCPServer.addClientConnection(username, this);
			send(makeJSONDataString(ResponseCode.SIGNIN_RES, StatusCode.SUCCESS, "user_id",  id));
		} else {
			send(makeJSONDataString(ResponseCode.SIGNIN_RES, StatusCode.FAILED, ErrorCode.SIGNIN_FAILED, Message.ACCOUNT_NOT_EXISTS));
			closeHandler();
		}
	}
	
	private void signUpHanlder(JSONObject request) throws SQLException, IOException {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		if (!checkUserExists(username) && saveUserToDatabase(username, password)) {
			send(makeJSONDataString(ResponseCode.SIGNUP_RES, StatusCode.SUCCESS, ErrorCode.NONE, null));
		} else {
			send(makeJSONDataString(ResponseCode.SIGNUP_RES, StatusCode.FAILED, ErrorCode.SIGNUP_FAILED, Message.ACCOUNT_EXISTS));
			closeHandler();
		}
	}
	
	private void roomCreationHandler(JSONObject request) {
		String roomName = (String) request.get("room_name");
		String roomMaster = (String) request.get("room_master");
		String roomPass = (String) request.get("room_pass");
		long roomSize = (long) request.get("size");
		
		int pos = TCPServer.hasRoom(roomName);
		if (pos == -1) {
			Room newRoom = new Room(roomName, roomMaster, roomPass, (int)roomSize);
			TCPServer.addNewRoom(newRoom);
			send(makeJSONDataString(ResponseCode.ROOM_CREATION_RES, StatusCode.SUCCESS, ErrorCode.NONE, null));
		}
		else {
			send(makeJSONDataString(ResponseCode.ROOM_CREATION_RES, StatusCode.FAILED, ErrorCode.ROOM_CREATION_FAILED, Message.ROOM_EXISTS));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void joinRoomHandler(JSONObject request) {
		String roomName = (String) request.get("room_name");
		String memberName = (String) request.get("member_name");
		String roomPass = (String) request.get("room_pass");
		
		int pos = TCPServer.hasRoom(roomName);
		if (pos != -1) {
			Room room = TCPServer.getRoom(pos);
			if (room.isFull()) {
				send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_FULL));
			}
			else if (isExactRoomPassword(room.getRoomPassword(), roomPass)) {
				room.addMember(memberName);
				JSONArray teamArray = new JSONArray();
				teamArray.addAll(room.getListMember());
				send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.SUCCESS, "team", teamArray));
				sendUpdateToRoomMember(room, memberName);
			} 
			else {
				String response;
				if (roomPass == null) response = makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.REQUIRE_ROOM_PASS);
				else response = makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_PASS_INCORRECT);
				
				send(response);
			}
		} 
		else {
			send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_NOT_EXISTS));
		}
	}
	
	private void sendUpdateToRoomMember(Room room, String newMemberName) {
		String update = makeJSONDataString(UpdateCode.NEW_MEMBER, StatusCode.NONE, "new_member", newMemberName);
		for (String memberName: room.getListMember()) {
			if (!memberName.equals(newMemberName)) {
				TCPServer.getConnection(memberName).send(update);
			}
		}
	}

	private boolean isExactRoomPassword(String realRoomPass, String roomPass) {
		if (realRoomPass == null || realRoomPass.isEmpty()) return true;
		return realRoomPass.equals(roomPass);
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONDataString(int tcpCode, int status_code, int error_code, String message) {
		JSONObject response = new JSONObject();
		
		response.put("tcp_code", tcpCode);
		response.put("status", status_code);
		if (error_code != ErrorCode.NONE) response.put("error_code", error_code);
		if (message != null) response.put("message", message);
		
		return response.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private String makeJSONDataString(int tcpCode, int status_code, String dataName, Object dataValue) {
		JSONObject response = new JSONObject();
		
		response.put("tcp_code", tcpCode);
		if (status_code != StatusCode.NONE) response.put("status", status_code);
		response.put(dataName, dataValue);
		
		return response.toJSONString();
	}
	
	private void send(String data) {
		pr.println(data);
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
	
	private void closeHandler() {
		try {
			socket.close();
			pr.close();
			br.close();
			conn = null;
			isRunning = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
