package model.element;

public class Meteor extends Element {
    private int angle;
    private int speed;

    public Meteor(String url, String type, double radius, double width, double height) {
        super(url, type, radius, width, height);
    }

    public Meteor(String url, String type, double posX, double posY, double radius, double width, double height) {
        super(url, type, radius, width, height);
        setLayoutX(posX);
        setLayoutY(posY);
    }

    public Meteor(String url, String type, double posX, double posY, int angle, int speed, double radius, double width, double height) {
        super(url, type, radius, width, height);
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