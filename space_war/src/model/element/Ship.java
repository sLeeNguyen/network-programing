package model.element;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class Ship extends ImageView {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 75;
    
    
    private Bullet[] bullets;
    private int b;
    
    private int angle;
    
    public Ship(String urlShip) {
    	super(urlShip);
    }

    public Ship(String urlShip, String urlBullet, double posX, double posY) {
        super(urlShip);
        setLayoutX(posX);
        setLayoutY(posY);
    }
    
    private void createBullets(String urlBullet) {
    	bullets = new Bullet[10];
    	for (int i = 0; i < bullets.length; ++i) {
    		bullets[i] = new Bullet(urlBullet, getLayoutX() + WIDTH / 2, getLayoutY());
    	}
    }
    
    public void shoot(AnchorPane root, int limit) {
    	Bullet bullet = bullets[b];
    	b = (b + 1) % bullets.length;
    	
    	root.getChildren().add(bullets[b]);
    	
//    	while (bullet.getLayoutY() > -37) {
//    		bullet.move(5);
//    	}
//    	bullet.setLayoutX(getLayoutX() + WIDTH / 2);
//		bullet.setLayoutY(getLayoutY());
//		root.getChildren().remove(bullet);
    }

    public void moveLeft(int limitLeft) {
        if (angle > -30) {
            angle -= 5;
        }
        setRotate(angle);
        
        if (getLayoutX() > limitLeft) {
            if (angle > -20) {
                setLayoutX(getLayoutX() - 3);
            } else {
                setLayoutX(getLayoutX() - 5);
            }
        }
    }

    public void moveRight(int limitRight) {
        if (angle < 30) {
            angle += 5;
        }
        setRotate(angle);
        
        if (getLayoutX() < (limitRight - Ship.WIDTH)) {
            if (angle < 20) {
                setLayoutX(getLayoutX() + 3);
            } else {
                setLayoutX(getLayoutX() + 5);
            }
        }
    }

    public void moveUp() {
    }

    public void moveDown() {
    }

    public void goStraight() {
        if (angle < 0) {
            angle += 5;
        }

        if (angle > 0) {
            angle -= 5;
        }

        setRotate(angle);
    }
}
