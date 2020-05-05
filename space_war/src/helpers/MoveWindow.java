package helpers;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MoveWindow {
	
	private static MoveWindow instance = null;
	private double x, y;
	
	public void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    public void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();

        Stage stage = (Stage) node.getScene().getWindow();
        stage.setOpacity(0.8);
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
    
    public void stopDrag(MouseEvent event) {
    	Node node = (Node) event.getSource();

        Stage stage = (Stage) node.getScene().getWindow();
        stage.setOpacity(1);
    }
    
    public static MoveWindow getInstance() {
    	if (instance == null) instance = new MoveWindow();
    	
    	return instance;
    }
    
}
