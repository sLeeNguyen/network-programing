package helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Room {
	private String roomName;
	private String roomPassword;
	private int roomSize;
	private int id;
	private int pos;
	private boolean isRunning;
	
	private Game game;
	
	private Player[] team;

	public Room(int id, String roomName, String roomPassword, int roomSize) {
		this.id = id;
		this.roomName = roomName;
		this.roomPassword = (roomPassword == null || roomPassword.isEmpty()) ? null : roomPassword;
		this.roomSize = roomSize;
		team = new Player[roomSize];
		this.pos = 0;
		this.isRunning = false;
	}

	public class Player {
		private String name;
		private String shipName;
		private PairConnection connections;
		private int id;
		private boolean readied;
		private boolean isOwner;
		
		private int score;
		private int numOfEnemiesKilled;
		
		Player(String name, String shipName, PairConnection connections, int id) {
			this.name = name;
			this.shipName = shipName;
			this.connections = connections;
			this.id = id;
			this.readied = false;
			this.isOwner = false;
		}
		
		public String getName() {
			return name;
		}
		
		public String getshipName() {
			return shipName;
		}
		
		public boolean isOwner() {
			return isOwner;
		}
		
		public void setOwner() {
			this.isOwner = true;
			this.readied = true;
		}
		
		public void ready(boolean readied) {
			this.readied = readied;
		}
		
		public boolean getReady() {
			return readied;
		}
		
		public PairConnection getConnections() {
			return connections;
		}
		
		public boolean checkUDPConnection() {
			return connections.getUdpConnection() == null;
		}
		
		public int getPlayerId() {
			return id;
		}
		
		public void addScore(int val) {
			score += val;
			++numOfEnemiesKilled;
		}
		
		public int getScore() {
			return score;
		}
		
		public void reset() {
			connections.setUdpConnection(null);
			score = 0;
			numOfEnemiesKilled = 0;
			if (!isOwner) readied = false;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject toJSONObject() {
			JSONObject playerJSON = new JSONObject();
			playerJSON.put("name", name);
			playerJSON.put("id", id);
			playerJSON.put("ship", shipName);
			playerJSON.put("readied", readied ? 1 : 0);
			
			return playerJSON;
		}
	
		@SuppressWarnings("unchecked")
		public JSONObject getGameResult() {
			JSONObject res = new JSONObject();
			res.put("player_name", name);
			res.put("player_id", id);
			res.put("score", score);
			res.put("enemy_killed", numOfEnemiesKilled);
			
			return res;
		}
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(String roomPassword) {
		this.roomPassword = roomPassword;
	}

	public int getRoomSize() {
		return roomSize;
	}

	public void setRoomSize(int roomSize) {
		this.roomSize = roomSize;
	}
	
	public int getRoomId() {
		return id;
	}
	
	public synchronized Game getGame() {
		return game;
	}
	
	public void setRunning() {
		this.isRunning = true;
		game = new Game(getListMember().size());
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	public boolean checkReady() {
		for (Player p: getListMember()) {
			if (p.getReady()) continue;
			return false;
		}
		
		return true;
	}
	
	public void reset() {
		isRunning = false;
		game = null;
		for (Player p: getListMember()) {
			p.reset();
		}
	}
	
	synchronized public Player addMember(String name, String shipName, PairConnection connections, boolean isOwner) {
		for (int i = 0; i < roomSize; ++i) {
			if ((pos >> i & 1) == 0) {
				Player newPlayer = new Player(name, shipName, connections, i);
				if (isOwner) newPlayer.setOwner();
				team[i] = newPlayer;
				pos |= (1<<i);
				return newPlayer;
			}
		}
		
		return null;
	}
	
	synchronized public Player removePlayer(int id) {
		Player p = team[id];
		team[id] = null;
		pos ^= (1<<id);
		
		if (!isEmpty()) {
			for (Player player: team) if (player != null) {
				player.setOwner();
				break;
			}
		}
		
		return p;
	}
	
	synchronized public Player removePlayer(String name) {
		Player p = null;
		for (Player player: team) if (player != null && player.getName().equals(name)) 
			p = player;
		
		try {
			team[p.getPlayerId()] = null;
			pos ^= (1<<p.getPlayerId());
			
			if (!isEmpty()) {
				for (Player player: team) if (player != null) {
					player.setOwner();
					break;
				}
			}
		} catch (Exception e) {
			//pass
		}
		
		return p;
	}
	
	public Player getMember(int index) {
		if (index < 0 || index >= team.length) return null;
		return team[index];
	}
	
	public List<Player> getListMember() {
		List<Player> list = new ArrayList<>();
		for (Player p: team) {
			if (p != null) list.add(p);
		}
		
		return list;
	}
	
	public Player getRoomOwner() {
		for (Player p: team) {
			if (p != null && p.isOwner) return p;
		}
		
		return null;
	}
	
	public boolean isFull() {
		int numOfPlayers = 0;
		numOfPlayers += (pos & 1) + (pos>>1 & 1) + (pos>>2 & 1);
		
		return numOfPlayers == this.roomSize;
	}
	
	public boolean isEmpty() {
		return pos == 0;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject roomJSON = new JSONObject();
		JSONArray teamArray = new JSONArray();
		for (Player p: team) {
			if (p != null) teamArray.add(p.toJSONObject());
		}
		
		roomJSON.put("room_id", id);
		roomJSON.put("room_name", roomName);
		roomJSON.put("has_pass", roomPassword == null ? 0 : 1);
		roomJSON.put("room_size", roomSize);
		roomJSON.put("owner_id", getRoomOwner().getPlayerId());
		roomJSON.put("team", teamArray);
		
		return roomJSON;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject roomJSONObjectSimpleInfo() {
		JSONObject roomJSON = new JSONObject();
		
		roomJSON.put("room_id", id);
		roomJSON.put("room_name", roomName);
		roomJSON.put("has_pass", roomPassword != null ? true : false);
		roomJSON.put("room_size", roomSize);

		return roomJSON;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSONArray() {
		JSONArray arr = new JSONArray();
		
		arr.add(id);
		arr.add(roomName);
		arr.add(getListMember().size() + "/" + roomSize);
		arr.add(isRunning);
		arr.add(roomPassword != null ? true : false);
		
		return arr;
	}
}