package model;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ShipPicker extends VBox {
    private final String CIRLE_NOT_CHOOSEN = "/view/resources/shipchooser/grey_circle.png";
    private final String CIRCLE_CHOOSEN = "/view/resources/shipchooser/yellow_boxTick.png";
    
    private ImageView circleImage = new ImageView(CIRLE_NOT_CHOOSEN);
    private SHIP shipE;
    
    private ImageView shipView;

    public ShipPicker(SHIP shipE) {
    	shipView = new ImageView(shipE.getUrlShip());
    	this.shipE = shipE;
    	
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-cursor: hand;");
        getChildren().add(circleImage);
        getChildren().add(shipView);
    }

    public SHIP getSHIP() {
        return shipE;
    }

    public void setCircleChoosen(boolean isChoosen) {
        if (isChoosen) {
            circleImage.setImage(new Image(CIRCLE_CHOOSEN));
        } else {
            circleImage.setImage(new Image(CIRLE_NOT_CHOOSEN));
        }
    }
}