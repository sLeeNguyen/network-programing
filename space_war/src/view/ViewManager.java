package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helpers.CheckAndAlert;
import helpers.Resource;
import helpers.UserInformation;
import helpers.code.ServerCode;
import helpers.connect.Client;
import helpers.connect.Client.TCPClient;
import helpers.controllers.ListRoomViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.InfoLabel;
import model.JoinRoomSubScene;
import model.RoomCreationSubScene;
import model.SHIP;
import model.ShipPicker;
import model.SpaceWarButton;
import model.SpaceWarSubScene;
import model.element.EType;
import model.element.Ship;

public class ViewManager {
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1280;
    private static final int MENU_BUTTONS_START_X = 100;
    private static final int MENU_BUTTONS_START_Y = 180;
    
    public static UserInformation user;
    public static SHIP yourShip;
    
    private GamePlay game;
    private AnchorPane root;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    
    private SpaceWarSubScene chooseShipSubScene;
    private SpaceWarSubScene roomSubScene;
    private SpaceWarSubScene helpSubScene;
    private SpaceWarSubScene creditsSubScene;
    private SpaceWarSubScene currentSubScene;
    
    // room's button
    private SpaceWarButton startBtn;
    private SpaceWarButton clearBtn;    
    private SpaceWarButton readyBtn;
    private SpaceWarButton unreadyBtn;
    private SpaceWarButton leaveBtn;
    
    List<SpaceWarButton> menuButtons;
    
    TCPClient tcpHandler;
    
    private PlayerInfor[] playersView;
    private PlayerInfor you;
    private int curr_size;
    private int roomId;

    private VBox roomInfor;
    private VBox chatFrame;
    
    public ViewManager() {  
    	root = new AnchorPane();
    	mainPane = new AnchorPane();
    	mainPane.setPrefHeight(HEIGHT);
    	mainPane.setPrefWidth(WIDTH);
    	
    	yourShip = SHIP.BLUE1;
    	roomId = -1;
    	setUpAll();
    	
        mainScene = new Scene(root, WIDTH, HEIGHT + 40);
        mainStage = new Stage();
        mainStage.setScene(mainScene);
    }
    
    public Stage getMainStage() {
        return mainStage;
    }

    private void setUpAll() {
    	createAll();
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
		
		showSubScene(chooseShipSubScene);
    	createTCPHandler();
    }
    
    private void createAll() {
    	createButton();
        createBackground();
        createLogo();
        createSubScene();
    }
    
    private void createButton() {
    	menuButtons = new ArrayList<>();
    	
    	createChooseShipButton();
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

    private void createChooseShipButton() {
        SpaceWarButton chooseShipButton = new SpaceWarButton("SHIP");
        addMenuButton(chooseShipButton);
        
        chooseShipButton.setOnAction(new EventHandler<ActionEvent>() {
            
        	public void handle(ActionEvent event) {
                showSubScene(chooseShipSubScene);
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
        		if (CheckAndAlert.alertConfirmMessage("Do you want to exit?")) {
            		if (ViewManager.user != null) {
            			Client.sendUserExitRequest(ViewManager.user.getName());
            		}
            		Platform.exit();
            		System.exit(0);
            	}
            }
        });
    }
    
    private void createBackground() {
        Image backgroundImage = new Image("/resources/background/back1.jpg", 1280, 768, false, true);
        BackgroundImage bi = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
        		BackgroundPosition.DEFAULT, (BackgroundSize)null);
        
        mainPane.setBackground(new Background(bi));
    }

    private void createLogo() {
        ImageView logo = new ImageView("/resources/logo/logox.png");
        logo.setLayoutX(530);
        logo.setLayoutY(40);
        logo.setEffect(new DropShadow());
        
        mainPane.getChildren().add(logo);
    }

    
    /*
     * Set chooseShipSubScene's elements
    */
    private void createShipChooserSubScene() {
    	chooseShipSubScene = new SpaceWarSubScene();
        mainPane.getChildren().add(chooseShipSubScene);
        createButtonOfShipChooserSubScene();
    }
    
