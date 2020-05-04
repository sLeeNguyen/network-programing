package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class RoomInformationSubScene extends SubScene {
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";	
	private static final String TEXT_FIELD_STYLE = "-fx-background-image: url('/model/resources/yellow_button13.png'); -fx-background-size: 300 49;"
			+ "-fx-font-weight: bold; -fx-font-family: Verdana; -fx-font-size: 20px";
	private static final String CHOICEBOX_STYLE = "-fx-background-image: url('/model/resources/yellow_button13.png'); -fx-background-size: 80 49;"
			+ "-fx-font: 20px \"Verdana\";";
	
	private HBox nameHB;
	private HBox sizeHB;
	private HBox passHB;
	private Font font;
	
	private TextField nameTF;
	private TextField passTF;
	ChoiceBox<Integer> sizeOption;
	
	
	public RoomInformationSubScene(double layoutX, double layoutY) {
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
	
	private void setElement() {
		setRoomName();
		setRoomSize();
		setRoomPassword();
	}
	
	private void setRoomName() {
		Label nameLabel = new Label("ROOM NAME");
		nameLabel.setFont(font);
		
		nameTF = new TextField();
		nameTF.setStyle(TEXT_FIELD_STYLE);
		nameTF.setMinSize(300, 49);

		nameHB = new HBox(20);
		nameHB.getChildren().addAll(nameLabel, nameTF);
		nameHB.setAlignment(Pos.CENTER_LEFT);
		nameHB.setLayoutX(15);
		nameHB.setLayoutY(0);
		
		getPane().getChildren().add(nameHB);
	}
	
	private void setRoomSize() {
		Label sizeLabel = new Label("ROOM SIZE");
		sizeLabel.setFont(font);
		
		sizeOption = new ChoiceBox<Integer>();
		sizeOption.setStyle(CHOICEBOX_STYLE);
		sizeOption.setMinSize(80, 49);
		sizeOption.getItems().addAll(1, 2, 3);
		sizeOption.setValue(1);
		
		sizeHB = new HBox(35);
		sizeHB.getChildren().addAll(sizeLabel, sizeOption);
		sizeHB.setAlignment(Pos.CENTER_LEFT);
		sizeHB.setLayoutX(15);
		sizeHB.setLayoutY(80);
		
		getPane().getChildren().add(sizeHB);
	}
	
	private void setRoomPassword() {
		Label passLabel = new Label("PASSWORD");
		passLabel.setFont(font);
		
		passTF = new TextField();
		passTF.setStyle(TEXT_FIELD_STYLE);
		passTF.setMinSize(300, 49);

		passHB = new HBox(28);
		passHB.getChildren().addAll(passLabel, passTF);
		passHB.setAlignment(Pos.CENTER_LEFT);
		passHB.setLayoutX(15);
		passHB.setLayoutY(80*2);
		
		getPane().getChildren().add(passHB);
	}
	
	public AnchorPane getPane() {
		return (AnchorPane) getRoot();
	}

	public String getRoomName() {
		return nameTF.getText();
	}
	
	public int getRoomSize() {
		return sizeOption.getValue();
	}
	
	public String getPassword() {
		return passTF.getText();
	}
}
