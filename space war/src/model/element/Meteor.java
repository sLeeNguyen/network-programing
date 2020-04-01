package model.element;

import javafx.scene.image.ImageView;

public class Meteor extends ImageView {
    private int angle;
    private int speed;

    public Meteor(String url) {
        super(url);
    }

    public Meteor(String url, double posX, double posY) {
        super(url);
        setLayoutX(posX);
        setLayoutY(posY);
    }

    public Meteor(String url, double posX, double posY, int angle, int speed) {
        super(url);
        setLayoutX(posX);
        setLayoutY(posY);
        this.angle = angle;
        this.speed = speed;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void move() {
        setLayoutY(getLayoutY() + speed);
        setRotate(getRotate() + angle);
    }
}