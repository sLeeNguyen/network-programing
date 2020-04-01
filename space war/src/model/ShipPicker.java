package model;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.element.Ship;

public class ShipPicker extends VBox {
    private final String CIRLE_NOT_CHOOSEN = "/view/resources/shipchooser/grey_circle.png";
    private final String CIRCLE_CHOOSEN = "/view/resources/shipchooser/yellow_boxTick.png";
    
    private ImageView circleImage = new ImageView(CIRLE_NOT_CHOOSEN);
    private Ship ship;

    public ShipPicker(SHIP shipE) {
        ship = new Ship(shipE.getUrlShip());
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-cursor: hand;");
        getChildren().add(circleImage);
        getChildren().add(ship);
    }

    public Ship getShip() {
        return ship;
    }

    public void setCircleChoosen(boolean isChoosen) {
        if (isChoosen) {
            circleImage.setImage(new Image(CIRCLE_CHOOSEN));
        } else {
            circleImage.setImage(new Image(CIRLE_NOT_CHOOSEN));
        }

    }
}