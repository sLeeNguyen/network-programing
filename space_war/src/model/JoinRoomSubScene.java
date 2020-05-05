package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class JoinRoomSubScene extends SubScene {
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";	
	private static final String TEXT_FIELD_STYLE = "-fx-background-image: url('/model/resources/yellow_button13.png'); -fx-background-size: 300 49;"
			+ "-fx-font-weight: bold; -fx-font-family: Verdana; -fx-font-size: 20px";
	
	private VBox nameHB;
	private VBox passHB;
	private Font font;
	
	private TextField nameTF;
	private TextField passTF;
	
	public JoinRoomSubScene(double layoutX, double layoutY) {
		super(new AnchorPane(), 500, 250);
		setLayoutX(layoutX);
		setLayoutY(layoutY);
		getPane().setStyle("-fx-background-color: transparent;");
		
		try {
			font = Font.loadFont(new FileInputStream(FONT_PATH), 20);
		} catch (FileNotFoundException e) {
			font = Font.font("Verdana", 20);
            System.out.println("Lỗi load font");
		}
		setElement();
	}
	
	public void setElement() {
		setRoomName();
		setRoomPassword();
	}
	
	private void setRoomName() {
		Label nameLabel = new Label("ROOM NAME");
		nameLabel.setFont(font);
		
		nameTF = new TextField();
		nameTF.setStyle(TEXT_FIELD_STYLE);
		nameTF.setMinSize(300, 49);

		nameHB = new VBox(10);
		nameHB.getChildren().addAll(nameLabel, nameTF);
		nameHB.setAlignment(Pos.CENTER_LEFT);
		nameHB.setLayoutX(100);
		nameHB.setLayoutY(0);
		
		getPane().getChildren().add(nameHB);
	}
	
	private void setRoomPassword() {
		Label passLabel = new Label("PASSWORD");
		passLabel.setFont(font);
		
		passTF = new TextField();
		passTF.setStyle(TEXT_FIELD_STYLE);
		passTF.setMinSize(300, 49);

		passHB = new VBox(10);
		passHB.getChildren().addAll(passLabel, passTF);
		passHB.setAlignment(Pos.CENTER_LEFT);
		passHB.setLayoutX(100);
		passHB.setLayoutY(100);
		
		getPane().getChildren().add(passHB);
	}
	
	public AnchorPane getPane() {
		return (AnchorPane) getRoot();
	}
	
	public String getRoomName() {
		return nameTF.getText();
	}
	
	public String getPassword() {
		return passTF.getText();
	}
}
