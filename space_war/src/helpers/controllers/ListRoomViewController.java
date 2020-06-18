package helpers.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ListRoomViewController implements Initializable {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox listRoomBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
    
	public void addRoomRow(String roomID, String roomName, String capacity, boolean isRunning, boolean requirePassword) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/helpers/fxml/room_bar.fxml"));
		try {
			Node node = loader.load();
			listRoomBox.getChildren().add(node);
		} catch (IOException e) {
			//pass
		}
		
		RoomBarController rc = loader.getController();
		rc.setInformation(roomID, roomName, capacity, isRunning, requirePassword);
	}
    
}
