package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import helpers.Action;
import helpers.CheckAndAlert;
import helpers.Resource;
import helpers.code.ServerCode;
import helpers.connect.Client;
import helpers.connect.Client.TCPClient;
import helpers.connect.Client.UDPClient;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import model.InfoLabel;
import model.SpaceWarButton;
import model.element.Bullet;
import model.element.EType;
import model.element.Element;
import model.element.Shield;
import model.element.Ship;
import view.ViewManager.PlayerInfor;

public class GamePlay {
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 850;
    
    private static final String BACKGROUND_URL = "/resources/background/purple.png";
    
    private Font font;
    private ViewManager manager;
    private AnchorPane root;
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    
    private GridPane gridPane1;
    private GridPane gridPane2;
        
    private boolean isLeftPressed;
    private boolean isRightPressed;
    
    private AnimationTimer animationGame;
    
    private int roomId;
    private Ship[] ships;
    private Ship yourShip;
    private InfoLabel pointsLabel;
    
    private TCPClient tcpHandler;
    private UDPClient udpHandler;

    public GamePlay() {
        initializeStage();
        createKeyListeners();
        try {
			font = Font.loadFont(new FileInputStream(Resource.FONT_PATH_1), 20);
		} catch (FileNotFoundException e) {
			//pass
		}
        
    }