    private void createButtonOfShipChooserSubScene() {
    	chooseShipSubScene.getPane().getChildren().clear();
        SpaceWarButton blueButton = new SpaceWarButton("BLUE SHIP"); 
    	SpaceWarButton greenButton = new SpaceWarButton("GREEN SHIP");
    	SpaceWarButton orangeButton = new SpaceWarButton("ORANGE SHIP");
    	SpaceWarButton redButton = new SpaceWarButton("RED SHIP");
    	
    	double baseX = 175;
    	double baseY = 50;
    	blueButton.changeButton(); blueButton.setLayoutX(baseX); blueButton.setLayoutY(baseY);
    	greenButton.changeButton();	greenButton.setLayoutX(baseX); greenButton.setLayoutY(baseY + 70);
    	orangeButton.changeButton(); orangeButton.setLayoutX(baseX); orangeButton.setLayoutY(baseY + 70*2);
    	redButton.changeButton(); redButton.setLayoutX(baseX); redButton.setLayoutY(baseY + 70*3);
    	chooseShipSubScene.getPane().getChildren().addAll(blueButton, greenButton, orangeButton, redButton);
    	
    	blueButton.setOnMouseClicked(e -> {
    		showShipsByColor("BLUE");
    	});
    	
    	greenButton.setOnMouseClicked(e -> {
    		showShipsByColor("GREEN");
    	});
    	
    	orangeButton.setOnMouseClicked(e -> {
    		showShipsByColor("ORANGE");
    	});
    	
    	redButton.setOnMouseClicked(e -> {
    		showShipsByColor("RED");
    	});
    }
    
    private void showShipsByColor(String color) {
    	chooseShipSubScene.getPane().getChildren().clear();
    	InfoLabel chooseLabel = new InfoLabel("CHOOSE YOUR SHIP");
        chooseLabel.setLayoutX(110);
        chooseLabel.setLayoutY(30);
        chooseShipSubScene.getPane().getChildren().add(chooseLabel);
        
        HBox choosenList = createListShip(color);
        chooseShipSubScene.getPane().getChildren().add(choosenList);
        
        FontAwesomeIconView iconBack = new FontAwesomeIconView(FontAwesomeIcon.CHEVRON_CIRCLE_LEFT);
        iconBack.setSize("3.3em"); iconBack.setLayoutX(35); iconBack.setLayoutY(68); iconBack.setCursor(Cursor.HAND);
        switch (color) {
        	case "BLUE":
        		iconBack.setFill(Paint.valueOf("#41BAF0"));
        		break;
        		
        	case "GREEN":
        		iconBack.setFill(Paint.valueOf("#75C643"));
        		break;
        		
        	case "ORANGE":
        		iconBack.setFill(Paint.valueOf("#D75C3B"));
        		break;
        		
        	case "RED":
        		iconBack.setFill(Paint.valueOf("#A94445"));
        		break;
        		
    		default: break;
        }
        
        iconBack.setOnMouseClicked(e -> {
        	chooseShipSubScene.getPane().getChildren().clear();
        	createButtonOfShipChooserSubScene();
        });
        
        chooseShipSubScene.getPane().getChildren().add(iconBack);
    }
    
    private HBox createListShip(String color) {
        HBox box = new HBox();
        box.setSpacing(40);
        
        List<ShipPicker> shipPickers = new ArrayList<>();
        List<SHIP> listShips = new ArrayList<>();
        
        for (int i = 1; i <= 3; ++i) {
        	listShips.add(SHIP.valueOf(color+i));
        }
        
        for(SHIP ship: listShips) {
            final ShipPicker sp = new ShipPicker(ship);
            shipPickers.add(sp);
            box.getChildren().add(sp);
            if (sp.getSHIP().name().equals(yourShip.name())) sp.setCircleChoosen(true);
            
            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
               
            	public void handle(MouseEvent event) {
            		
            		for (ShipPicker sp: shipPickers) {
            			sp.setCircleChoosen(false);
            		}

                    sp.setCircleChoosen(true);
                    yourShip = sp.getSHIP();
                }
            });
        }
        
        // set default SHIP
