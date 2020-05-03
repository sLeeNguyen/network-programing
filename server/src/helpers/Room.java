package helpers;

import java.util.ArrayList;
import java.util.List;

public class Room {
	private String roomName;
	private String roomPassword;
	private int roomSize;
	
	private List<String> team;
	
	public Room(String roomName, String roomMaster, String roomPassword, int roomSize) {
		this.roomName = roomName;
		this.roomPassword = roomPassword;
		this.roomSize = roomSize;
		team = new ArrayList<>();
		team.add(roomMaster);
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
	
	public boolean addMember(String name) {
		if (team.size() == roomSize) return false;
		team.add(name);
		return true;
	}
	
	public String getMember(int index) {
		if (index < 0 || index >= team.size()) return null;
		return team.get(index);
	}
	
	public List<String> getListMember() {
		return team;
	}
	
	public String getRoomMaster() {
		return team.get(0);
	}
}
