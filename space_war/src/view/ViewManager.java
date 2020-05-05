package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import helpers.CheckAndAlert;
import helpers.UserInformation;
import helpers.connect.Client;
import helpers.connect.Client.TCPViewManager;
import javafx.application.Platform;
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
import model.JoinRoomSubScene;
import model.RoomCreationSubScene;
import model.SHIP;
import model.ShipPicker;
import model.SpaceWarButton;
import model.SpaceWarSubScene;
import model.element.Ship;

import helpers.code.ResponseCode;
import helpers.code.UpdateCode;

public class ViewManager {
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1100;
    private static final int MENU_BUTTONS_START_X = 100;
    private static final int MENU_BUTTONS_START_Y = 180;
    
    public static UserInformation user;
    
    private AnchorPane root;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    private SpaceWarSubScene startSubScene;
    private SpaceWarSubScene roomSubScene;
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
        createRoomButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    private void createSubScene() {
        createShipChooserSubScene();
        createRoomSubScene();
        
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
    	
		showSubScene(startSubScene);
		createTCPHandler();
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

    private void createRoomButton() {
        SpaceWarButton roomButton = new SpaceWarButton("ROOM");
        addMenuButton(roomButton);
        
        roomButton.setOnAction(new EventHandler<ActionEvent>() {
        
        	public void handle(ActionEvent event) {
                showSubScene(roomSubScene);
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
    
    
    /*
     * Set startSubScene's elements
    */
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
                    CheckAndAlert.alertSuccessMessage("Please choose your ship");
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
    /*======================== End startSubScene ========================*/
    
    
    /*
     * Set roomSubScene's elements
    */
    private void createRoomSubScene() {
    	roomSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(roomSubScene);
        createButtonOfRoomSubScene();
    }
    
    private void createButtonOfRoomSubScene() {
    	SpaceWarButton createRoomButton = new SpaceWarButton("NEW ROOM"); 
    	SpaceWarButton joinRoomButton = new SpaceWarButton("JOIN ROOM"); 
    	createRoomButton.changeButton(); createRoomButton.setLayoutX(175); createRoomButton.setLayoutY(120);
    	joinRoomButton.changeButton();	joinRoomButton.setLayoutX(175); joinRoomButton.setLayoutY(200);
    	
    	roomSubScene.getPane().getChildren().addAll(createRoomButton, joinRoomButton);
    	
    	createRoomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				RoomCreationSubScene roomInfor = new RoomCreationSubScene(50, 50);
				roomSubScene.getPane().getChildren().clear();
				roomSubScene.getPane().getChildren().add(roomInfor);		
				setButtonOfRoomInfor(roomInfor);
			}
		});
    	
    	joinRoomButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				JoinRoomSubScene joinRoomInfor = new JoinRoomSubScene(50, 50);
				roomSubScene.getPane().getChildren().clear();
				roomSubScene.getPane().getChildren().add(joinRoomInfor);
				setButtonOfJoinRoom(joinRoomInfor);
			}
		});
    }
    
    private void setButtonOfRoomInfor(RoomCreationSubScene roomInfor) {
		SpaceWarButton cancelButton = new SpaceWarButton("CANCEL");
		SpaceWarButton createButton = new SpaceWarButton("CREATE");
		
		cancelButton.setLayoutX(60); cancelButton.setLayoutY(300);
		createButton.setLayoutX(340); createButton.setLayoutY(300);
		
		roomSubScene.getPane().getChildren().addAll(cancelButton, createButton);
		
		cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				roomSubScene.getPane().getChildren().clear();
				createButtonOfRoomSubScene();
			}
		});
		
		createButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String roomName = roomInfor.getRoomName();
				String roomPass = roomInfor.getPassword();
				int size = roomInfor.getRoomSize();
				
				if (roomName == null || roomName.isEmpty()) CheckAndAlert.alertErrorMessage("Vui lòng nhập tên phòng");

				Client.sendRoomCreationRequest(roomName, user.getName(), size, roomPass);
			}
		});
	}
    
    private void setButtonOfJoinRoom(JoinRoomSubScene joinRoomInfor) {
    	SpaceWarButton cancelButton = new SpaceWarButton("CANCEL");
		SpaceWarButton joinButton = new SpaceWarButton("JOIN");
		
		cancelButton.setLayoutX(60); cancelButton.setLayoutY(300);
		joinButton.setLayoutX(340); joinButton.setLayoutY(300);
		
		roomSubScene.getPane().getChildren().addAll(cancelButton, joinButton);
		
		cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				roomSubScene.getPane().getChildren().clear();
				createButtonOfRoomSubScene();
			}
		});
		
		joinButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String roomName = joinRoomInfor.getRoomName();
				String roomPass = joinRoomInfor.getPassword();
				
				if (roomName == null || roomName.isEmpty()) CheckAndAlert.alertErrorMessage("Vui lòng nhập tên phòng");
				
				Client.sendJoinRoomRequest(roomName, roomPass, user.getName());
			}
		});
    }

    public void createAndSetRoomUI() {
    	
    }
    
    /*======================== End roomSubScene ========================*/
    
    
    /*
     * Set roomSubScene's elements
    */
    
    /*======================== End roomSubScene ========================*/
    
    
    /*
     * Set roomSubScene's elements
    */
    
    /*======================== End roomSubScene ========================*/
    
    private void createBackground() {
        Image backgroundImage = new Image("/view/resources/blue.png", 256, 256, false, true);
        BackgroundImage bi = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, 
        		BackgroundPosition.DEFAULT, (BackgroundSize)null);
        
        mainPane.setBackground(new Background(bi));
    }

    private void createLogo() {
        ImageView logo = new ImageView("/view/resources/logox.png");
        logo.setLayoutX(450);
        logo.setLayoutY(40);
        logo.setStyle("-fx-cursor: hand;");
        logo.setEffect(new DropShadow());
        
        mainPane.getChildren().add(logo);
    }
    
    /*
     * Handle response from Server
    */
    private void createTCPHandler() {
    	TCPViewManager tcpHandler = new TCPViewManager(data ->  {
    		Platform.runLater(() -> {
    			JSONObject json_data = (JSONObject) JSONValue.parse(data);
        		
        		long tcp_code = (long) json_data.get("tcp_code");
        		
        		switch ((int)tcp_code) {
        			case ResponseCode.ROOM_CREATION_RES:
        				roomCreationHanler(json_data);
        				break;
        				
        			case ResponseCode.JOIN_ROOM_RES:
    					joinRoomHandler(json_data);
    					break;
    					
    				case UpdateCode.NEW_MEMBER:
    					newMemberHandler(json_data);
    					break;
        		}
    		});
    	});
    	
    	tcpHandler.start();
    }
    /*======================== end handle ========================*/

	private void newMemberHandler(JSONObject requireUpdate) {
		String newMemberName = (String) requireUpdate.get("new_member");
		CheckAndAlert.alertSuccessMessage(newMemberName + " đã tham gia");
	}

	private void joinRoomHandler(JSONObject response) {
		long status = (long) response.get("status");
		
		if (status == 0) {
			long error_code = (long) response.get("error_code");
			String message = (String) response.get("message");
			CheckAndAlert.alertErrorMessage((int)error_code, message);
		} else {
	//		viewManager.createAndSetRoomUI();
			JSONArray teamArray = (JSONArray) response.get("team");
			CheckAndAlert.alertSuccessMessage("Vào thành công " + teamArray.toJSONString());
		}
	}

	private void roomCreationHanler(JSONObject response) {
		long status = (long) response.get("status");
		
		if (status == 0) {
			long error_code = (long) response.get("error_code");
			String message = (String) response.get("message");
			CheckAndAlert.alertErrorMessage((int)error_code, message);
		} else {
	//		viewManager.createAndSetRoomUI();
			CheckAndAlert.alertSuccessMessage("Tạo phòng thành công");
		}
	}
}


























