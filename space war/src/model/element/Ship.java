package model.element;

import javafx.scene.image.ImageView;

public class Ship extends ImageView {
    public static final int WIDTH = 99;
    public static final int HEIGHT = 75;
    private Bullet bullet;
    private int angle;

    public Ship(String url) {
        super(url);
    }

    public Ship(String url, double posX, double posY) {
        super(url);
        setLayoutX(posX);
        setLayoutY(posY);
    }

    public void moveLeft(int limitLeft) {
        if (angle > -30) {
            angle -= 5;
        }

        setRotate((double)angle);
        if (getLayoutX() > (double)limitLeft) {
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
        if (getLayoutX() < (limitRight - 99)) {
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

        setRotate((double)angle);
    }
}
