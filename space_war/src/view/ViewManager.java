package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import helpers.CheckAndAlert;
import helpers.controllers.TitleBarController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.InfoLabel;
import model.SHIP;
import model.ShipPicker;
import model.SpaceWarButton;
import model.SpaceWarSubScene;
import model.element.Ship;

public class ViewManager {
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1100;
    private static final int MENU_BUTTONS_START_X = 100;
    private static final int MENU_BUTTONS_START_Y = 160;
    
    private AnchorPane root;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    private SpaceWarSubScene startSubScene;
    private SpaceWarSubScene scoreSubScene;
    private SpaceWarSubScene helpSubScene;
    private SpaceWarSubScene creditsSubScene;
    private SpaceWarSubScene currentSubScene;
    List<SpaceWarButton> menuButtons;
    List<ShipPicker> shipPickers;
    private Ship choosenShip;

    public ViewManager() {  
    	root = new AnchorPane();
    	mainPane = new AnchorPane();
    	mainPane.setPrefHeight(HEIGHT);
    	mainPane.setPrefWidth(WIDTH);
    	
    	setUpAll();
        
        mainScene = new Scene(root, WIDTH, HEIGHT + 40);
        mainStage = new Stage();
        mainStage.setScene(mainScene);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void createButton() {
    	menuButtons = new ArrayList<>();
    	
        createStartButton();
        createScoreButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    private void createSubScene() {
        createShipChooserSubScene();
        
        scoreSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(scoreSubScene);
        
        helpSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(helpSubScene);
        
        creditsSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(creditsSubScene);
    }

    private void setUpAll() {
    	createButton();
        createBackground();
        createLogo();
        createSubScene();
        
    	root.getChildren().add(mainPane);
    	
    	AnchorPane.setTopAnchor(mainPane, 40D);
    	AnchorPane.setBottomAnchor(mainPane, 0D);
    	AnchorPane.setLeftAnchor(mainPane, 0D);
    	AnchorPane.setRightAnchor(mainPane, 0D);
    	
		try {
			Node node = FXMLLoader.load(getClass().getResource("/helpers/fxml/title_bar.fxml"));
		
			root.getChildren().add(node);
			AnchorPane.setTopAnchor(node, 0D);
	    	AnchorPane.setLeftAnchor(node, 0D);
	    	AnchorPane.setRightAnchor(node, 0D);
	    	
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Xảy ra lỗi. Xin hãy thử lại!");
			e.printStackTrace();
		}
    	
    }
    
    private void addMenuButton(SpaceWarButton button) {
        button.setLayoutX(MENU_BUTTONS_START_X);
        button.setLayoutY(MENU_BUTTONS_START_Y + menuButtons.size() * MENU_BUTTONS_START_X);
        menuButtons.add(button);
        mainPane.getChildren().add(button);
    }

    private void showSubScene(SpaceWarSubScene subScene) {
        if (currentSubScene != null) {
            currentSubScene.moveSubScene();
        }

        if (!subScene.equals(currentSubScene)) {
            subScene.moveSubScene();
            currentSubScene = subScene;
        } else {
            currentSubScene = null;
        }

    }

    private void createStartButton() {
        SpaceWarButton startButton = new SpaceWarButton("START");
        addMenuButton(startButton);
        
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            
        	public void handle(ActionEvent event) {
                showSubScene(startSubScene);
            }
        });
    }

    private void createScoreButton() {
        SpaceWarButton scoreButton = new SpaceWarButton("SCORE");
        addMenuButton(scoreButton);
        
        scoreButton.setOnAction(new EventHandler<ActionEvent>() {
        
        	public void handle(ActionEvent event) {
                showSubScene(scoreSubScene);
            }
        });
    }

    private void createHelpButton() {
        SpaceWarButton helpButton = new SpaceWarButton("HELP");
        addMenuButton(helpButton);
       
        helpButton.setOnAction(new EventHandler<ActionEvent>() {
        
        	public void handle(ActionEvent event) {
                showSubScene(helpSubScene);
            }
        });
    }

    private void createCreditsButton() {
        SpaceWarButton creditsButton = new SpaceWarButton("CREDIT");
        addMenuButton(creditsButton);
        
        creditsButton.setOnAction(new EventHandler<ActionEvent>() {
        
        	public void handle(ActionEvent event) {
                showSubScene(creditsSubScene);
            }
        });
    }

    private void createExitButton() {
        SpaceWarButton exitButton = new SpaceWarButton("EXIT");
        addMenuButton(exitButton);
        
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
        
        	public void handle(ActionEvent event) {
                mainStage.close();
            }
        });
    }

    private void createShipChooserSubScene() {
        startSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(startSubScene);
        
        InfoLabel chooseLabel = new InfoLabel("CHOOSE YOUR SHIP");
        chooseLabel.setLayoutX(110);
        chooseLabel.setLayoutY(30);
        startSubScene.getPane().getChildren().add(chooseLabel);
        
        HBox choosenList = createListShip();
        startSubScene.getPane().getChildren().add(choosenList);
       
        createButtonToStart();
    }

    private void createButtonToStart() {
        SpaceWarButton playButton = new SpaceWarButton("PLAY");
        playButton.setLayoutX(360);
        playButton.setLayoutY(320);
        
        startSubScene.getPane().getChildren().add(playButton);
        
        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        
        	public void handle(MouseEvent event) {
                if (choosenShip == null) {
                    System.out.println("Please choose your ship!");
                } else {
                    GamePlay gameManager = new GamePlay();
                    gameManager.createNewGame(mainStage, choosenShip);
                }
            }
        });
    }

    private HBox createListShip() {
        HBox box = new HBox();
        box.setSpacing(20);
        
        shipPickers = new ArrayList<>();
        SHIP[] var5;
        int var4 = (var5 = SHIP.values()).length;

        for(int var3 = 0; var3 < var4; ++var3) {
            SHIP ship = var5[var3];
            final ShipPicker sp = new ShipPicker(ship);
            shipPickers.add(sp);
            box.getChildren().add(sp);
            
            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
               
            	public void handle(MouseEvent event) {
            		
            		for (ShipPicker sp: shipPickers) {
            			sp.setCircleChoosen(false);
            		}

                    sp.setCircleChoosen(true);
                    choosenShip = new Ship(sp.getSHIP().getUrlShip(), "player", 12, 100, 75);
                }
            });
        }

        box.setLayoutX(72);
        box.setLayoutY(150);
        
        return box;
    }

    private void createBackground() {
        Image backgroundImage = new Image("/view/resources/blue.png", 256, 256, false, true);
        BackgroundImage bi = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, 
        		BackgroundPosition.DEFAULT, (BackgroundSize)null);
        
        mainPane.setBackground(new Background(bi));
    }

    private void createLogo() {
        ImageView logo = new ImageView("/view/resources/logo4.gif");
        logo.setLayoutX(450);
        logo.setLayoutY(50);
        logo.setStyle("-fx-cursor: hand;");
        logo.setEffect(new DropShadow());
        
        mainPane.getChildren().add(logo);
    }
}