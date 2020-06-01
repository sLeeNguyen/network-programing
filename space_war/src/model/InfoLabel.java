package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import helpers.Resource;
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
    public static final String BACKGROUND_PATH = "/resources/buttons/yellow_notification.png";

    public InfoLabel(String text) {
        setPrefWidth(380);
        setPrefHeight(49);
        setText(text);
        setWrapText(true);
        setAlignment(Pos.CENTER);
        setLabelFont(23);
        setBackgroundImage(380, 49);
    }
    
    public InfoLabel(String text, double width, double height) {
        setPrefSize(width, height);
        setText(text);
        setWrapText(true);
        setAlignment(Pos.CENTER);
        setLabelFont(23);
        setBackgroundImage(width, height);
    }
    
    private void setBackgroundImage(double width, double height) {
        BackgroundImage bi = new BackgroundImage(new Image(BACKGROUND_PATH, width, height, false, true), BackgroundRepeat.NO_REPEAT, 
        		BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, (BackgroundSize)null);
        setBackground(new Background(bi));
    }

    public void setLabelFont(int fontSize) {
        try {
            setFont(Font.loadFont(new FileInputStream(Resource.FONT_PATH_2), fontSize));
        } catch (FileNotFoundException var2) {
            setFont(Font.font("Verdana", 23));
        }
    }
}