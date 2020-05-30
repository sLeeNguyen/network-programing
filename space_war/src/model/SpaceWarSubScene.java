package model;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.util.Duration;

public class SpaceWarSubScene extends SubScene {
    private static final String BACKGROUND_IMAGE = "/resources/buttons/yellow_panel.png";
    private boolean isHide = true;

    public SpaceWarSubScene() {
        super(new AnchorPane(), 600, 400);
        prefWidth(600);
        prefHeight(400);
        
        BackgroundImage bi = new BackgroundImage(new Image(BACKGROUND_IMAGE, 600, 400, false, true), BackgroundRepeat.REPEAT, 
        		BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, (BackgroundSize)null);
       
        AnchorPane root = (AnchorPane)getRoot();
        root.setBackground(new Background(bi));
        
        setLayoutX(1280);
        setLayoutY(200);
        setOpacity(0.9);
    }

    public void moveSubScene() {
        TranslateTransition trans = new TranslateTransition();
        trans.setDuration(Duration.seconds(0.3D));
        trans.setNode(this);
        
        if (isHide) {
            trans.setToX(-800);
            isHide = false;
        } else {
            trans.setToX(0);
            isHide = true;
        }

        trans.play();
    }

    public AnchorPane getPane() {
        return (AnchorPane)getRoot();
    }
}