package model.element;

import helpers.Resource;
import model.SHIP;

public class Ship extends Element {
	public static final int SHOOT = 1<<0;
	public static final int SHIELD = 1<<1;
    
    private int angle;
    private SHIP shipE;
    private int normalSpeed;
    private int maxSpeed;
    private int bulletType;
    private int skills;
    private int bulletDelay;
    private int blood;
    
    private int numShields;
    private Shield shield;
    private int preBulletY;
    
    public Ship(SHIP shipE, int type, double radius, double width, double height, long id) {
    	super(shipE.getUrlShip(), type, radius, width, height, (int)id);
    	this.shipE = shipE;
    	this.skills = SHOOT | SHIELD;
    	this.blood = 1;
    	this.numShields = 100;
    	this.normalSpeed = 5;
    	this.maxSpeed = 7;
    }

    public Ship(String urlShip, int type,  double posX, double posY, double radius, double width, double height, int id, int normalSpeed, int skills, int blood) {
        super(urlShip, type, radius, width, height, id);
        this.normalSpeed = normalSpeed;
        this.skills = skills;
        this.blood = blood;
        setLayoutX(posX);
        setLayoutY(posY);
        if ((skills & SHIELD) != 0) numShields = 1;
    }
    
    public void setBulletType(int type) {
    	this.bulletType = type;
    }
    
    public void setBulletDelay(int delayY) {
    	this.preBulletY = -delayY;
    	this.bulletDelay = delayY;
    }
   
    public boolean moveLeft(double limitLeft) {
        if (angle > -30) {
            angle -= 5;
        }
        setRotate(angle);
        
        if (getLayoutX() > limitLeft) {
            if (angle > -20) {
                setLayoutX(getLayoutX() - normalSpeed);
                if (shield != null) shield.setLayoutX(getLayoutX() - normalSpeed); 
            } else {
                setLayoutX(getLayoutX() - maxSpeed);
                if (shield != null) shield.setLayoutX(getLayoutX() - maxSpeed);
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
                if (shield != null) shield.setLayoutX(getLayoutX() + normalSpeed); 
            } else {
                setLayoutX(getLayoutX() + maxSpeed);
                if (shield != null) shield.setLayoutX(getLayoutX() + maxSpeed);
            }
            return true;
        }
        
        return false;
    }

    public void moveUp() {
    }

    public void moveDown() {
    	if (getType() == EType.ENEMY) {
    		setLayoutY(getLayoutY() + normalSpeed);
    		if (shield != null) shield.setLayoutY(getLayoutY() + normalSpeed);
    	}
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
    	Bullet b;
    	if (getType() == EType.ENEMY) {
    		b = new Bullet(Resource.ENEMY_BULLET_PATH, bulletType, getLayoutX() + 44, getLayoutY() + 90, 7, 14, 31);
    	} else {
    		b = new Bullet(shipE.getUrlBullet(), bulletType, getLayoutX() + 44, getLayoutY(), 8, 13, 37);
    	}
    	
    	return b;
    }
    
    public boolean checkShoot() {
    	if ((skills & SHOOT) != 0 && getType() == EType.ENEMY && getLayoutY() >= preBulletY + bulletDelay) {
    		preBulletY += bulletDelay;
    		return true;
    	}
    	
    	return false;
    }
    
	public SHIP getShipE() {
		return shipE;
	}

	public boolean hasSkill(int skill) {
		return (skills & skill) != 0 ? true : false;
	}
	
	public Shield useShield() {
		if (numShields <= 0) return null;
		numShields--;
		if (getType() == EType.ENEMY) {
			shield = new Shield(Resource.ENEMY_SHIELD_PATH, getLayoutX()  - 20, getLayoutY() + 30);
		}
		else {
			shield = new Shield(Resource.PLAYER_SHIELD_PATH, getLayoutX() - 25, getLayoutY() - 30);
		}
		
		return shield;
	}
	
	public void deleteShield() {
		if (shield != null) shield.dead(true);
		this.shield = null;
	}
	
	public int decreaseBlood() {
		blood--;
		return blood;
	}
}
