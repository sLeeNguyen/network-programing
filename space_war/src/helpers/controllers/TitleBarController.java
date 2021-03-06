package helpers.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import helpers.CheckAndAlert;
import helpers.MoveWindow;
import helpers.connect.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.ViewManager;


public class TitleBarController implements Initializable {
	
	private MoveWindow mw;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mw = MoveWindow.getInstance();
	}
	

    @FXML
    void onCancel(MouseEvent event) {
    	if (CheckAndAlert.alertConfirmMessage("Do you want to exit?")) {
    		if (ViewManager.user != null) {
    			Client.sendUserExitRequest(ViewManager.user.getName());
    		}
    		Platform.exit();
    		System.exit(0);
    	}
     }

    @FXML
    void onMinimize(MouseEvent event) {
    	Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    void onPressed(MouseEvent event) {
    	mw.pressed(event);
    }
    
    @FXML
    void onDragged(MouseEvent event) {
    	mw.dragged(event);
    }
    
    @FXML
    void onStopDrag(MouseEvent event) {
    	mw.stopDrag(event);
    }
}