//        shipPickers.get(0).setCircleChoosen(true);
//        yourShip = shipPickers.get(0).getSHIP();

        box.setLayoutX(110);
        box.setLayoutY(150);
        
        return box;
    }
    /*======================== End chooseShipSubScene ========================*/
    
    
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
    	SpaceWarButton viewAllRoomButton = new SpaceWarButton("ALL ROOM");
    	createRoomButton.changeButton(); createRoomButton.setLayoutX(175); createRoomButton.setLayoutY(100);
    	joinRoomButton.changeButton();	joinRoomButton.setLayoutX(175); joinRoomButton.setLayoutY(180);
    	viewAllRoomButton.changeButton(); viewAllRoomButton.setLayoutX(175); viewAllRoomButton.setLayoutY(260);
    	
    	roomSubScene.getPane().getChildren().addAll(createRoomButton, joinRoomButton, viewAllRoomButton);
    	
    	createRoomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				RoomCreationSubScene roomInfor = new RoomCreationSubScene(50, 50);
				roomSubScene.getPane().getChildren().clear();
				roomSubScene.getPane().getChildren().add(roomInfor);		
				setButtonOfRoomInfor(roomInfor);
			}
		});
    	
    	joinRoomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				JoinRoomSubScene joinRoomInfor = new JoinRoomSubScene(50, 50);
				roomSubScene.getPane().getChildren().clear();
				roomSubScene.getPane().getChildren().add(joinRoomInfor);
				setButtonOfJoinRoom(joinRoomInfor);
			}
		});
    	
    	viewAllRoomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				Client.sendViewAllRoomRequest();
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
				
				if (roomName == null || roomName.isEmpty()) {
					CheckAndAlert.alertErrorMessage("Vui lòng nhập tên phòng");
					return;
				}

				Client.sendRoomCreationRequest(roomName, user.getName(), size, roomPass, yourShip.name());
				
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
				
				if (roomName == null || roomName.isEmpty()) {
					CheckAndAlert.alertErrorMessage("Vui lòng nhập tên phòng");
					return;
				}
				
				Client.sendJoinRoomRequest(roomName, roomPass, user.getName(), yourShip.name());
			}
		});
    }

    private void createAndSetRoomUI(int roomId, String roomName, long roomSize, boolean hasPass) {
    	mainPane.getChildren().clear();
    	createTeamView(playersView, roomId, roomName, roomSize, hasPass);
    }
    
    public class PlayerInfor extends VBox {
    	private String name;
    	private Ship ship;
    	private HBox box;
    	private boolean owner;
    	
    	public PlayerInfor() {
    		setEmpty();
    		owner = false;
    	}
    	
    	public PlayerInfor(Ship ship, String name) {
    		setEmpty();
    		setShipView(ship, name);
    		
    		owner = false;
    	}
    	
    	private void setEmpty() {
    		setSpacing(20);
    		setPadding(new Insets(10, 0, 40, 0));
    		setPrefSize(200, 350);
    		setAlignment(Pos.TOP_CENTER);
    		setStyle("-fx-background-color: #ffeb00ab; -fx-background-radius: 10px");
    		box = new HBox();
    		box.setPrefHeight(130);
    	}
    	
    	public void setShipView(Ship ship, String name) {
    		this.ship = ship;
    		this.name = name;
    		
    		ImageView readyImg = new ImageView(Resource.READY_PATH);
    		box.getChildren().add(readyImg);
    		box.setAlignment(Pos.CENTER);
    		box.setVisible(false);
    		
    		Label nameLB = new Label(this.name);
    		nameLB.setTextFill(Color.WHITESMOKE);
    		try {
				nameLB.setFont(Font.loadFont(new FileInputStream("src/resources/fonts/Cunia.ttf"), 21));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    		
    		getChildren().addAll(box, ship, nameLB);
    	}
    	
    	public void ready() {
    		box.setVisible(true);
    	}
    	
    	public void unReady() {
    		box.setVisible(false);
    	}
    	
    	public Ship getShip() {
    		return ship;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public void setOwner(boolean isOwner) {
    		this.owner = isOwner;
    		if (owner == true) {
    			box.getChildren().clear();
    			ImageView ownerImg = new ImageView(Resource.OWNER_PATH);
    			box.getChildren().add(ownerImg);
    			box.setVisible(true);
    		}
    	}
    	
    	public boolean isOwner() {
    		return owner;
    	}
    	
    	public void reset() {
    		owner = false;
    		ship = null;
    		box.getChildren().clear();
    		getChildren().clear();
    	}
    }
    
    private void createTeamView(PlayerInfor[] players, int roomId, String roomName, long roomSize, boolean hasPass) {
    	InfoLabel yourTeam = new InfoLabel("Your Team");
    	yourTeam.setLayoutX(680);
    	yourTeam.setLayoutY(120);
    	
    	HBox teamView = new HBox(30);
    	
    	teamView.setAlignment(Pos.CENTER);
    	teamView.setLayoutX(550);
    	teamView.setLayoutY(200);
    	teamView.setEffect(new DropShadow());
    	teamView.getChildren().addAll(players);
    	
    	double baseX = 550D;
    	leaveBtn = new SpaceWarButton("LEAVE"); leaveBtn.setLayoutX(baseX); leaveBtn.setLayoutY(650);
    	clearBtn = new SpaceWarButton("CLEAR"); clearBtn.setLayoutX(baseX + 250); clearBtn.setLayoutY(650);
    	startBtn = new SpaceWarButton("START"); startBtn.setLayoutX(baseX + 250*2); startBtn.setLayoutY(650);
    	readyBtn = new SpaceWarButton("READY"); readyBtn.setLayoutX(baseX + 250*2); readyBtn.setLayoutY(650); 
    	unreadyBtn = new SpaceWarButton("UNREADY"); unreadyBtn.setLayoutX(baseX + 250*2); unreadyBtn.setLayoutY(650); unreadyBtn.setVisible(false);
    	
    	startBtn.setVisible(you.isOwner());
    	clearBtn.setVisible(you.isOwner());
    	readyBtn.setVisible(!you.isOwner());

    	leaveBtn.setOnMouseClicked(e -> {
    		if (CheckAndAlert.alertConfirmMessage("Are you sure?")) {
    			Client.sendLeaveRoomRequest(you.getShip().getID());
    		}
    	});
    	
    	startBtn.setOnMouseClicked(e -> {
    		Client.sendPlayGameRequest();        		
    	});
    	
    	clearBtn.setOnMouseClicked(e -> {
    		if (CheckAndAlert.alertConfirmMessage("Are you sure?")) {
    			Client.sendClearRoomRequest();
    		}
    	});
    	
    	readyBtn.setOnMouseClicked(e -> {
    		you.ready();
    		readyBtn.setVisible(false);
    		unreadyBtn.setVisible(true);
    		leaveBtn.setDisable(true);
    		Client.sendReadyRequest(you.getShip().getID(), 1);
    	});
    	
    	unreadyBtn.setOnMouseClicked(e -> {
    		you.unReady();
    		unreadyBtn.setVisible(false);
    		readyBtn.setVisible(true);
    		leaveBtn.setDisable(false);
    		Client.sendReadyRequest(you.getShip().getID(), 0);
    	});
    	
    	
    	mainPane.getChildren().addAll(yourTeam, teamView, leaveBtn, clearBtn, startBtn, readyBtn, unreadyBtn);
    	/************************************************************************************/
    	    	
    	roomInfor = new VBox();
    	Label name = new Label("ROOM: " + roomName); name.setStyle("-fx-text-fill: #ffff; -fx-font-size: 20px;"); 
    	Label size = new Label("Size: " + curr_size + "/" + roomSize); size.setStyle("-fx-text-fill: #ffff; -fx-font-size: 20px;");
    	
    	roomInfor.getChildren().addAll(name, size);
    	roomInfor.setLayoutX(30);
    	roomInfor.setLayoutY(30);    	
    	mainPane.getChildren().add(roomInfor);
    	
    	createChat();
    }
    
    private void createChat() {
    	VBox chatBox = new VBox();
    	chatBox.setLayoutX(50);
    	chatBox.setLayoutY(120);
    	chatBox.setAlignment(Pos.CENTER);
    	chatBox.getStylesheets().add(this.getClass().getResource("/helpers/styles/chatStyles.css").toExternalForm());
    	
    	ImageView chatImg = new ImageView(Resource.CHAT_PATH);
    	
    	ScrollPane sp = new ScrollPane();
    	sp.setId("scrollPane");
    	chatFrame = new VBox(5);
    	chatFrame.setMaxWidth(370);
    	chatFrame.setPrefWidth(380);
    	chatFrame.setPadding(new Insets(15, 5, 8, 15));
    	sp.setContent(chatFrame); 
    	sp.setMaxHeight(400);
    	sp.setPrefHeight(400);
    	sp.fitToWidthProperty().set(true);
    	sp.vvalueProperty().bind(chatFrame.heightProperty());
    	sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);

    	TextField inputText = new TextField();
    	inputText.setId("inputText");
    	inputText.setPromptText("Type your message...");
    	inputText.setPrefWidth(300);
    	inputText.setOnKeyPressed(e -> {
    		if (e.getCode() == KeyCode.ENTER) {
    			String content = inputText.getText().trim();
    			if (!content.isEmpty()) {
    				chatFrame.getChildren().add(createMessageView(user.getName(), content, "#ffd400", "#fff"));
    				Client.sendMessage(user.getName(), content);
    			}
    			inputText.clear();
    		}
    	});
    	
    	ImageView faceImg = new ImageView(Resource.FACE_PATH);
    	Button btnSend = new Button();
    	btnSend.setId("btnSend");
    	btnSend.setPrefWidth(32);
    	btnSend.setOnMouseClicked(e -> {
    		String content = inputText.getText().trim();
			if (!content.isEmpty()) {
				chatFrame.getChildren().add(createMessageView(user.getName(), content, "#ffd400", "#fff"));
				Client.sendMessage(user.getName(), content);
			}
			inputText.clear();
    	});
    	
    	
    	HBox chatBar = new HBox(5);
    	chatBar.setId("chatBar");
    	chatBar.setAlignment(Pos.CENTER);
    	chatBar.getChildren().addAll(faceImg, inputText, btnSend);
    	
    	chatBox.getChildren().addAll(chatImg, sp, chatBar);
    	mainPane.getChildren().addAll(chatBox);
    }
    
    private TextFlow createMessageView(String sender, String message, String...fill) {
    	TextFlow messageFlow = new TextFlow();
    	messageFlow.setPrefWidth(370);
    	
    	if (sender == null) {
    		Text mes = new Text(message);
    		mes.setStyle("-fx-fill: #0a79cd; -fx-font-weight: bold; -fx-font-size: 16px;");
    		messageFlow.getChildren().add(mes);
    	} else {
    		String idStyle = "-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 16px;";
        	String mesStyle = "-fx-fill: %s; -fx-font-size: 16px;";
        	
        	Label id = new Label(String.format("[%s]: ", sender)); id.setStyle(String.format(idStyle, fill[0]));
        	Text mes = new Text(message); mes.setStyle(String.format(mesStyle, fill[1]));
        	messageFlow.getChildren().addAll(id, mes);
    	}
    	
    	return messageFlow;
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
    
    
    /*
     * Handle response from Server
    */
    void createTCPHandler() {
    	tcpHandler = new TCPClient(data ->  {
    		Platform.runLater(() -> {
//    			System.out.println("VM: " + data);
    			JSONObject json_data = (JSONObject) JSONValue.parse(data);
        		
        		long tcp_code = (long) json_data.get("tcp_code");
        		
        		switch ((int)tcp_code) {
	        		case ServerCode.CHAT_RES:
	        			chatHandler(json_data);
	        			break;
        		
        			case ServerCode.ESTABLISH_UDP_CONNECTION_RES:
        				udpConnection();
        				break;
        		
        			case ServerCode.LEAVE_ROOM_RES:
        				leaveRoomHandler(json_data);
        				break;
        				
        			case ServerCode.LEAVE_ROOM_NOTIFY_RES:
        				deletePlayer(json_data);
        				break;
        				
        			case ServerCode.SET_NEW_OWNER_RES:
        				setOwnerRoom(json_data);
        				break;
        				
        			case ServerCode.CLEAR_ROOM_RES:
        				clearRoomHandler(json_data);
        				break;
        				
        			case ServerCode.ROOM_CREATION_RES:
        				roomCreationHanler(json_data);
        				break;
        				
        			case ServerCode.JOIN_ROOM_RES:
    					joinRoomHandler(json_data);
    					break;
    					
        			case ServerCode.VIEW_ALL_ROOM_RES:
        				viewAllRoomHandler(json_data);
        				break;
    					
    				case ServerCode.NEW_MEMBER_RES:
    					newMemberHandler(json_data);
    					break;
    					
    				case ServerCode.READY_RES:
    					readyHandler(json_data);
    					break;
    				
    				case ServerCode.PLAY_GAME_RES:
    					gamePlayHandler(json_data);
    					break;
        		}
    		});
    	});
    	
    	tcpHandler.start();
    	
    	if (roomId != -1) {
    		if (!you.isOwner()) {
        		unreadyBtn.setVisible(false);
        		readyBtn.setVisible(true);
        		leaveBtn.setDisable(false);
    		}
    		
    		for (PlayerInfor pi: playersView) {
    			if (pi.getShip() != null && !pi.isOwner()) {
    				pi.unReady();
    			}
    		}
    	}
    }

    
    private void setOwnerRoom(JSONObject json_data) {
    	int ownerId = (int) (long) json_data.get("owner_id");
    	
    	playersView[ownerId].setOwner(true);
    	if (ownerId == you.getShip().getID()) {
    		startBtn.setVisible(true);
        	clearBtn.setVisible(true);
        	readyBtn.setVisible(false);
    	}
    }
    
    private void readyHandler(JSONObject json_data) {
    	JSONArray dataArr = (JSONArray) json_data.get("data");
    	int playerId = (int) (long) dataArr.get(0);
    	int status = (int) (long) dataArr.get(1);
    	
    	if (status == 0) {
    		playersView[playerId].unReady();
//    		startBtn.setDisable(true);
    	} else {
    		playersView[playerId].ready();
    	}
    }
    
    private void clearRoomHandler(JSONObject json_data) {
    	int status = (int) (long) json_data.get("status");
    	
    	if (status == 1) {
    		roomId = -1;
    		mainPane.getChildren().clear();
    		createAll();
    		showSubScene(roomSubScene);
    		
    	} else {
    		int code = (int) (long) json_data.get("error_code");
    		String msg = (String) json_data.get("message");
    		CheckAndAlert.alertErrorMessage(code, msg);
    	}
    }
    
    private void deletePlayer(JSONObject json_data) {
    	int playerId = (int) (long) json_data.get("player_id");
    	
    	String playerName = playersView[playerId].getName();
    	playersView[playerId].reset();
    	chatFrame.getChildren().add(createMessageView(null, String.format("%s has left the room", playerName)));
    	Label size = (Label) roomInfor.getChildren().get(1);
		size.setText("Size: " + --curr_size + "/" + size.getText().split("/")[1]);
    }
    
    private void leaveRoomHandler(JSONObject json_data) {
    	int status = (int) (long) json_data.get("status");
    	
    	if (status == 1) {
    		roomId = -1;
    		mainPane.getChildren().clear();
    		createAll();
    		showSubScene(roomSubScene);
    		
    	} else {
    		int code = (int) (long) json_data.get("error_code");
    		String msg = (String) json_data.get("message");
    		CheckAndAlert.alertErrorMessage(code, msg);
    	}
    }
    
    private void chatHandler(JSONObject json_data) {
    	JSONArray data = (JSONArray) json_data.get("data");
    	String sender = (String) data.get(0);
    	String message = (String) data.get(1);
    	
    	chatFrame.getChildren().add(createMessageView(sender, message, "#f1b600", "#fff"));
    }
    
    private void udpConnection() {
    	Client.sendUdpConnection(roomId, user.getName());
    }
    
    private void gamePlayHandler(JSONObject response) {
    	int status = (int) (long) response.get("status");

    	if (status != 0) {
    		tcpHandler.stopThread();
    		game = new GamePlay();
    		game.createNewGame(this, playersView, this.roomId);
    	} else if (status == 0) {
    		int errorCode = (int) (long) response.get("error_code");
    		String message = (String) response.get("message");
    		CheckAndAlert.alertErrorMessage(errorCode, message);
    	}
    }
    
	private void newMemberHandler(JSONObject requireUpdate) {
		JSONObject newMember = (JSONObject) requireUpdate.get("new_member");
		if (newMember != null) {
//			startBtn.setDisable(true);
			int playerId = (int) (long) newMember.get("id");
			SHIP shipE = SHIP.valueOf((String) newMember.get("ship"));
			playersView[playerId].setShipView(new Ship(shipE, EType.ANOTHER_PLAYER, 12, 100, 75, playerId), (String)newMember.get("name"));
			Label size = (Label) roomInfor.getChildren().get(1);
			size.setText("Size: " + ++curr_size + "/" + size.getText().split("/")[1]);
			
			chatFrame.getChildren().add(createMessageView(null, String.format("%s has joined!", (String) newMember.get("name"))));
		}
	}
	
	private void viewAllRoomHandler(JSONObject response) {
		int status = (int) (long) response.get("status");
		
		if (status == 1) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/helpers/fxml/list_room_view.fxml"));
				Node node = loader.load();
				node.setLayoutX(200); node.setLayoutY(50);
				
				ListRoomViewController rvc = loader.getController();
				
				mainPane.getChildren().forEach(child -> child.setVisible(false));
				mainPane.getChildren().add(node);
				
				JSONArray listRoom = (JSONArray) response.get("list_room");
				for (int i = 0; i < listRoom.size(); ++i) {
					JSONArray r = (JSONArray) listRoom.get(i);
					int roomID = (int) (long) r.get(0);
					String roomName = (String) r.get(1);
					String capacity = (String) r.get(2);
					boolean isRunning = (boolean) r.get(3);
					boolean hasPass = (boolean) r.get(4);
					rvc.addRoomRow(Integer.toString(roomID), roomName, capacity, isRunning, hasPass);
				}
				
				SpaceWarButton backBtn = new SpaceWarButton("BACK");
				backBtn.setLayoutX(200); backBtn.setLayoutY(700);
				mainPane.getChildren().add(backBtn);
				backBtn.setOnMouseClicked(e -> {
					mainPane.getChildren().removeAll(node, backBtn);
					mainPane.getChildren().forEach(child -> child.setVisible(true));
				});
				
			} catch (IOException e) {
				//pass
			}
		}
	}
	
	private void joinRoomHandler(JSONObject response) {
		long status = (long) response.get("status");
		
		if (status == 0) {
			long error_code = (long) response.get("error_code");
			String message = (String) response.get("message");
			CheckAndAlert.alertErrorMessage((int)error_code, message);
		} else {
			JSONObject room = (JSONObject) response.get("room");
			JSONArray teamArray = (JSONArray) room.get("team");
			
			this.roomId = (int) (long) room.get("room_id");
			int ownerId = (int) (long) room.get("owner_id");
			
			curr_size = teamArray.size();
			playersView = new PlayerInfor[3];
			playersView[0] = new PlayerInfor();
			playersView[1] = new PlayerInfor();
			playersView[2] = new PlayerInfor();

			for (int i = 0; i < teamArray.size(); ++i) {
				JSONObject player = (JSONObject) teamArray.get(i);
				int playerId = (int) (long) player.get("id");
				String playerName = (String) player.get("name");
				SHIP shipE = SHIP.valueOf((String) player.get("ship"));
				int readied = (int) (long) player.get("readied");
 				
				if (playerName.equals(user.getName())) {
					playersView[playerId].setShipView(new Ship(shipE, EType.YOU, 12, 100, 75, playerId), playerName);
					you = playersView[playerId];
				}
				else {
					playersView[playerId].setShipView(new Ship(shipE, EType.ANOTHER_PLAYER, 12, 100, 75, playerId), playerName);
					if (readied == 1) playersView[playerId].ready();
				}
			}
			playersView[ownerId].setOwner(true);
			
			createAndSetRoomUI(this.roomId, (String) room.get("room_name"), (long) room.get("room_size"), (long) room.get("has_pass") == 1);
			chatFrame.getChildren().add(createMessageView(null, "You have joined the room!"));
		}
	}

	private void roomCreationHanler(JSONObject response) {
		long status = (long) response.get("status");
		
		if (status == 0) {
			long error_code = (long) response.get("error_code");
			String message = (String) response.get("message");
			CheckAndAlert.alertErrorMessage((int)error_code, message);
		} else {
			JSONObject room = (JSONObject) response.get("room");
			this.roomId = (int)((long) room.get("room_id"));
			
			playersView = new PlayerInfor[3];
			playersView[0] = you = new PlayerInfor(new Ship(yourShip, EType.YOU, 40, 100, 75, 0), user.getName()); you.setOwner(true);
			playersView[1] = new PlayerInfor();
			playersView[2] = new PlayerInfor();
			
			curr_size = 1;
			createAndSetRoomUI(this.roomId, (String) room.get("room_name"), (long) room.get("room_size"), (boolean) room.get("has_pass"));
			chatFrame.getChildren().add(createMessageView(null, "You have created the room!"));
		}
	}
	
    /*======================== end handle ========================*/
}


























