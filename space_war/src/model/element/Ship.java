package model.element;

import model.SHIP;

public class Ship extends Element {
    
    private int angle;
    private SHIP shipE;
    private int normalSpeed;
    private int maxSpeed;
    private int bulletType;
    
    public Ship(SHIP shipE, int type, double radius, double width, double height, long id) {
    	super(shipE.getUrlShip(), type, radius, width, height, (int)id);
    	this.shipE = shipE;
    	this.normalSpeed = 5;
    	this.maxSpeed = 7;
    	this.bulletType = getType() == EType.YOU ? EType.YOUR_BULLET : EType.PLAYER_BULLET;
    }
    
    public Ship(String urlShip, int type, double radius, double width, double height, long id) {
        super(urlShip, type, radius, width, height, (int) id);
        this.normalSpeed = 5;
    	this.maxSpeed = 7;
    }

    public Ship(String urlShip, int type,  double posX, double posY, double radius, double width, double height, int id, int normalSpeed) {
        super(urlShip, type, radius, width, height, id);
        this.normalSpeed = normalSpeed;
        setLayoutX(posX);
        setLayoutY(posY);
    }

    public boolean moveLeft(double limitLeft) {
        if (angle > -30) {
            angle -= 5;
        }
        setRotate(angle);
        
        if (getLayoutX() > limitLeft) {
            if (angle > -20) {
                setLayoutX(getLayoutX() - normalSpeed);
            } else {
                setLayoutX(getLayoutX() - maxSpeed);
            }
            return true;
        }
        
        return false;
    }

    public boolean moveRight(double limitRight) {
        if (angle < 30) {
            angle += 5;
        }
        setRotate(angle);
        
        if (getLayoutX() < (limitRight - getWidth())) {
            if (angle < 20) {
                setLayoutX(getLayoutX() + normalSpeed);
            } else {
                setLayoutX(getLayoutX() + maxSpeed);
            }
            return true;
        }
        
        return false;
    }

    public void moveUp() {
    }

    public void moveDown() {
    	if (getType() == EType.ENEMY) setLayoutY(getLayoutY() + normalSpeed);
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

    public Bullet shoot() {
    	return new Bullet(shipE.getUrlBullet(), bulletType, getLayoutX() + 44, getLayoutY(), 8, 13, 37);
    }
    
	public SHIP getShipE() {
		return shipE;
	}
}
