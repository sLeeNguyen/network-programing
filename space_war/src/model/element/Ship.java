package model.element;

public class Ship extends Element {
    
    private int angle;
    
    public Ship(String urlShip, String type, double radius, double width, double height) {
    	super(urlShip, type, radius, width, height);
    }

    public Ship(String urlShip, String type, double posX, double posY, double radius, double width, double height) {
        super(urlShip, type, radius, width, height);
        setLayoutX(posX);
        setLayoutY(posY);
    }

    public void moveLeft(double limitLeft) {
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

    public void moveRight(double limitRight) {
        if (angle < 30) {
            angle += 5;
        }
        setRotate(angle);
        
        if (getLayoutX() < (limitRight - getWidth())) {
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
