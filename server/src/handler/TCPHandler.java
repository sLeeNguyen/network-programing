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
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import code.ErrorCode;
import code.Message;
import code.RequestCode;
import code.ResponseCode;
import code.StatusCode;
import database.DatabaseHandler;
import helpers.Element;
import helpers.Game;
import helpers.Room;
import helpers.Room.Player;
import server.TCPServer;

public class TCPHandler extends Thread {
	private Socket socket;
	private Connection conn;
	private PrintStream pr;
	private BufferedReader br;
	
	private boolean isRunning;
	
	private Room room;
	
	public TCPHandler(Socket socket) {
		this.isRunning = true;
		this.socket = socket;
		this.room = null;
		this.conn = DatabaseHandler.getInstance().getConnection();
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
//				System.out.println(s_req);
				if (s_req == null) continue;
				JSONObject request = (JSONObject) JSONValue.parse(s_req);
				
				long req_code = (long) request.get("req_code");
				
				// handle request and send response			
				switch((int)req_code) {
					case RequestCode.ENEMY_DIE_REQ:
						enemyDeadHandler(request);
						break;
						
					case RequestCode.CHAT_REQ:
						chatHandler(request);
						break;
						
					case RequestCode.ROOM_CREATION_REQ:
						roomCreationHandler(request);
						break;
						
					case RequestCode.JOIN_ROOM_REQ:
						joinRoomHandler(request);
						break;
						
					case RequestCode.VIEW_ALL_ROOM_REQ:
						getAllRoomHandler(request);
						break;
						
					case RequestCode.PLAYER_DIE_REQ:
						playerDeadHandler(request);
						break;
						
					case RequestCode.PLAY_GAME_REQ:
						playRoomHandler(request);
						break;
						
					case RequestCode.READY_REQ:
						readyHandler(request);
						break;
						
					case RequestCode.LEAVE_ROOM_REQ:
						leaveRoomHandler(request);
						break;
						
					case RequestCode.CLEAR_ROOM_REQ:
						clearRoomHandler(request);
						break;
					
					case RequestCode.SIGNIN_REQ:
						signInHandler(request);
						break;
					
					case RequestCode.SIGNUP_REQ:
						signUpHanlder(request);
						break;
						
					case RequestCode.USER_EXIT:
						userExitHandler(request);
						break;
						
					default: break;
				}
			}
			
		} catch (IOException | SQLException | InterruptedException e) {
			System.out.println("Error: " + e.getMessage());
			send(makeJSONDataString(ResponseCode.ERROR_RES, StatusCode.FAILED, ErrorCode.SERVER_FAILED, Message.SERVER_ERROR));
		}
	}
	
	/**
	 * Home
	 * 
	 * sign in, sign up, room: create, join, ready, leave, clear, start, chat
	 * */
	private void signInHandler(JSONObject request) throws SQLException, IOException {
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		
		if (TCPServer.getConnections(username) != null) {
			send(makeJSONDataString(ResponseCode.SIGNIN_RES, StatusCode.FAILED, ErrorCode.SIGNIN_FAILED, Message.ACCOUNT_BEING_USED));
			return;
		}
		
		int id = checkSignIn(username, password);
		if (id != 0) {
			TCPServer.addTCPClientConnection(username, this);
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
	
	private void userExitHandler(JSONObject request) {
		String username = (String) request.get("username");
		
		if (room != null) {
			synchronized (room) {
				Player p = room.removePlayer(username);
				if (p != null) sendToRoomMember(null, makeJSONDataString(ResponseCode.LEAVE_ROOM_NOTIFY_RES, StatusCode.NONE, "player_id", p.getPlayerId()));
				if (room.isEmpty()) TCPServer.deleteRoom(room.getRoomId());
				else if (p.isOwner()) {
					sendToRoomMember(null, makeJSONDataString(ResponseCode.SET_NEW_OWNER_RES, StatusCode.NONE, "owner_id", room.getRoomOwner().getPlayerId()));
				}
			}
		}
		
		closeHandler();
		TCPServer.deleteConnections(username);
	}
	
	private void getAllRoomHandler(JSONObject request) {
		send(makeJSONDataString(ResponseCode.VIEW_ALL_ROOM_RES, StatusCode.SUCCESS, "list_room", TCPServer.getRoomArrayInfor()));
	}
	
	private void roomCreationHandler(JSONObject request) {
		String roomName = (String) request.get("room_name");
		String roomOwner = (String) request.get("room_owner");
		String roomPass = (String) request.get("room_pass");
		int roomSize = (int) (long) request.get("size");
		String shipName = (String) request.get("ship");
		
		Room room = TCPServer.hasRoom(roomName);
		if (room == null) {
			Integer roomId = genId();
			while (TCPServer.checkRoomId(roomId)) {
				roomId = genId();
			}
			Room newRoom = new Room(roomId, roomName, roomPass, roomSize);
			newRoom.addMember(roomOwner, shipName, TCPServer.getConnections(roomOwner), true);
			TCPServer.addNewRoom(roomId, newRoom);
			send(makeJSONDataString(ResponseCode.ROOM_CREATION_RES, StatusCode.SUCCESS, "room", newRoom.roomJSONObjectSimpleInfo()));
			this.room = newRoom;
		}
		else {
			send(makeJSONDataString(ResponseCode.ROOM_CREATION_RES, StatusCode.FAILED, ErrorCode.ROOM_CREATION_FAILED, Message.ROOM_EXISTS));
		}
	}
	
	private void joinRoomHandler(JSONObject request) {
		Long roomID = (Long) request.get("room_id");
		String roomName = (String) request.get("room_name");
		String memberName = (String) request.get("member_name");
		String roomPass = (String) request.get("room_pass");
		String shipName = (String) request.get("ship");
		
		Room room = null;
		if (roomID != null) room = TCPServer.getRoomById(roomID.intValue());
		else room = TCPServer.hasRoom(roomName);
		
		if (room != null) {
			synchronized (room) {
				if (room.isRunning()) send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_IS_RUNNING));
				
				else if (room.isFull()) send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_FULL));
				
				else if (isExactRoomPassword(room.getRoomPassword(), roomPass)) {
					
					Player newPlayer = room.addMember(memberName, shipName, TCPServer.getConnections(memberName), false);

					send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.SUCCESS, "room", room.toJSONObject()));
					String data = makeJSONDataString(ResponseCode.NEW_MEMBER_RES, StatusCode.NONE, "new_member", newPlayer.toJSONObject());
					this.room = room;
					sendToRoomMember(memberName, data);
				}
				else {
					String response;
					if (roomPass == null) response = makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.REQUIRE_ROOM_PASS);
					else response = makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_PASS_INCORRECT);
					
					send(response);
				}
			}
		} 
		else {
			send(makeJSONDataString(ResponseCode.JOIN_ROOM_RES, StatusCode.FAILED, ErrorCode.JOIN_ROOM_FAILED, Message.ROOM_NOT_EXISTS));
		}
	}
	
	private void chatHandler(JSONObject request) {
		String sender = (String) request.get("sender");
		JSONArray data = (JSONArray) request.get("data");
		synchronized (room) {
			sendToRoomMember(sender, makeJSONDataString(ResponseCode.CHAT_RES, StatusCode.NONE, "data", data));
		}
	}
	
	private void readyHandler(JSONObject request) {
		int playerId = (int) (long) request.get("player_id");
		JSONArray dataArr = (JSONArray) request.get("data");
		
		synchronized (room) {
			int status = (int) (long) dataArr.get(1);
			Player p = room.getMember(playerId);
			if (status == 0) p.ready(false);
			else p.ready(true);
			sendToRoomMember(p.getName(), makeJSONDataString(ResponseCode.READY_RES, StatusCode.NONE, "data", dataArr));
		}
	}
	
	private void leaveRoomHandler(JSONObject request) {
		int playerId = (int) (long) request.get("player_id");
		
		synchronized (room) {
			if (room.isRunning()) {
				send(makeJSONDataString(ResponseCode.LEAVE_ROOM_RES, StatusCode.FAILED, ErrorCode.LEAVE_ROOM_FAILED, Message.ROOM_IS_RUNNING));
				return;
			}
			
			Player p = room.removePlayer(playerId);
			send(makeJSONDataString(ResponseCode.LEAVE_ROOM_RES, StatusCode.SUCCESS, ErrorCode.NONE, null));
			sendToRoomMember(null, makeJSONDataString(ResponseCode.LEAVE_ROOM_NOTIFY_RES, StatusCode.NONE, "player_id", playerId));
			
			if (room.isEmpty()) TCPServer.deleteRoom(room.getRoomId());
			else if (p.isOwner()) {
				sendToRoomMember(null, makeJSONDataString(ResponseCode.SET_NEW_OWNER_RES, StatusCode.NONE, "owner_id", room.getRoomOwner().getPlayerId()));
			}
		}
		this.room = null;
	}
	
	private void clearRoomHandler(JSONObject request) {
		synchronized (room) {
			if (room.isRunning()) {
				send(makeJSONDataString(ResponseCode.CLEAR_ROOM_RES, StatusCode.FAILED, ErrorCode.CLEAR_ROOM_FAILED, Message.ROOM_IS_RUNNING));
				return;
			}
			sendToRoomMember(null, makeJSONDataString(ResponseCode.CLEAR_ROOM_RES, StatusCode.SUCCESS, null, null));
			TCPServer.deleteRoom(room.getRoomId());
			List<Player> players = room.getListMember();
			for (Player p: players) {
				p.getConnections().getTcpHandler().room = null;
			}
		}
	}
	
	private void sendToRoomMember(String sender, String data) {
		if (sender == null) {
			for (Player p: room.getListMember()) {
				p.getConnections().getTcpHandler().send(data);
			}
		} else {
			for (Player p: room.getListMember()) {
				if (!p.getName().equals(sender)) {
					p.getConnections().getTcpHandler().send(data);
				}
			}
		}
	}
	/*********************** End Home ***********************/
	
	
	/**
	 * Room Handler
	 * 
	 * leave, delete, start, chat room
	 * */
	private void playRoomHandler(JSONObject request) throws InterruptedException {		
		synchronized (room) {
			if (!room.checkReady()) {
				send(makeJSONDataString(ResponseCode.PLAY_GAME_RES, StatusCode.FAILED, ErrorCode.PLAY_GAME_FAILED, Message.PARTNER_NOT_READY));
				return;
			}
			establishUdpConnections(room);
			room.setRunning();
			
			String data = makeJSONDataString(ResponseCode.PLAY_GAME_RES, StatusCode.SUCCESS, ErrorCode.NONE, null);
			sendToRoomMember(null, data);
			
			Thread.sleep(500);
			sendToRoomMember(null, "");
			sendToRoomMember(null, makeJSONDataString(ResponseCode.NEW_LEVEL_RES, StatusCode.NONE, "level", room.getGame().getLevel()));
			Thread.sleep(5000);
			sendNextBatch(room.getGame());
		}
	}
	
	private void sendNextBatch(Game game) {
		JSONArray elementsBatch = game.nextBatch();
		String firstData = makeJSONDataString(ResponseCode.ELEMENTS_BATCH_RES, StatusCode.NONE, "data", elementsBatch);
		sendToRoomMember(null, firstData);
	}
	
	private void establishUdpConnections(Room room) {
		List<Player> players = room.getListMember();
		
		String data = makeJSONDataString(ResponseCode.ESTABLISH_UDP_CONNECTION_RES, StatusCode.NONE, null, null);
		
		int cnt = 0;
		while (cnt < players.size()) {
			for (Player p: players) {
				if (p.checkUDPConnection()) {
					p.getConnections().getTcpHandler().send(data);
				} else ++cnt;
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/*********************** End RoomHandler ***********************/
	
	
	/**
	 * Main: Game
	 * 
	 * kill, dead, generate enemy, game level, game over, game pass v.v...
	 * @throws InterruptedException 
	 *  
	 * */
	private void playerDeadHandler(JSONObject request) throws InterruptedException {
		int playerId = (int) (long) request.get("player_id");
		Game game = room.getGame();
		sendToRoomMember(null, makeJSONDataString(ResponseCode.PLAYER_DIE_RES, StatusCode.NONE, "player_id", playerId));
		if (game == null || !room.isRunning()) {
			return;
		}
		game.killPlayer();
		synchronized (game) {
			if (game.checkEndGame() == -1) {
				endGameHandler(game, false);
			}
		}
	}
	
	private void enemyDeadHandler(JSONObject request) throws InterruptedException {
		int enemyId = (int) (long) request.get("enemy_id");
		int killerId = (int) (long) request.get("killer_id");
		
		Game game = room.getGame();
		if (game == null || !room.isRunning()) return;
		Element e = game.getElement(enemyId);
		if (e == null) return;
		
		synchronized (e) {
			if (!e.isDead()) {
				game.killEnemy(e);
				if (killerId != -1) {
					Player you = room.getMember(killerId);
					you.addScore(e.getWorth());
					send(makeJSONDataString(ResponseCode.NEW_SCORE_RES, StatusCode.NONE, "score", you.getScore()));
				}
				
				synchronized (game) {
					Thread.sleep(100);
					if (game.checkEndGame() == 1) {
						endGameHandler(game, true);
					} 
					else if (game.passLevel()) {
						game.nextLevel();
						sendToRoomMember(null, makeJSONDataString(ResponseCode.NEW_LEVEL_RES, StatusCode.NONE, "level", room.getGame().getLevel()));
						Thread.sleep(5000);
						sendNextBatch(game);
					}
					else if (game.checkBatch()) {
						sendNextBatch(game);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void endGameHandler(Game game, boolean isWin) throws InterruptedException {
		List<Player> players = room.getListMember();
		players.sort(new Comparator<Player>() {

			@Override
			public int compare(Player p1, Player p2) {
				return p1.getScore() < p2.getScore() ? 1 : -1;
			}
		});
		
		JSONArray gameRes = new JSONArray();
		for (Player p: players) {
			gameRes.add(p.getGameResult());
		}
		
		if (isWin) sendToRoomMember(null, makeJSONDataString(ResponseCode.WIN_GAME_RES, StatusCode.NONE, "game_result", gameRes));
		else sendToRoomMember(null, makeJSONDataString(ResponseCode.LOST_GAME_RES, StatusCode.NONE, "game_result", gameRes));
		Thread.sleep(500);
		sendToRoomMember(null, "");
		room.reset();
	}
	/*********************** End Game ***********************/
	
	/**
	 * Helper
	 * 
	 * database(insert, update, delete), check v.v...
	 * */
	
	private Socket getSocket() {
		return socket;
	}
	
	private void send(String data) {
		pr.println(data);
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
		if (dataName != null && dataValue != null) response.put(dataName, dataValue);
		
		return response.toJSONString();
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
	
	private Integer genId() {
		Random random = new Random();
		
		return random.nextInt(Integer.MAX_VALUE);
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
