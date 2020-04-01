package model.element;

import javafx.scene.image.ImageView;

public class Bullet extends ImageView {
	private static final String LASER_BLUE_PATH = "/view/resources/lasers/laser_blue06.png";
	public static final int HEIGHT = 37;
	public static final int WIDTH = 17;
	
	public Bullet(String url) {
		super(LASER_BLUE_PATH);
	}
	
	public Bullet(String url, double posX, double posY) {
		super(LASER_BLUE_PATH);
		setLayoutX(posX);
		setLayoutY(posY);
	}
	
	public void move(double y) {
		setLayoutY(getLayoutY() - y); 
	}
}
