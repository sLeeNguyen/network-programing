package helpers.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helpers.connect.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import view.ViewManager;

public class RoomBarController implements Initializable {
	
	@FXML
    private Label roomID;

    @FXML
    private Label roomName;

    @FXML
    private Label capacity;

    @FXML
    private Label status;
    
    @FXML
    private FontAwesomeIconView passIcon;
    
    private boolean requirePassword;

    @FXML
    void joinRoom(MouseEvent event) {
    	String pass = null;
    	if (requirePassword) {
    		pass = getPassword();
    	} 
    	Client.sendJoinRoomRequest(Integer.parseInt(roomID.getText()), pass, ViewManager.user.getName(), ViewManager.yourShip.name());
    }
    
    public void setInformation(String roomID, String roomName, String capacity, boolean isRunning, boolean requirePassword) {
    	this.requirePassword = requirePassword;
    	this.roomID.setText(roomID);
    	this.roomName.setText(roomName);
    	this.capacity.setText(capacity);
    	this.status.setText(isRunning ? "Running" : "Free");
    	this.passIcon.setVisible(requirePassword);
    }
    
    private String getPassword() {
    	Dialog<String> dialog = new Dialog<String>();
    	dialog.setTitle("Space War");
    	dialog.setHeaderText("Please enter room\'s password!");
    	
    	ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
    	
    	JFXTextField passwordJFX = new JFXTextField();
    	dialog.getDialogPane().setContent(passwordJFX);
    	
    	Platform.runLater(() -> passwordJFX.requestFocus());
    	
    	dialog.setResultConverter(dialogButton -> {
    		if (dialogButton == okButtonType) {
    			return passwordJFX.getText();
    		}
    		return null;
    	});
    	
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()) return result.get();
    	
    	return null;
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
}