    private void createKeyListeners() {
        
    	gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
           
        	public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.LEFT) {
                    isLeftPressed = true;
                    
                } else if (event.getCode() == KeyCode.RIGHT) {
                    isRightPressed = true;
                    
                } else if (event.getCode() == KeyCode.SPACE) {
                	if (!yourShip.isDead()) {
	                	gamePane.getChildren().add(yourShip.shoot());
	                	Client.sendPlayerActionData(roomId, yourShip.getID(), yourShip.getLayoutX(), Action.SHOOT);
                	}
            	}

            }
        });
        
        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            
        	public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.LEFT) {
                    isLeftPressed = false;
                    
                } else if (event.getCode() == KeyCode.RIGHT) {
                    isRightPressed = false;
                }
            }
        });
    }

    private void initializeStage() {
    	root = new AnchorPane();
        gamePane = new AnchorPane();
        gameScene = new Scene(root, GAME_WIDTH, GAME_HEIGHT + 40);
        
        gamePane.setPrefHeight(GAME_HEIGHT);
        gamePane.setPrefWidth(GAME_WIDTH);
        
        gameStage = new Stage();
        gameStage.initStyle(StageStyle.UNDECORATED);
        gameStage.setScene(gameScene);
    }

    public Stage getGameStage() {
        return gameStage;
    }

    public void createNewGame(ViewManager manager, PlayerInfor[] players, int roomId) {
    	this.roomId = roomId;
        this.manager = manager;
        manager.getMainStage().hide();
        setUpAll(players);
        gameStage.show();
    }
    
    private void setUpAll(PlayerInfor[] players) {
    	createBackground();
        setShips(players);
        createPointLabel();
        createGameLoop();
        
    	root.getChildren().add(gamePane);
    	
    	AnchorPane.setTopAnchor(gamePane, 40D);
    	AnchorPane.setBottomAnchor(gamePane, 0D);
    	AnchorPane.setLeftAnchor(gamePane, 0D);
    	AnchorPane.setRightAnchor(gamePane, 0D);
    	
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
    	
		createTCPHandler();
		createUDPHandler();
    }

    private void setShips(PlayerInfor[] players) {
    	ships = new Ship[3];
    	for (int i = 0; i < players.length; ++i) {
    		Ship ship = players[i].getShip();
    		if (ship != null) {
    			Ship shipClone = new Ship(ship.getShipE(), ship.getType(), ship.getRadius(), ship.getWidth(), ship.getHeight(), ship.getID());
        		if (shipClone.getType() == EType.YOU) {
        			yourShip = shipClone;
        			yourShip.setBulletType(EType.YOUR_BULLET);
        		} else shipClone.setBulletType(EType.PLAYER_BULLET);
        		
        		ships[shipClone.getID()] = shipClone;
        		shipClone.setLayoutX(200 + 330*i);
        		shipClone.setLayoutY(GAME_HEIGHT - shipClone.getHeight() - 20);
        		gamePane.getChildren().add(shipClone);
    		}
    	}
    }

    private List<Element> elements() {
    	return gamePane.getChildren().stream().filter(n -> n instanceof Element).map(n -> (Element) n).collect(Collectors.toList());
    }
    
    private void war() {
    	elements().forEach(e -> {
    		switch (e.getType()) {
    			
    			case EType.YOUR_BULLET: case EType.PLAYER_BULLET:
    				movePlayerBullet((Bullet) e);
    				break;
    			
	    		case EType.ENEMY:
	    			moveEnemyShip((Ship) e);
	    			break;
	    			
	    		case EType.ENEMY_BULLET:
	    			moveEnemyBullet((Bullet) e);
	    			break;
    		}
    	});
    	
    	gamePane.getChildren().removeIf(n -> {
    		if (n instanceof Element) {
    			return ((Element) n).isDead();
    		}
    		return false;
    	});
    }
    
    private void moveEnemyShip(Ship enemy) {
    	enemy.moveDown();
		if (enemy.checkShoot()) {
			gamePane.getChildren().add(enemy.shoot());
		}
		if (enemy.getLayoutY() > GAME_HEIGHT) {
			Client.sendEnemyDeadRequest(enemy.getID(), -1);
			enemy.dead(true);
			return;
		}
		for (Ship ship: ships) {
			if (ship != null) {
				if (checkCollide(ship, enemy)) {
    				enemy.dead(true);
    				enemy.deleteShield();
    				if (ship.getType() == EType.YOU) Client.sendEnemyDeadRequest(enemy.getID(), yourShip.getID());
    				Shield shield = ship.useShield();
    				if (shield != null) {
    					showShield(ship, shield);
    				} else {
	    				if (ship.getType() == EType.YOU) Client.sendPlayerDeadRequest(yourShip.getID());
    				}
    			}
			}
		}
    }
    
    private void showShield(Ship ship, Shield shield) {
    	gamePane.getChildren().add(shield);
    	FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shield);
    	fadeIn.setFromValue(0);
    	fadeIn.setToValue(1);
    	fadeIn.play();
    	fadeIn.setOnFinished(e -> {
    		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), shield);
    		fadeOut.setDelay(Duration.millis(4));
    		fadeOut.setFromValue(1);
    		fadeOut.setToValue(0);
    		fadeOut.play();
    		fadeOut.setOnFinished(n -> ship.deleteShield());
    	});
    }
    
    private void moveEnemyBullet(Bullet b) {
    	b.moveDown(10);
    	if (b.getLayoutY() > GAME_HEIGHT) b.dead(true);
    	for (Ship player: ships) {
    		if (player != null) {
	    		if (checkCollide(b, player)) {
	    			b.dead(true);
	    			Shield playerShield = player.useShield();
	    			if (playerShield != null) {
	    				showShield(player, playerShield);
	    			}
	    			else if (player.decreaseBlood() <= 0 && player.getType() == EType.YOU) {
	    				Client.sendPlayerDeadRequest(yourShip.getID());
	    			}
	    		}
    		}
    	}
    }
    
    private void movePlayerBullet(Bullet b) {
		b.moveUp(10);
		
		if (b.getLayoutY() < -10) b.dead(true);
		elements().stream().filter(el -> el.getType() == EType.ENEMY).forEach(e -> {
			Ship enemy = (Ship) e;
			if (checkCollide(b, enemy)) {
				b.dead(true);
				Shield enemyShield = enemy.useShield();
				if (enemyShield != null) {
					showShield(enemy, enemyShield);
				}
				else if (enemy.decreaseBlood() <= 0) {
					enemy.dead(true);
					enemy.deleteShield();
					if (b.getType() == EType.YOUR_BULLET) Client.sendEnemyDeadRequest(enemy.getID(), yourShip.getID());	
				}
			}
		});
    }
    
    private void createBackground() {
        gridPane1 = new GridPane();
        gridPane2 = new GridPane();

        for(int i = 0; i < 20; ++i) {
            ImageView background1 = new ImageView(BACKGROUND_URL);
            ImageView background2 = new ImageView(BACKGROUND_URL);
            
            GridPane.setConstraints(background1, i % 5, i / 5);
            GridPane.setConstraints(background2, i % 5, i / 5);
            
            gridPane1.getChildren().add(background1);
            gridPane2.getChildren().add(background2);
        }

        gridPane1.setLayoutY(-1024);
        gamePane.getChildren().addAll(new Node[]{gridPane1, gridPane2});
    }

    private void createPointLabel() {
    	String content = "POINTS: 0";
    	pointsLabel = new InfoLabel(content, 170, 50);
    	pointsLabel.setPadding(new Insets(10, 10, 10, 10));
    	pointsLabel.setAlignment(Pos.CENTER_LEFT);
    	pointsLabel.setLayoutX(GAME_WIDTH - 190);
    	pointsLabel.setLayoutY(10);
    	pointsLabel.setLabelFont(16);
    	gamePane.getChildren().add(pointsLabel);
    }
    
    private void createGameLoop() {
        
    	animationGame = new AnimationTimer() {
            public void handle(long now) {
            	moveYourShip();
                moveBackground();
                war();
            }
        };
        
        animationGame.start();
    }

    private void moveYourShip() {
    	if (yourShip.isDead()) return;
    	
        if (isLeftPressed && !isRightPressed) {
        	Client.sendPlayerActionData(roomId, yourShip.getID(), yourShip.getLayoutX(), Action.MOVE_LEFT);
            yourShip.moveLeft(0);
        }

        if (!isLeftPressed && isRightPressed) {
        	Client.sendPlayerActionData(roomId, yourShip.getID(), yourShip.getLayoutX(), Action.MOVE_RIGHT);
        	yourShip.moveRight(GAME_WIDTH);
        }

        if ((!isLeftPressed && !isRightPressed || isLeftPressed && isRightPressed)) {
        	Client.sendPlayerActionData(roomId, yourShip.getID(), yourShip.getLayoutX(), Action.NONE);
        	yourShip.goStraight();
        }
    }
    
    private void moveBackground() {
        gridPane2.setLayoutY(gridPane2.getLayoutY() + 2);
        gridPane1.setLayoutY(gridPane1.getLayoutY() + 2);
        
        if (gridPane2.getLayoutY() >= 1024) {
            gridPane2.setLayoutY(-1024);
        }

        if (gridPane1.getLayoutY() >= 1024) {
            gridPane1.setLayoutY(-1024);
        }
    }
    
    private double calculateDistance(Pair<Double, Double> coor1, Pair<Double, Double> coor2) {
    	return Math.sqrt(Math.pow(coor1.getKey() - coor2.getKey(), 2) + Math.pow(coor1.getValue() - coor2.getValue(), 2));
    }
    
    private boolean checkCollide(Element e1, Element e2) {
    	if (e1.getRadius() + e2.getRadius() > calculateDistance(e1.getCoordinate(), e2.getCoordinate())) {
    		return true;
    	}
    	return false;
    }

    @SuppressWarnings("static-access")
	private void showGameResult(JSONArray gameRes, boolean isWin) {
    	AnchorPane pane = new AnchorPane();
    	pane.setPrefSize(1000, 700);
    	pane.setStyle("-fx-background-color: #00000085; -fx-background-radius: 10px;");
    	pane.setLayoutX(GAME_WIDTH/2 - 500);
    	pane.setLayoutY(GAME_HEIGHT/2 - 350);
    	
    	ImageView img;
    	if (isWin) {
    		img = new ImageView(Resource.WIN_GAME_PATH);
    		img.setLayoutX(410); img.setLayoutY(20);
    	}
    	else {
    		img = new ImageView(Resource.LOST_GAME_PATH);
    		img.setLayoutX(380); img.setLayoutY(20);
		}
    	
    	
    	HBox headBar = new HBox(60);
    	headBar.setPrefSize(790, 50);
    	headBar.setLayoutX(100); headBar.setLayoutY(180);
    	headBar.setAlignment(Pos.CENTER_RIGHT);
    	headBar.setPadding(new Insets(10, 35, 10, 10));
    	headBar.setStyle("-fx-background-color: #00000099; -fx-background-radius: 10px;");
    	Label nameLB = createGameResultLabel("Player"); headBar.setMargin(nameLB, new Insets(0, 20, 0, 0));
    	Label scoreLB = createGameResultLabel("Score");
    	Label killLB = createGameResultLabel("Enemy killed");
    	Label rankLB = createGameResultLabel("Rank");
    	headBar.getChildren().addAll(nameLB, scoreLB, killLB, rankLB);
    	
    	pane.getChildren().addAll(img, headBar);
    	
    	for (int i = 0; i < gameRes.size(); ++i) {
    		JSONObject obj = (JSONObject) gameRes.get(i);
    		String name = (String) obj.get("player_name");
    		int id = (int) (long) obj.get("player_id");
    		int score = (int) (long) obj.get("score");
    		int numOfEnemiesKilled = (int) (long) obj.get("enemy_killed");
    		
    		HBox row = createPlayerResult(ships[id].getShipE().getUrlShip(), name, ""+score, ""+numOfEnemiesKilled, "TOP "+(i+1));
    		row.setLayoutX(100); row.setLayoutY(240 + 120*i);
    		pane.getChildren().add(row);
    	}
    	
    	SpaceWarButton okBtn = new SpaceWarButton("OK");
    	okBtn.setLayoutX(750); okBtn.setLayoutY(600);
    	okBtn.setOnMouseClicked(e -> {
    		gameStage.close();
    		manager.createTCPHandler();
    		manager.getMainStage().show();
    	});
    	pane.getChildren().add(okBtn);
    	
    	gamePane.getChildren().add(pane);

    	pane.setScaleX(0); pane.setScaleY(0);
    	ScaleTransition transition = new ScaleTransition(Duration.seconds(1), pane);
    	transition.setToX(1);
    	transition.setToY(1);
    	transition.play();
    }
    
    private HBox createPlayerResult(String shipUrl, String playerName, String score, String numOfEnemiesKilled, String rank) {
    	HBox row = new HBox(60);
    	row.setAlignment(Pos.CENTER_LEFT);
    	row.setPadding(new Insets(10, 10, 10, 10));
    	row.setPrefWidth(790);
    	
    	ImageView shipImg = new ImageView(new Image(shipUrl, 100, 75, false, false));
    	
    	VBox nameBox = new VBox();
    	VBox scoreBox = new VBox();
    	VBox killBox = new VBox();
    	VBox rankBox = new VBox();
    	
    	shipImg.setRotate(-30);
    	nameBox.getChildren().add(createGameResultLabel(playerName)); nameBox.setAlignment(Pos.CENTER);
    	scoreBox.getChildren().add(createGameResultLabel(score)); scoreBox.setAlignment(Pos.CENTER); scoreBox.setPrefWidth(100);
    	killBox.getChildren().add(createGameResultLabel(numOfEnemiesKilled)); killBox.setAlignment(Pos.CENTER); killBox.setPrefWidth(100);
    	rankBox.getChildren().add(createGameResultLabel(rank)); rankBox.setAlignment(Pos.CENTER); rankBox.setPrefWidth(100);
    	HBox.setHgrow(nameBox, Priority.ALWAYS);
    	
    	row.getChildren().addAll(shipImg, nameBox, scoreBox, killBox, rankBox);
    	
    	return row;
    }
    
    private Label createGameResultLabel(String content) {
    	Label label = new Label(content);
    	label.setFont(font);
    	label.setTextFill(Paint.valueOf("#ffffff"));
    	
    	return label;
    }
    
    /*
     * Handle response from Server
    */
    private void closeConnections() {
    	tcpHandler.stopThread();
    	udpHandler.stopThread();
    }
    
    private void createTCPHandler() {
    	tcpHandler = new TCPClient(data ->  {
    		Platform.runLater(() -> {
//    			System.out.println(data);
    			JSONObject json_data = (JSONObject) JSONValue.parse(data);
        		
        		int tcp_code = (int) (long) json_data.get("tcp_code");
        		
        		switch (tcp_code) {
        			case ServerCode.ELEMENTS_BATCH_RES:
        				setGameElements(json_data);
        				break;
        				
        			case ServerCode.NEW_SCORE_RES:
        				updateScore(json_data);
        				break;
        				
        			case ServerCode.NEW_LEVEL_RES:
        				showLevel(json_data);
        				break;
        				
        			case ServerCode.PLAYER_DIE_RES:
        				removePlayer(json_data);
        				break;
        				
        			case ServerCode.WIN_GAME_RES: 
        				winGameHandler(json_data);
        				break;
        				
        			case ServerCode.LOST_GAME_RES:
        				lostGameHandler(json_data);
        				break;
        				
    				default: break;
        		}
    		});
    	});
    	
    	tcpHandler.start();
    }
    
    private void removePlayer(JSONObject json_data) {
    	int playerId = (int) (long) json_data.get("player_id");
    	ships[playerId].dead(true);
    	gamePane.getChildren().remove(ships[playerId]);
    }
    
    private void lostGameHandler(JSONObject json_data) {
    	JSONArray gameRes = (JSONArray) json_data.get("game_result");
    	
    	ImageView lostImg = new ImageView(Resource.LOST_GAME_PATH);
    	lostImg.setLayoutX(GAME_WIDTH/2 - 140);
    	lostImg.setLayoutY(GAME_HEIGHT/2 - 60);
    	lostImg.setScaleX(0);
    	lostImg.setScaleY(0);
    	gamePane.getChildren().add(lostImg);
    	closeConnections();
    	
    	ScaleTransition transition = new ScaleTransition(Duration.seconds(2), lostImg);
    	transition.setToX(1.7);
    	transition.setToY(1.7);
    	transition.play();
    	
    	transition.setOnFinished(e -> {
    		FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), lostImg);
    		fadeOut.setFromValue(1);
    		fadeOut.setToValue(0);
    		fadeOut.setDelay(Duration.seconds(1));
    		fadeOut.play();
    		fadeOut.setOnFinished(n -> {
    			gamePane.getChildren().remove(lostImg);
    			showGameResult(gameRes, false);
    			animationGame.stop();
			});
		});
    }
    
    private void winGameHandler(JSONObject json_data) {
    	JSONArray gameRes = (JSONArray) json_data.get("game_result");
    	
    	ImageView winImg = new ImageView(Resource.WIN_GAME_PATH);
    	winImg.setLayoutX(GAME_WIDTH/2 - 110);
    	winImg.setLayoutY(GAME_HEIGHT/2 - 60);
    	winImg.setScaleX(0);
    	winImg.setScaleY(0);
    	gamePane.getChildren().add(winImg);
    	closeConnections();
    	
    	ScaleTransition transition = new ScaleTransition(Duration.seconds(2), winImg);
    	transition.setToX(1.7);
    	transition.setToY(1.7);
    	transition.play();
    	
    	transition.setOnFinished(e -> {
    		FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), winImg);
    		fadeOut.setFromValue(1);
    		fadeOut.setToValue(0);
    		fadeOut.setDelay(Duration.seconds(1));
    		fadeOut.play();
    		fadeOut.setOnFinished(n -> {
    			gamePane.getChildren().remove(winImg);
    			showGameResult(gameRes, true);
    			animationGame.stop();
			});
    	});
    }
    
    private void showLevel(JSONObject json_data) {
    	int level = (int) (long) json_data.get("level");
    	
    	ImageView levelImg = new ImageView(Resource.LEVEL_PATH[level-1]);
    	levelImg.setLayoutX(GAME_WIDTH/2 - 140);
    	levelImg.setLayoutY(GAME_HEIGHT/2 - 60);
    	levelImg.setScaleX(0);
    	levelImg.setScaleY(0);
    	gamePane.getChildren().add(levelImg);
    	
    	ScaleTransition transition = new ScaleTransition(Duration.seconds(5), levelImg);
    	transition.setToX(1.7);
    	transition.setToY(1.7);
    	transition.play();
    	
    	transition.setOnFinished(e -> {
    		FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), levelImg);
    		fadeOut.setFromValue(1);
    		fadeOut.setToValue(0);
    		fadeOut.setDelay(Duration.seconds(1));
    		fadeOut.play();
    		fadeOut.setOnFinished(n -> gamePane.getChildren().remove(levelImg));
    	});  
    }
    
    private void updateScore(JSONObject json_data) {
    	int score = (int) (long) json_data.get("score");
    	String content = "POINTS: " + score;
    	pointsLabel.setText(content);
    }
    
    private void setGameElements(JSONObject json_data) {
    	JSONArray eArr = (JSONArray) json_data.get("data");
    	
    	/**
    	 * eArr - containing information about each elements 
    	 * eArr[i] - [<id>:long, <layoutX>:double, <layoutY>:double, <speed>:long, <color position>:long, <kind position>:long,
    	 * 				 <skill>:long, <bulletDelay>:long, <blood>:long]
    	 * */
    	for (int i = 0; i < eArr.size(); ++i) {
    		JSONArray e = (JSONArray) eArr.get(i);
    		
    		int id = (int) (long) 		e.get(0);
    		double layoutX = (double) 	e.get(1);
    		double layoutY = (double) 	e.get(2);
    		int speed = (int) (long) 	e.get(3);
    		int color = (int) (long)	e.get(4);
    		int kind  = (int) (long)	e.get(5);
    		int skill = (int) (long)	e.get(6);
    		int delayY = (int) (long) 	e.get(7);
    		int blood = (int) (long) 	e.get(8);
    		
    		Ship enemy = new Ship(Resource.ENEMY_PATH[color][kind], EType.ENEMY, layoutX, layoutY, 
    				Resource.ENEMY_RADIUS, Resource.ENEMY_WIDTH, Resource.ENEMY_HEIGHT, id, speed, skill, blood);
    		enemy.setBulletType(EType.ENEMY_BULLET);
    		enemy.setBulletDelay(delayY);
    		gamePane.getChildren().add(enemy);
    	}
    	
    }
    
    
    private void createUDPHandler() {
    	udpHandler = new UDPClient(data -> {
    		Platform.runLater(() -> {
    			JSONObject json_data = (JSONObject) JSONValue.parse(data);
        		
    			if (json_data != null) {
        			long udp_code = (long) json_data.get("udp_code");
            		
            		switch ((int)udp_code) {
    	        		case ServerCode.SHIP_ACTION_RES:
    	        			shipHandler(json_data);
    	        			break;
            		}
        		}
        		
    		});
    	});
    	
    	udpHandler.start();
    }
    
    private void shipHandler(JSONObject response) {
    	JSONArray data = (JSONArray) response.get("payload");
    	
    	int shipId = (int) (long) data.get(0);
    	double layoutX = (double) data.get(1);
    	int action = (int) (long) data.get(2);
    	
    	switch (action) {
	    	case Action.MOVE_LEFT:
	    		ships[shipId].moveLeft(0);
	    		break;
	    		
	    	case Action.MOVE_RIGHT:
	    		ships[shipId].moveRight(GAME_WIDTH);
	    		break;
	    		
	    	case Action.NONE:
	    		ships[shipId].setLayoutX(layoutX);
	    		ships[shipId].goStraight();
	    		break;
	    		
	    	case Action.SHOOT:
	    		ships[shipId].setLayoutX(layoutX);
	    		gamePane.getChildren().add(ships[shipId].shoot());
	    		break;
    	}
    }
    /*======================== end handle ========================*/
}