package view;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.element.Bullet;
import model.element.Element;
import model.element.Meteor;
import model.element.Ship;

public class GamePlay {
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 850;
    
    private static final String BACKGROUND_URL = "/view/resources/purple.png";
    private static final String[] METEOR_IMAGES = {"/view/resources/meteorite/meteor_grey_big2.png", "/view/resources/meteorite/meteor_brown.png", 
    		"/view/resources/meteorite/meteor_grey.png", "/view/resources/meteorite/meteor_brown_big3.png"};
    
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;
    
    private GridPane gridPane1;
    private GridPane gridPane2;
    
    private Ship ship;
    
    private boolean isLeftPressed;
    private boolean isRightPressed;
    
    private AnimationTimer animationGame;
    
    private Meteor[] brownMeteors;
    private Meteor[] greyMeteors;
    private Random randomInt;

    public GamePlay() {
        initializeStage();
        createKeyListeners();
        randomInt = new Random();
    }

    private void createKeyListeners() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
           
        	public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.LEFT) {
                    isLeftPressed = true;
                    
                } else if (event.getCode() == KeyCode.RIGHT) {
                    isRightPressed = true;
                    
                } else if (event.getCode() == KeyCode.SPACE) {
                	shoot();
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
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    public Stage getGameStage() {
        return gameStage;
    }

    public void createNewGame(Stage mainStage, Ship ship) {
        menuStage = mainStage;
        menuStage.hide();
        createBackground();
        createShip(ship);
        createGameElement();
        createGameLoop();
        gameStage.show();
    }

    private void createShip(Ship choosenShip) {
        ship = choosenShip;
        ship.setLayoutX(GAME_WIDTH / 2);
        ship.setLayoutY(GAME_HEIGHT - Ship.HEIGHT - 10);
        gamePane.getChildren().add(ship);
    }

    private List<Element> elements() {
    	return gamePane.getChildren().stream().filter(n -> n instanceof Element).map(n -> (Element) n).collect(Collectors.toList());
    }

    private void shoot() {
    	Bullet bullet = new Bullet("", "playerbullet", ship.getLayoutX() + 44, ship.getLayoutY());
    	gamePane.getChildren().add(bullet);
    }
    
    private void war() {
    	elements().forEach(e -> {
    		switch (e.getType()) {
    			
    			case "playerbullet":
    				Bullet b = (Bullet) e;
    				b.moveUp(5);
    				
    				if (b.getLayoutY() < 0) b.dead(true);
    				elements().stream().filter(el -> el.getType().contains("enemy")).forEach(enemy -> {
    					if (b.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
    						b.dead(true);
    						enemy.dead(true);
    					}
    				});
    				
    				break;
    				
    			case "enemybullet":
    				// handle
    				break;
				
    			case "enemymeteor":
    				//handle
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

    private void createGameLoop() {
        
    	animationGame = new AnimationTimer() {
            public void handle(long now) {
            	moveShip();
                moveBackground();
                moveElement();
                war();
            }
        };
        
        animationGame.start();
    }

    private void createGameElement() {
        brownMeteors = new Meteor[5];

        for(int i = 0; i < brownMeteors.length; ++i) {
           
        	brownMeteors[i] = new Meteor(METEOR_IMAGES[randomInt.nextInt(4)], "enemymeteor", 
            		40 + randomInt.nextInt(1160), -randomInt.nextInt(2000), 
            		2 + randomInt.nextInt(5), 5 + randomInt.nextInt(4));
           
            gamePane.getChildren().add(brownMeteors[i]);
        }

        greyMeteors = new Meteor[5];

        for(int i = 0; i < greyMeteors.length; ++i) {
            
        	greyMeteors[i] = new Meteor(METEOR_IMAGES[randomInt.nextInt(4)], "enemymeteor",
            		40 + randomInt.nextInt(1160), -randomInt.nextInt(2000), 
            		2 + randomInt.nextInt(2), 5 + randomInt.nextInt(4));
            
            gamePane.getChildren().add(greyMeteors[i]);
        }

    }

    private void checkElementDisappear(Meteor meteor) {
    	
        if (meteor.getLayoutY() > GAME_HEIGHT) {
            meteor.setLayoutY(-randomInt.nextInt(2000));
            meteor.setLayoutX(40 + randomInt.nextInt(1160));
        }
    }

    private void moveElement() {
        int i;
        for(i = 0; i < brownMeteors.length; ++i) {
            brownMeteors[i].move();
            checkElementDisappear(brownMeteors[i]);
        }

        for(i = 0; i < greyMeteors.length; ++i) {
            greyMeteors[i].move();
            checkElementDisappear(greyMeteors[i]);
        }

    }

    private void moveShip() {
        if (isLeftPressed && !isRightPressed) {
            ship.moveLeft(0);
        }

        if (!isLeftPressed && isRightPressed) {
            ship.moveRight(GAME_WIDTH);
        }

        if (!isLeftPressed && !isRightPressed) {
            ship.goStraight();
        }

        if (isLeftPressed && isRightPressed) {
            ship.goStraight();
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
}