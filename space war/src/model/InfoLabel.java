package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.text.Font;

public class InfoLabel extends Label {
    public static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
    public static final String BACKGROUND_PATH = "/view/resources/yellow_notification.png";

    public InfoLabel(String text) {
        setPrefWidth(380);
        setPrefHeight(49);
        setText(text);
        setWrapText(true);
        setAlignment(Pos.CENTER);
        setLabelFont();
        setBackgroundImage();
    }

    private void setBackgroundImage() {
        BackgroundImage bi = new BackgroundImage(new Image(BACKGROUND_PATH, 380, 49, false, true), BackgroundRepeat.NO_REPEAT, 
        		BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, (BackgroundSize)null);
        setBackground(new Background(bi));
    }

    private void setLabelFont() {
        try {
            setFont(Font.loadFont(new FileInputStream(FONT_PATH), 23));
        } catch (FileNotFoundException var2) {
            setFont(Font.font("Verdana", 23));
        }
    }
}