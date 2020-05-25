package helpers.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import helpers.CheckAndAlert;
import helpers.MoveWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class TitleBarController implements Initializable {
	
	private MoveWindow mw;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mw = MoveWindow.getInstance();
	}
	

    @FXML
    void onCancel(MouseEvent event) {
    	Node node = (Node) event.getSource();
    	Stage stage = (Stage) node.getScene().getWindow();
    	if (CheckAndAlert.alertConfirmMessage("Bạn có chắc chắn muốn thoát?")) {
    		stage.close();
    		System.exit(1);
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
