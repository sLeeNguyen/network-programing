package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class SpaceWarButton extends Button {
    private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
    private static final String BUTTON_STYLE_1 = "-fx-background-color: transparent; -fx-background-image: url('/model/resources/yellow_button.png'); -fx-cursor: hand;";
    private static final String BUTTON_STYLE_2 = "-fx-background-color: transparent; -fx-background-image: url('/model/resources/yellow_button13.png'); -fx-background-size: 250 49; -fx-cursor: hand;";
    		
    public SpaceWarButton(String text) {
        setText(text);
        setButtonFont();
        setPrefWidth(190);
        setPrefHeight(49);
        setStyle(BUTTON_STYLE_1);
        initializeButtonListeners();
    }
    
    public void changeButton() {
    	setPrefSize(250, 49);
    	setStyle(BUTTON_STYLE_2);
    }
    
    private void setButtonFont() {
        try {
            setFont(Font.loadFont(new FileInputStream(FONT_PATH), 23));
        } catch (FileNotFoundException var2) {
            setFont(Font.font("Verdana", 23));
            System.out.println("Lỗi load font");
        }
    }

    private void setButtonPressedStyle() {
        setPrefHeight(45);
        setLayoutY(getLayoutY() + 4);
    }

    private void setButtonReleasedStyle() {
        setPrefHeight(49);
        setLayoutY(getLayoutY() - 4);
    }

    private void initializeButtonListeners() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            
        	public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonPressedStyle();
                }
            }
        });
        
        setOnMouseReleased(new EventHandler<MouseEvent>() {
           
        	public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonReleasedStyle();
                }
            }
        });
        
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            
        	public void handle(MouseEvent event) {
                setEffect(new DropShadow());
            }
        });
        
        setOnMouseExited(new EventHandler<MouseEvent>() {
            
        	public void handle(MouseEvent event) {
                setEffect((Effect)null);
            }
        });
    }
}